package com.app.cookbook.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.cookbook.MyApplication
import com.app.cookbook.R
import com.app.cookbook.adapter.FoodAdapter
import com.app.cookbook.constant.GlobalFunction.goToFoodByCategory
import com.app.cookbook.constant.GlobalFunction.goToFoodDetail
import com.app.cookbook.constant.GlobalFunction.isHistoryFood
import com.app.cookbook.constant.GlobalFunction.onClickFavoriteFood
import com.app.cookbook.constant.GlobalFunction.showToastMessage
import com.app.cookbook.databinding.ActivityHistoryBinding
import com.app.cookbook.listener.IOnClickFoodListener
import com.app.cookbook.model.Category
import com.app.cookbook.model.Food
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HistoryActivity : BaseActivity() {

    private var mBinding: ActivityHistoryBinding? = null
    private var mListFood: MutableList<Food>? = null
    private var mFoodAdapter: FoodAdapter? = null
    private var mValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        initToolbar()
        initUi()
        loadDataHistory()
    }

    private fun initToolbar() {
        mBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mBinding!!.layoutToolbar.tvToolbarTitle.text = getString(R.string.label_history)
    }

    private fun initUi() {
        val linearLayoutManager = LinearLayoutManager(this)
        mBinding!!.rcvData.layoutManager = linearLayoutManager
        mListFood = ArrayList()
        mFoodAdapter = FoodAdapter(mListFood, object : IOnClickFoodListener {
            override fun onClickItemFood(food: Food) {
                goToFoodDetail(this@HistoryActivity, food.id)
            }

            override fun onClickFavoriteFood(food: Food, favorite: Boolean) {
                onClickFavoriteFood(this@HistoryActivity, food, favorite)
            }

            override fun onClickCategoryOfFood(category: Category) {
                goToFoodByCategory(this@HistoryActivity, category)
            }
        })
        mBinding!!.rcvData.adapter = mFoodAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadDataHistory() {
        mValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resetListData()
                for (dataSnapshot in snapshot.children) {
                    val food = dataSnapshot.getValue(Food::class.java) ?: return
                    if (isHistoryFood(food)) {
                        mListFood!!.add(0, food)
                    }
                }
                mFoodAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                showToastMessage(
                    this@HistoryActivity,
                    getString(R.string.msg_get_date_error)
                )
            }
        }
        MyApplication[this].foodDatabaseReference()?.addValueEventListener(mValueEventListener!!)
    }

    private fun resetListData() {
        if (mListFood == null) {
            mListFood = ArrayList()
        } else {
            mListFood!!.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mValueEventListener?.let {
            MyApplication[this].foodDatabaseReference()?.removeEventListener(it)
        }
    }
}