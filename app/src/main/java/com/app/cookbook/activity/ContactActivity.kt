package com.app.cookbook.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.app.cookbook.R
import com.app.cookbook.adapter.ContactAdapter
import com.app.cookbook.constant.AboutUsConfig
import com.app.cookbook.constant.GlobalFunction.callPhoneNumber
import com.app.cookbook.databinding.ActivityContactBinding
import com.app.cookbook.listener.ICallPhoneListener
import com.app.cookbook.model.Contact

class ContactActivity : BaseActivity() {

    private var mBinding: ActivityContactBinding? = null
    private var mContactAdapter: ContactAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        initToolbar()
        initData()
        initListener()
    }

    private fun initToolbar() {
        mBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mBinding!!.layoutToolbar.tvToolbarTitle.text = getString(R.string.label_contact)
    }

    private fun initData() {
        mBinding!!.tvAboutUsTitle.text = AboutUsConfig.ABOUT_US_TITLE
      //  mBinding!!.tvAboutUsContent.text = AboutUsConfig.ABOUT_US_CONTENT
       // mBinding!!.tvAboutUsWebsite.text = AboutUsConfig.ABOUT_US_WEBSITE_TITLE
        mContactAdapter = ContactAdapter(this, loadListContact(),
            object : ICallPhoneListener {
                override fun onClickCallPhone() {
                    callPhoneNumber(this@ContactActivity)
                }
            })
        val layoutManager = GridLayoutManager(this, 3)
        mBinding!!.rcvData.isNestedScrollingEnabled = false
        mBinding!!.rcvData.isFocusable = false
        mBinding!!.rcvData.layoutManager = layoutManager
        mBinding!!.rcvData.adapter = mContactAdapter
    }

    private fun initListener() {
        mBinding!!.layoutWebsite.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                   // Uri.parse(AboutUsConfig.WEBSITE)
                )
            )
        }
    }

    private fun loadListContact(): List<Contact> {
        val contactArrayList: MutableList<Contact> = ArrayList()
        contactArrayList.add(Contact(Contact.FACEBOOK, R.drawable.ic_facebook))
        contactArrayList.add(Contact(Contact.HOTLINE, R.drawable.ic_hotline))
        contactArrayList.add(Contact(Contact.GMAIL, R.drawable.ic_gmail))
        contactArrayList.add(Contact(Contact.SKYPE, R.drawable.ic_skype))
        contactArrayList.add(Contact(Contact.YOUTUBE, R.drawable.ic_youtube))
        contactArrayList.add(Contact(Contact.ZALO, R.drawable.ic_zalo))
        return contactArrayList
    }

    public override fun onDestroy() {
        super.onDestroy()
        mContactAdapter?.release()
    }
}