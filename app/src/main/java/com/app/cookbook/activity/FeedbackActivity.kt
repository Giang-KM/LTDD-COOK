package com.app.cookbook.activity

import android.os.Bundle
import com.app.cookbook.MyApplication
import com.app.cookbook.R
import com.app.cookbook.constant.GlobalFunction.hideSoftKeyboard
import com.app.cookbook.constant.GlobalFunction.showToastMessage
import com.app.cookbook.databinding.ActivityFeedbackBinding
import com.app.cookbook.model.Feedback
import com.app.cookbook.prefs.DataStoreManager.Companion.user
import com.app.cookbook.utils.StringUtil.isEmpty
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class FeedbackActivity : BaseActivity() {

    private var mBinding: ActivityFeedbackBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        initToolbar()
        initData()
    }

    private fun initToolbar() {
        mBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mBinding!!.layoutToolbar.tvToolbarTitle.text = getString(R.string.label_feedback)
    }

    private fun initData() {
        mBinding!!.edtEmail.setText(user!!.email)
        mBinding!!.tvSendFeedback.setOnClickListener { onClickSendFeedback() }
    }

    private fun onClickSendFeedback() {
        val strName = mBinding!!.edtName.text.toString()
        val strPhone = mBinding!!.edtPhone.text.toString()
        val strEmail = mBinding!!.edtEmail.text.toString()
        val strComment = mBinding!!.edtComment.text.toString()
        if (isEmpty(strName)) {
            showToastMessage(this, getString(R.string.msg_name_require))
        } else if (isEmpty(strComment)) {
            showToastMessage(this, getString(R.string.msg_comment_require))
        } else {
            showProgressDialog(true)
            val feedback = Feedback(strName, strPhone, strEmail, strComment)
            MyApplication[this].feedbackDatabaseReference()
                ?.child(System.currentTimeMillis().toString())
                ?.setValue(feedback) { _: DatabaseError?, _: DatabaseReference? ->
                    showProgressDialog(false)
                    sendFeedbackSuccess()
                }
        }
    }

    private fun sendFeedbackSuccess() {
        hideSoftKeyboard(this)
        showToastMessage(this, getString(R.string.msg_send_feedback_success))
        mBinding!!.edtName.setText("")
        mBinding!!.edtPhone.setText("")
        mBinding!!.edtComment.setText("")
    }
}