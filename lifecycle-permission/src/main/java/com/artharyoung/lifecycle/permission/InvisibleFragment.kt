package com.artharyoung.lifecycle.permission

import android.content.Intent
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.*

internal class InvisibleFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE = 100
        const val SETTING_CODE = 120
    }

    private val channel by lazy {
        Channel<HashMap<String, PermissionState>>(Channel.CONFLATED)
    }

    private var activityResult = {}

    fun requestPermissions(permissions: Array<out String>): LiveData<HashMap<String, PermissionState>> {
        requestPermissions(permissions, REQUEST_CODE)
        return liveData { emit(channel.receive()) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE) return

        val result = hashMapOf<String, PermissionState>()

        for ((index, value) in grantResults.withIndex()) {
            when (value) {
                PackageManager.PERMISSION_GRANTED -> result[permissions[index]] = PermissionState.Granted
                else -> {
                    val permission = permissions[index]
                    result[permission] = if (shouldShowRequestPermissionRationale(permission)) {
                        PermissionState.Rationale
                    } else {
                        PermissionState.Denied
                    }
                }
            }
        }

        lifecycleScope.launch { channel.send(result) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != SETTING_CODE) return
        activityResult.invoke()
    }

    fun setActivityResult(block: () -> Unit) {
        activityResult = block
    }
}