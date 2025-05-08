package com.app.cookbook.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.app.cookbook.MyApplication
import com.app.cookbook.R
import com.app.cookbook.adapter.CategoryListAdapter
import com.app.cookbook.constant.GlobalFunction.goToFoodByCategory
import com.app.cookbook.databinding.ActivityListCategoryBinding
import com.app.cookbook.listener.IOnClickCategoryListener
import com.app.cookbook.model.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ListCategoryActivity : BaseActivity() {

    private var mBinding: ActivityListCategoryBinding? = null
    private var mCategoryListAdapter: CategoryListAdapter? = null
    private var mListCategory: MutableList<Category>? = null
    private var mCategoryValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityListCategoryBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        initToolbar()
        initView()
        loadListCategoryFromFirebase()
    }

    private fun initToolbar() {
        mBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mBinding!!.layoutToolbar.tvToolbarTitle.text = getString(R.string.label_category)
    }

    private fun initView() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        mBinding!!.rcvData.layoutManager = gridLayoutManager
        mListCategory = ArrayList()
        mCategoryListAdapter = CategoryListAdapter(mListCategory,
            object : IOnClickCategoryListener {
                override fun onClickItemCategory(category: Category) {
                    goToFoodByCategory(
                        this@ListCategoryActivity,
                        category
                    )
                }
            })
        mBinding!!.rcvData.adapter = mCategoryListAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadListCategoryFromFirebase() {
        mCategoryValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resetListCategory()
                for (dataSnapshot in snapshot.children) {
                    val category = dataSnapshot.getValue(
                        Category::class.java
                    ) ?: return
                    mListCategory!!.add(0, category)
                }
                mCategoryListAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        MyApplication[this].categoryDatabaseReference()?.addValueEventListener(mCategoryValueEventListener!!)
    }

    private fun resetListCategory() {
        if (mListCategory == null) {
            mListCategory = ArrayList()
        } else {
            mListCategory!!.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCategoryValueEventListener?.let {
            MyApplication[this].categoryDatabaseReference()?.removeEventListener(it)
        }
    }
}