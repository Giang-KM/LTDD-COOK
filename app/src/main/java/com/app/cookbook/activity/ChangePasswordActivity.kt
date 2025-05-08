package com.app.cookbook.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.app.cookbook.R
import com.app.cookbook.constant.GlobalFunction.showToastMessage
import com.app.cookbook.databinding.ActivityChangePasswordBinding
import com.app.cookbook.prefs.DataStoreManager
import com.app.cookbook.prefs.DataStoreManager.Companion.user
import com.app.cookbook.utils.StringUtil.isEmpty
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : BaseActivity() {

    private var mBinding: ActivityChangePasswordBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        initToolbar()
        initListener()
    }

    private fun initToolbar() {
        mBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mBinding!!.layoutToolbar.tvToolbarTitle.text = getString(R.string.label_change_password)
    }

    private fun initListener() {
        mBinding!!.edtOldPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    mBinding!!.edtOldPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main)
                } else {
                    mBinding!!.edtOldPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray)
                }
            }
        })
        mBinding!!.edtNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    mBinding!!.edtNewPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main)
                } else {
                    mBinding!!.edtNewPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray)
                }
            }
        })
        mBinding!!.edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    mBinding!!.edtConfirmPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main)
                } else {
                    mBinding!!.edtConfirmPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray)
                }
            }
        })
        mBinding!!.btnChangePassword.setOnClickListener { onClickValidateChangePassword() }
    }

    private fun onClickValidateChangePassword() {
        val strOldPassword = mBinding!!.edtOldPassword.text.toString().trim { it <= ' ' }
        val strNewPassword = mBinding!!.edtNewPassword.text.toString().trim { it <= ' ' }
        val strConfirmPassword = mBinding!!.edtConfirmPassword.text.toString().trim { it <= ' ' }
        if (isEmpty(strOldPassword)) {
            showToastMessage(this, getString(R.string.msg_old_password_require))
        } else if (isEmpty(strNewPassword)) {
            showToastMessage(this, getString(R.string.msg_new_password_require))
        } else if (isEmpty(strConfirmPassword)) {
            showToastMessage(this, getString(R.string.msg_confirm_password_require))
        } else if (user!!.password != strOldPassword) {
            showToastMessage(this, getString(R.string.msg_old_password_invalid))
        } else if (strNewPassword != strConfirmPassword) {
            showToastMessage(this, getString(R.string.msg_confirm_password_invalid))
        } else if (strOldPassword == strNewPassword) {
            showToastMessage(this, getString(R.string.msg_new_password_invalid))
        } else {
            changePassword(strNewPassword)
        }
    }

    private fun changePassword(newPassword: String) {
        showProgressDialog(true)
        val user = FirebaseAuth.getInstance().currentUser ?: return
        user.updatePassword(newPassword)
            .addOnCompleteListener { task: Task<Void?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    showToastMessage(this, getString(R.string.msg_change_password_successfully))
                    val userLogin = DataStoreManager.user
                    userLogin!!.password = newPassword
                    DataStoreManager.user = userLogin
                    mBinding!!.edtOldPassword.setText("")
                    mBinding!!.edtNewPassword.setText("")
                    mBinding!!.edtConfirmPassword.setText("")
                } else {
                    showToastMessage(this, getString(R.string.msg_change_password_fail))
                }
            }
    }
}