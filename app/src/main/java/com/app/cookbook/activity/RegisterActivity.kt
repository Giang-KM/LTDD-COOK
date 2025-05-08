package com.app.cookbook.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.app.cookbook.R
import com.app.cookbook.constant.GlobalFunction.showToastMessage
import com.app.cookbook.constant.GlobalFunction.startActivity
import com.app.cookbook.databinding.ActivityRegisterBinding
import com.app.cookbook.model.User
import com.app.cookbook.prefs.DataStoreManager
import com.app.cookbook.utils.StringUtil.isEmpty
import com.app.cookbook.utils.StringUtil.isValidEmail
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : BaseActivity() {

    private var mActivityRegisterBinding: ActivityRegisterBinding? = null
    private var isEnableButtonRegister = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityRegisterBinding = ActivityRegisterBinding.inflate(
            layoutInflater
        )
        setContentView(mActivityRegisterBinding!!.root)
        initListener()
    }

    private fun initListener() {
        mActivityRegisterBinding!!.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    mActivityRegisterBinding!!.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_main)
                } else {
                    mActivityRegisterBinding!!.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray)
                }
                val strPassword =
                    mActivityRegisterBinding!!.edtPassword.text.toString().trim { it <= ' ' }
                if (!isEmpty(s.toString()) && !isEmpty(strPassword)) {
                    isEnableButtonRegister = true
                    mActivityRegisterBinding!!.btnRegister.setBackgroundResource(R.drawable.bg_button_enable_corner_10)
                } else {
                    isEnableButtonRegister = false
                    mActivityRegisterBinding!!.btnRegister.setBackgroundResource(R.drawable.bg_button_disable_corner_10)
                }
            }
        })
        mActivityRegisterBinding!!.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    mActivityRegisterBinding!!.edtPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main)
                } else {
                    mActivityRegisterBinding!!.edtPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray)
                }
                val strEmail =
                    mActivityRegisterBinding!!.edtEmail.text.toString().trim { it <= ' ' }
                if (!isEmpty(s.toString()) && !isEmpty(strEmail)) {
                    isEnableButtonRegister = true
                    mActivityRegisterBinding!!.btnRegister.setBackgroundResource(R.drawable.bg_button_enable_corner_10)
                } else {
                    isEnableButtonRegister = false
                    mActivityRegisterBinding!!.btnRegister.setBackgroundResource(R.drawable.bg_button_disable_corner_10)
                }
            }
        })
        mActivityRegisterBinding!!.layoutLogin.setOnClickListener { finish() }
        mActivityRegisterBinding!!.btnRegister.setOnClickListener { onClickValidateRegister() }
    }

    private fun onClickValidateRegister() {
        if (!isEnableButtonRegister) return
        val strEmail = mActivityRegisterBinding!!.edtEmail.text.toString().trim { it <= ' ' }
        val strPassword = mActivityRegisterBinding!!.edtPassword.text.toString().trim { it <= ' ' }
        if (isEmpty(strEmail)) {
            showToastMessage(this, getString(R.string.msg_email_require))
        } else if (isEmpty(strPassword)) {
            showToastMessage(this, getString(R.string.msg_password_require))
        } else if (!isValidEmail(strEmail)) {
            showToastMessage(this, getString(R.string.msg_email_invalid))
        } else {
            registerUserFirebase(strEmail, strPassword)
        }
    }

    private fun registerUserFirebase(email: String, password: String) {
        showProgressDialog(true)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        val userObject = User(user.email, password)
                        DataStoreManager.user = userObject
                        startActivity(this@RegisterActivity, MainActivity::class.java)
                        finishAffinity()
                    }
                } else {
                    showToastMessage(this, getString(R.string.msg_register_error))
                }
            }
    }
}