package com.artharyoung.lifecycle.permission

sealed class PermissionState {
    object Granted : PermissionState()
    object Denied : PermissionState()
    object Rationale : PermissionState()
}