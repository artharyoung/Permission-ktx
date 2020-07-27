package com.artharyoung.permission

import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.artharyoung.lifecycle.permission.LivePermission
import java.io.Serializable

fun AppCompatActivity.requestPermission(
    vararg permission: String,
    enforce: Boolean = true,
    error: () -> Unit = {},
    success: () -> Unit
) {
    LivePermission(this).requestPermissions(
        permission = *permission,
        enforce = enforce,
        retry = { permissions, manager, function ->
            RetryDialog().apply {
                applyExtras(
                    mapOf(
                        RetryDialog.PERMISSION_TIP to permissions.first(),
                        RetryDialog.PERMISSION_ENFORCE to enforce
                    )
                )
                setOnChooseListener { function.invoke(it) }
            }.show(manager)
        },
        error = {
            if (enforce) {
                Toast.makeText(this, getPermissionToast(it.first()), Toast.LENGTH_SHORT).show()
            } else {
                error.invoke()
            }
        },
        success = success
    )
}

fun Fragment.requestPermission(
    vararg permission: String,
    enforce: Boolean = true,
    error: () -> Unit = {},
    success: () -> Unit
) {
    LivePermission(this).requestPermissions(
        permission = *permission,
        enforce = enforce,
        retry = { permissions, manager, function ->
            RetryDialog().apply {
                applyExtras(
                    mapOf(
                        RetryDialog.PERMISSION_TIP to permissions.first(),
                        RetryDialog.PERMISSION_ENFORCE to enforce
                    )
                )
                setOnChooseListener { function.invoke(it) }
            }.show(manager)
        },
        error = {
            if (enforce) {
                Toast.makeText(requireContext(), getPermissionToast(it.first()), Toast.LENGTH_SHORT)
                    .show()
            } else {
                error.invoke()
            }
        },
        success = success
    )
}

fun Fragment.applyExtras(extras: Map<String, Any>) {
    arguments = Bundle().applyExtras(extras)
}

fun Bundle.applyExtras(extras: Map<String, Any>): Bundle {
    extras.keys.forEach { key ->
        when (val value: Any? = extras[key]) {
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            is Parcelable -> putParcelable(key, value)
            is Serializable -> putSerializable(key, value)
            else -> error("can't apply extra $key - unknown type")
        }
    }
    return this
}