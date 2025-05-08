package com.app.cookbook.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.cookbook.MyApplication
import com.app.cookbook.R
import com.app.cookbook.adapter.FoodAdapter
import com.app.cookbook.constant.GlobalFunction.goToFoodByCategory
import com.app.cookbook.constant.GlobalFunction.goToFoodDetail
import com.app.cookbook.constant.GlobalFunction.onClickFavoriteFood
import com.app.cookbook.databinding.ActivityListFoodBinding
import com.app.cookbook.listener.IOnClickFoodListener
import com.app.cookbook.model.Category
import com.app.cookbook.model.Food
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ListFoodActivity : BaseActivity() {

    private var mBinding: ActivityListFoodBinding? = null
    private var mFoodAdapter: FoodAdapter? = null
    private var mListFood: MutableList<Food>? = null
    private var mFoodValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityListFoodBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        initToolbar()
        initView()
        loadListFoodFromFirebase()
    }

    private fun initToolbar() {
        mBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mBinding!!.layoutToolbar.tvToolbarTitle.text = getString(R.string.label_food_popular)
    }

    private fun initView() {
        val linearLayoutManager = LinearLayoutManager(this)
        mBinding!!.rcvData.layoutManager = linearLayoutManager
        mListFood = ArrayList()
        mFoodAdapter = FoodAdapter(mListFood, object : IOnClickFoodListener {
            override fun onClickItemFood(food: Food) {
                goToFoodDetail(this@ListFoodActivity, food.id)
            }

            override fun onClickFavoriteFood(food: Food, favorite: Boolean) {
                onClickFavoriteFood(this@ListFoodActivity, food, favorite)
            }

            override fun onClickCategoryOfFood(category: Category) {
                goToFoodByCategory(this@ListFoodActivity, category)
            }
        })
        mBinding!!.rcvData.adapter = mFoodAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadListFoodFromFirebase() {
        mFoodValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resetListFood()
                for (dataSnapshot in snapshot.children) {
                    val food = dataSnapshot.getValue(Food::class.java) ?: return
                    mListFood!!.add(0, food)
                }
                mListFood?.let { it.sortWith { food1: Food, food2: Food -> food2.count - food1.count } }
                mFoodAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        MyApplication[this].foodDatabaseReference()?.addValueEventListener(mFoodValueEventListener!!)
    }

    private fun resetListFood() {
        if (mListFood == null) {
            mListFood = ArrayList()
        } else {
            mListFood!!.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mFoodValueEventListener?.let {
            MyApplication[this].foodDatabaseReference()?.removeEventListener(it)
        }
    }
}