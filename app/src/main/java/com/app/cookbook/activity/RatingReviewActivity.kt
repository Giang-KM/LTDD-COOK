package com.app.cookbook.activity

import android.os.Bundle
import com.app.cookbook.MyApplication
import com.app.cookbook.R
import com.app.cookbook.constant.Constant
import com.app.cookbook.constant.GlobalFunction.encodeEmailUser
import com.app.cookbook.constant.GlobalFunction.hideSoftKeyboard
import com.app.cookbook.constant.GlobalFunction.showToastMessage
import com.app.cookbook.databinding.ActivityRatingReviewBinding
import com.app.cookbook.model.Rating
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class RatingReviewActivity : BaseActivity() {

    private var mBinding: ActivityRatingReviewBinding? = null
    private var mFoodId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRatingReviewBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        loadDataIntent()
        initToolbar()
        initView()
    }

    private fun loadDataIntent() {
        val bundle = intent.extras ?: return
        mFoodId = bundle.getLong(Constant.FOOD_ID)
    }

    private fun initToolbar() {
        mBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mBinding!!.layoutToolbar.tvToolbarTitle.text = getString(R.string.label_rate_review)
    }

    private fun initView() {
        mBinding!!.ratingbar.rating = 5f
        mBinding!!.tvSendReview.setOnClickListener {
            val rate = mBinding!!.ratingbar.rating
            val review = mBinding!!.edtReview.text.toString().trim { it <= ' ' }
            val rating = Rating(review, rate.toString().toDouble())
            sendRatingFood(rating)
        }
    }

    private fun sendRatingFood(rating: Rating) {
        MyApplication[this].ratingFoodDatabaseReference(mFoodId)
            ?.child(encodeEmailUser().toString())
            ?.setValue(rating) { _: DatabaseError?, _: DatabaseReference? ->
                showToastMessage(
                    this@RatingReviewActivity,
                    getString(R.string.msg_send_review_success)
                )
                mBinding!!.ratingbar.rating = 5f
                mBinding!!.edtReview.setText("")
                hideSoftKeyboard(this@RatingReviewActivity)
            }
    }
}