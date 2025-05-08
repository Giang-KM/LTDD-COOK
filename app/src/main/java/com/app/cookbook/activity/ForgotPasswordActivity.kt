package com.app.cookbook.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.app.cookbook.R
import com.app.cookbook.databinding.ActivityForgotPasswordBinding
import com.app.cookbook.utils.StringUtil.isEmpty
import com.app.cookbook.utils.StringUtil.isValidEmail
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {

    private var mActivityForgotPasswordBinding: ActivityForgotPasswordBinding? = null
    private var isEnableButtonResetPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(
            layoutInflater
        )
        setContentView(mActivityForgotPasswordBinding!!.root)
        initToolbar()
        initListener()
    }

    private fun initToolbar() {
        mActivityForgotPasswordBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mActivityForgotPasswordBinding!!.layoutToolbar.tvToolbarTitle.text =
            getString(R.string.label_reset_password)
    }

    private fun initListener() {
        mActivityForgotPasswordBinding!!.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    mActivityForgotPasswordBinding!!.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_main)
                } else {
                    mActivityForgotPasswordBinding!!.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray)
                }
                if (!isEmpty(s.toString())) {
                    isEnableButtonResetPassword = true
                    mActivityForgotPasswordBinding!!.btnResetPassword.setBackgroundResource(R.drawable.bg_button_enable_corner_10)
                } else {
                    isEnableButtonResetPassword = false
                    mActivityForgotPasswordBinding!!.btnResetPassword.setBackgroundResource(R.drawable.bg_button_disable_corner_10)
                }
            }
        })
        mActivityForgotPasswordBinding!!.btnResetPassword.setOnClickListener { onClickValidateResetPassword() }
    }

    private fun onClickValidateResetPassword() {
        if (!isEnableButtonResetPassword) return
        val strEmail = mActivityForgotPasswordBinding!!.edtEmail.text.toString().trim { it <= ' ' }
        if (isEmpty(strEmail)) {
            Toast.makeText(
                this@ForgotPasswordActivity,
                getString(R.string.msg_email_require), Toast.LENGTH_SHORT
            ).show()
        } else if (!isValidEmail(strEmail)) {
            Toast.makeText(
                this@ForgotPasswordActivity,
                getString(R.string.msg_email_invalid), Toast.LENGTH_SHORT
            ).show()
        } else {
            resetPassword(strEmail)
        }
    }

    private fun resetPassword(email: String) {
        showProgressDialog(true)
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task: Task<Void?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        getString(R.string.msg_reset_password_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    mActivityForgotPasswordBinding!!.edtEmail.setText("")
                }
            }
    }
}