package com.app.cookbook.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.app.cookbook.R
import com.app.cookbook.constant.GlobalFunction.showToastMessage
import com.app.cookbook.constant.GlobalFunction.startActivity
import com.app.cookbook.databinding.ActivityLogInBinding
import com.app.cookbook.model.User
import com.app.cookbook.prefs.DataStoreManager
import com.app.cookbook.utils.StringUtil.isEmpty
import com.app.cookbook.utils.StringUtil.isValidEmail
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {

    private var mActivityLogInBinding: ActivityLogInBinding? = null
    private var isEnableButtonLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityLogInBinding = ActivityLogInBinding.inflate(
            layoutInflater
        )
        setContentView(mActivityLogInBinding!!.root)
        initListener()
    }

    private fun initListener() {
        mActivityLogInBinding!!.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    mActivityLogInBinding!!.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_main)
                } else {
                    mActivityLogInBinding!!.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray)
                }
                val strPassword =
                    mActivityLogInBinding!!.edtPassword.text.toString().trim { it <= ' ' }
                if (!isEmpty(s.toString()) && !isEmpty(strPassword)) {
                    isEnableButtonLogin = true
                    mActivityLogInBinding!!.btnLogin.setBackgroundResource(R.drawable.bg_button_enable_corner_10)
                } else {
                    isEnableButtonLogin = false
                    mActivityLogInBinding!!.btnLogin.setBackgroundResource(R.drawable.bg_button_disable_corner_10)
                }
            }
        })
        mActivityLogInBinding!!.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!isEmpty(s.toString())) {
                    mActivityLogInBinding!!.edtPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main)
                } else {
                    mActivityLogInBinding!!.edtPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray)
                }
                val strEmail = mActivityLogInBinding!!.edtEmail.text.toString().trim { it <= ' ' }
                if (!isEmpty(s.toString()) && !isEmpty(strEmail)) {
                    isEnableButtonLogin = true
                    mActivityLogInBinding!!.btnLogin.setBackgroundResource(R.drawable.bg_button_enable_corner_10)
                } else {
                    isEnableButtonLogin = false
                    mActivityLogInBinding!!.btnLogin.setBackgroundResource(R.drawable.bg_button_disable_corner_10)
                }
            }
        })
        mActivityLogInBinding!!.layoutRegister.setOnClickListener {
            startActivity(
                this,
                RegisterActivity::class.java
            )
        }
        mActivityLogInBinding!!.btnLogin.setOnClickListener { onClickValidateLogin() }
        mActivityLogInBinding!!.tvForgotPassword.setOnClickListener {
            startActivity(
                this,
                ForgotPasswordActivity::class.java
            )
        }
    }

    private fun onClickValidateLogin() {
        if (!isEnableButtonLogin) return
        val strEmail = mActivityLogInBinding!!.edtEmail.text.toString().trim { it <= ' ' }
        val strPassword = mActivityLogInBinding!!.edtPassword.text.toString().trim { it <= ' ' }
        if (isEmpty(strEmail)) {
            showToastMessage(this, getString(R.string.msg_email_require))
        } else if (isEmpty(strPassword)) {
            showToastMessage(this, getString(R.string.msg_password_require))
        } else if (!isValidEmail(strEmail)) {
            showToastMessage(this, getString(R.string.msg_email_invalid))
        } else {
            loginUserFirebase(strEmail, strPassword)
        }
    }

    private fun loginUserFirebase(email: String, password: String) {
        showProgressDialog(true)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        val userObject = User(user.email, password)
                        DataStoreManager.user = userObject
                        startActivity(this@LoginActivity, MainActivity::class.java)
                        finishAffinity()
                    }
                } else {
                    showToastMessage(this, getString(R.string.msg_login_error))
                }
            }
    }
}