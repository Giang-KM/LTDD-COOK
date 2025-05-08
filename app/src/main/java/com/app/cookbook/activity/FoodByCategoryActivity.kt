package com.app.cookbook.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.cookbook.MyApplication
import com.app.cookbook.adapter.FoodAdapter
import com.app.cookbook.constant.Constant
import com.app.cookbook.constant.GlobalFunction.goToFoodDetail
import com.app.cookbook.constant.GlobalFunction.onClickFavoriteFood
import com.app.cookbook.databinding.ActivityFoodByCategoryBinding
import com.app.cookbook.listener.IOnClickFoodListener
import com.app.cookbook.model.Category
import com.app.cookbook.model.Food
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FoodByCategoryActivity : BaseActivity() {

    private var mBinding: ActivityFoodByCategoryBinding? = null
    private var mFoodAdapter: FoodAdapter? = null
    private var mListFood: MutableList<Food>? = null
    private var mCategory: Category? = null
    private var mFoodValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFoodByCategoryBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        loadDataIntent()
        initToolbar()
        initView()
        loadListFoodFromFirebase()
    }

    private fun loadDataIntent() {
        val bundle = intent.extras
        if (bundle != null) {
            mCategory = bundle[Constant.OBJECT_CATEGORY] as Category?
        }
    }

    private fun initToolbar() {
        mBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mBinding!!.layoutToolbar.tvToolbarTitle.text = mCategory!!.name
    }

    private fun initView() {
        val linearLayoutManager = LinearLayoutManager(this)
        mBinding!!.rcvData.layoutManager = linearLayoutManager
        mListFood = ArrayList()
        mFoodAdapter = FoodAdapter(mListFood, object : IOnClickFoodListener {
            override fun onClickItemFood(food: Food) {
                goToFoodDetail(this@FoodByCategoryActivity, food.id)
            }

            override fun onClickFavoriteFood(food: Food, favorite: Boolean) {
                onClickFavoriteFood(this@FoodByCategoryActivity, food, favorite)
            }

            override fun onClickCategoryOfFood(category: Category) {}
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
                mFoodAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        MyApplication[this].foodDatabaseReference()
            ?.orderByChild("categoryId")
            ?.equalTo(mCategory!!.id.toDouble())
            ?.addValueEventListener(mFoodValueEventListener!!)
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