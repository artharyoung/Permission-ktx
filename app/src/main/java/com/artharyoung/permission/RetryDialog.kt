package com.artharyoung.permission

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_permission_tip.*

class RetryDialog : DialogFragment() {

    private var clickEvent: (Boolean) -> Unit = {}

    companion object {
        const val PERMISSION_TIP = "permission_tip"
        const val PERMISSION_ENFORCE = "permission_enforce"
    }

    private var enforce: Boolean = true

    private var permissionEnum = PermissionEnum.SEND_SMS

    fun setOnChooseListener(block: (Boolean) -> Unit) {
        clickEvent = block
    }

    fun show(manager: FragmentManager) {
        show(manager, "RefuseTipsDialog")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.NoneWindowFullScreenFloatTransparent)
        enforce = requireArguments().getBoolean(PERMISSION_ENFORCE)
        permissionEnum = getPermissionTips(requireArguments().getString(PERMISSION_TIP, ""))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                clickEvent.invoke(false)
                return@setOnKeyListener true
            }
            false
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_permission_tip, null)
    }

    override fun onViewCreated(parent: View, savedInstanceState: Bundle?) {
        super.onViewCreated(parent, savedInstanceState)

        image_permission.setImageResource(permissionEnum.icon)
        text_title_permission.text = getString(permissionEnum.title)
        text_desc_permission.text = getString(permissionEnum.content)
        text_button_quit.apply {
            text = if (enforce) "残忍退出" else "残忍拒绝"
            setOnClickListener {
                dismissAllowingStateLoss()
                clickEvent.invoke(false)
            }
        }
        text_button_confirm.setOnClickListener {
            dismissAllowingStateLoss()
            clickEvent.invoke(true)
        }
    }
}