package com.app.cookbook.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.app.cookbook.MyApplication
import com.app.cookbook.R
import com.app.cookbook.constant.Constant
import com.app.cookbook.constant.GlobalFunction.showToastMessage
import com.app.cookbook.constant.GlobalFunction.startActivity
import com.app.cookbook.databinding.ActivityFoodDetailBinding
import com.app.cookbook.model.Food
import com.app.cookbook.model.UserInfo
import com.app.cookbook.prefs.DataStoreManager.Companion.user
import com.app.cookbook.utils.StringUtil.isEmpty
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FoodDetailActivity : BaseActivity() {

    private var mBinding: ActivityFoodDetailBinding? = null
    private var mFoodId: Long = 0
    private var mFood: Food? = null
    private var mFoodDetailValueEventListener: ValueEventListener? = null
    private var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFoodDetailBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        loadDataIntent()
        initToolbar()
        initView()
        loadFoodDetailFromFirebase()
    }

    private fun loadDataIntent() {
        val bundle = intent.extras ?: return
        mFoodId = bundle.getLong(Constant.FOOD_ID)
    }

    private fun loadFoodDetailFromFirebase() {
        showProgressDialog(true)
        mFoodDetailValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showProgressDialog(false)
                mFood = snapshot.getValue(Food::class.java)
                if (mFood == null) return
                initData()
            }

            override fun onCancelled(error: DatabaseError) {
                showProgressDialog(false)
                showToastMessage(this@FoodDetailActivity, getString(R.string.msg_get_date_error))
            }
        }
        MyApplication[this].foodDetailDatabaseReference(mFoodId)?.addValueEventListener(mFoodDetailValueEventListener!!)
    }

    private fun initToolbar() {
        mBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mBinding!!.layoutToolbar.tvToolbarTitle.text = getString(R.string.label_food_detail)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        val webSettings = mBinding!!.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.builtInZoomControls = false
        webSettings.setSupportZoom(false)
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        mBinding!!.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webSettings.useWideViewPort = true
        mBinding!!.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        mBinding!!.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showProgressDialog(true)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                showProgressDialog(false)
                isLoaded = true
                addHistory()
                changeCountViewFood()
            }
        }
    }

    private fun initData() {
        if (!isLoaded) {
            loadWebViewFoodDetail()
        }
        mBinding!!.tvRate.text = mFood!!.rate.toString()
        val strCountReview = "(" + mFood!!.countReviews + ")"
        mBinding!!.tvCountReview.text = strCountReview
        mBinding!!.layoutRatingAndReview.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(Constant.FOOD_ID, mFoodId)
            startActivity(this@FoodDetailActivity, RatingReviewActivity::class.java, bundle)
        }
    }

    private fun loadWebViewFoodDetail() {
        if (mFood == null || isEmpty(mFood!!.url)) return
        mBinding!!.webView.loadUrl(mFood!!.url!!)
    }

    private fun addHistory() {
        if (mFood == null || isHistory(mFood!!)) return
        val userEmail = user!!.email
        val userInfo = UserInfo(System.currentTimeMillis(), userEmail)
        MyApplication[this].foodDatabaseReference()
            ?.child(mFood!!.id.toString())
            ?.child("history")
            ?.child(userInfo.id.toString())
            ?.setValue(userInfo)
    }

    private fun isHistory(food: Food): Boolean {
        if (food.history == null || food.history!!.isEmpty()) {
            return false
        }
        val listHistory: List<UserInfo> = ArrayList(
            food.history!!.values
        )
        if (listHistory.isEmpty()) {
            return false
        }
        for (userInfo in listHistory) {
            if (user!!.email == userInfo.emailUser) {
                return true
            }
        }
        return false
    }

    private fun changeCountViewFood() {
        MyApplication[this].countFoodDatabaseReference(mFoodId)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentCount = snapshot.getValue(Int::class.java)
                    var newCount = 1
                    if (currentCount != null) {
                        newCount = currentCount + 1
                    }
                    MyApplication[this@FoodDetailActivity]
                        .countFoodDatabaseReference(mFoodId)!!.removeEventListener(this)
                    MyApplication[this@FoodDetailActivity]
                        .countFoodDatabaseReference(mFoodId)!!.setValue(newCount)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        mFoodDetailValueEventListener?.let {
            MyApplication[this].foodDetailDatabaseReference(mFoodId)?.removeEventListener(it)
        }
    }
}