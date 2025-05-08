package com.app.cookbook.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.app.cookbook.constant.AboutUsConfig
import com.app.cookbook.constant.GlobalFunction.startActivity
import com.app.cookbook.databinding.ActivitySplashBinding
import com.app.cookbook.prefs.DataStoreManager.Companion.user
import com.app.cookbook.utils.StringUtil.isEmpty

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var mActivitySplashBinding: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivitySplashBinding = ActivitySplashBinding.inflate(
            layoutInflater
        )
        setContentView(mActivitySplashBinding!!.root)
        initUi()
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ goToActivity() }, 2000)
    }

    private fun initUi() {
        mActivitySplashBinding!!.tvAboutUsTitle.text = AboutUsConfig.ABOUT_US_TITLE
        mActivitySplashBinding!!.tvAboutUsSlogan.text = AboutUsConfig.ABOUT_US_SLOGAN
    }

    private fun goToActivity() {
        if (user != null
            && !isEmpty(user!!.email)
        ) {
            startActivity(this, MainActivity::class.java)
        } else {
            startActivity(this, LoginActivity::class.java)
        }
        finish()
    }
}