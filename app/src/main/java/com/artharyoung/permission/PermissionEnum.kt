package com.artharyoung.permission

import android.Manifest

@Suppress("unused")
enum class PermissionEnum(val permission: String, val icon: Int, val title: Int, val content: Int, val toast: Int) {
    CAMERA(Manifest.permission.CAMERA, R.mipmap.ic_permission_tip_camera, R.string.tip_permission_title_camera, R.string.tip_permission_content_camera, R.string.permission_camera_denied_tip),

    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.mipmap.ic_permission_tip_storage, R.string.tip_permission_title_storage, R.string.tip_permission_content_storage, R.string.permission_external_sdcard_denied_tip),

    ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION, R.mipmap.ic_permission_tip_location, R.string.tip_permission_title_location, R.string.tip_permission_content_location, R.string.permission_location_rationale_denied_tip),
    ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION, R.mipmap.ic_permission_tip_location, R.string.tip_permission_title_location, R.string.tip_permission_content_location, R.string.permission_location_rationale_denied_tip),

    RECORD_AUDIO(Manifest.permission.RECORD_AUDIO, R.mipmap.ic_permission_tip_microphone, R.string.tip_permission_title_microphone, R.string.tip_permission_content_microphone, R.string.permission_record_audio_denied_tip),

    CALL_PHONE(Manifest.permission.CALL_PHONE, R.mipmap.ic_permission_tip_telephone, R.string.tip_permission_title_telephone, R.string.tip_permission_content_telephone, R.string.permission_call_phone_denied_tip),
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE, R.mipmap.ic_permission_tip_telephone, R.string.tip_permission_title_telephone, R.string.tip_permission_content_telephone, R.string.permission_call_phone_denied_tip),

    SEND_SMS(Manifest.permission.SEND_SMS, R.mipmap.ic_permission_tip_message, R.string.tip_permission_title_message, R.string.tip_permission_content_message, R.string.permission_sms_denied_tip)
}

fun getPermissionTips(permission: String): PermissionEnum {
    for (entry in PermissionEnum.values()) {
        if (permission == entry.permission) {
            return entry
        }
    }
    throw Exception("add permission tips in PermissionEnum class")
}

fun getPermissionToast(permission: String): Int {
    for (entry in PermissionEnum.values()) {
        if (permission == entry.permission) {
            return entry.toast
        }
    }
    throw Exception("add permission tips in PermissionEnum class")
}
