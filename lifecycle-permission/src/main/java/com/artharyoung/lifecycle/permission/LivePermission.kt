package com.artharyoung.lifecycle.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class LivePermission {

    companion object {
        const val TAG = "LivePermission"
    }

    private val invisibleFragment by lazy {
        manager.findFragmentByTag(TAG) as? InvisibleFragment ?: InvisibleFragment().run {
            manager.beginTransaction().add(this, TAG).commitNowAllowingStateLoss()
            this@run
        }
    }

    private val manager: FragmentManager
    private val owner: LifecycleOwner
    private val activity: FragmentActivity

    private lateinit var permission: Array<out String>
    private var enforce: Boolean = true
    private lateinit var retry: (Array<out String>, FragmentManager, (Boolean) -> Unit) -> Unit
    private lateinit var error: (Array<out String>) -> Unit
    private lateinit var success: () -> Unit


    constructor(activity: AppCompatActivity) {
        manager = activity.supportFragmentManager
        owner = activity
        this.activity = activity
    }

    constructor(fragment: Fragment) {
        manager = fragment.childFragmentManager
        owner = fragment.viewLifecycleOwner
        this.activity = fragment.requireActivity()
    }

    fun requestPermissions(
        vararg permission: String,
        enforce: Boolean,
        retry: (Array<out String>, FragmentManager, (Boolean) -> Unit) -> Unit,
        error: (Array<out String>) -> Unit,
        success: () -> Unit
    ) {
        require(permission.isNotEmpty())

        this.permission = permission
        this.enforce = enforce
        this.retry = retry
        this.error = error
        this.success = success

        val permissions = hashMapOf<String, PermissionState>()
        permission.forEach {
            permissions[it] =
                if (activity.isPermissionGranted(it)) PermissionState.Granted else PermissionState.Rationale
        }

        if (permissions.isGranted()) {
            try {
                success.invoke()
            } catch (e: Exception) {
                error.invoke(permissions.map { it.key }.toTypedArray())
            }
            return
        }

        val rationale = permissions.getRationale()
        val rationalePermission = rationale.map { it.key }.toTypedArray()

        invisibleFragment.requestPermissions(rationalePermission)
            .observe(owner, Observer { resultPermission ->
                permissions.putAll(resultPermission)

                if (permissions.isGranted()) {
                    try {
                        success.invoke()
                    } catch (e: Exception) {
                        error.invoke(permissions.map { it.key }.toTypedArray())
                    }
                    return@Observer
                }

                val result = permissions.getRationale()
                if (result.isNotEmpty()) {
                    retry.invoke(result.map { it.key }.toTypedArray(), manager) { bol ->
                        when {
                            bol -> requestPermissions(
                                permission = *permission,
                                enforce = enforce,
                                retry = retry,
                                error = error,
                                success = success
                            )
                            enforce -> activity.finish()
                            else -> error.invoke(result.map { it.key }.toTypedArray())
                        }
                    }
                } else {
                    val denied = permissions.getDenied()
                    retry.invoke(denied.map { it.key }.toTypedArray(), manager) { bol ->
                        when {
                            bol -> toSetting(activity)
                            enforce -> activity.finish()
                            else -> error.invoke(denied.map { it.key }.toTypedArray())
                        }
                    }
                }
            })
    }

    private fun toSetting(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }

        invisibleFragment.setActivityResult {
            requestPermissions(
                permission = *permission,
                enforce = enforce,
                retry = retry,
                error = error,
                success = success
            )
        }

        invisibleFragment.startActivityForResult(intent, InvisibleFragment.SETTING_CODE)
    }

    private fun HashMap<String, PermissionState>.getRationale(): Map<String, PermissionState> {
        return this.filter { it.value is PermissionState.Rationale }
    }

    private fun HashMap<String, PermissionState>.getDenied(): Map<String, PermissionState> {
        return this.filter { it.value is PermissionState.Denied }
    }

    private fun HashMap<String, PermissionState>.isGranted(): Boolean {
        val granted = filter { it.value is PermissionState.Granted }
        return granted.size == size
    }

    private fun Context.isPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}