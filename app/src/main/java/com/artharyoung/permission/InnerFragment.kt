package com.artharyoung.permission

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_inner.*


@Suppress("unused")
class InnerFragment:Fragment(R.layout.fragment_inner) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        click_button.setOnClickListener {

            requestPermission(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.SEND_SMS){

                Toast.makeText(requireContext(), "所有权限请求成功", Toast.LENGTH_SHORT).show()
            }

        }
    }
}