package com.app.cookbook.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.cookbook.MyApplication
import com.app.cookbook.R
import com.app.cookbook.adapter.FoodAdapter
import com.app.cookbook.constant.GlobalFunction.getTextSearch
import com.app.cookbook.constant.GlobalFunction.goToFoodByCategory
import com.app.cookbook.constant.GlobalFunction.goToFoodDetail
import com.app.cookbook.constant.GlobalFunction.hideSoftKeyboard
import com.app.cookbook.constant.GlobalFunction.onClickFavoriteFood
import com.app.cookbook.constant.GlobalFunction.showToastMessage
import com.app.cookbook.databinding.ActivitySearchBinding
import com.app.cookbook.listener.IOnClickFoodListener
import com.app.cookbook.model.Category
import com.app.cookbook.model.Food
import com.app.cookbook.utils.StringUtil.isEmpty
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class SearchActivity : BaseActivity() {

    private var mBinding: ActivitySearchBinding? = null
    private var mListFood: MutableList<Food>? = null
    private var mFoodAdapter: FoodAdapter? = null
    private var mValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        initToolbar()
        initUi()
        initListener()
        getListFoodFromFirebase("")
    }

    private fun initToolbar() {
        mBinding!!.layoutToolbar.imgToolbar.setOnClickListener { finish() }
        mBinding!!.layoutToolbar.tvToolbarTitle.text = getString(R.string.label_search)
    }

    private fun initUi() {
        val linearLayoutManager = LinearLayoutManager(this)
        mBinding!!.rcvSearchResult.layoutManager = linearLayoutManager
        mListFood = ArrayList()
        mFoodAdapter = FoodAdapter(mListFood, object : IOnClickFoodListener {
            override fun onClickItemFood(food: Food) {
                goToFoodDetail(this@SearchActivity, food.id)
            }

            override fun onClickFavoriteFood(food: Food, favorite: Boolean) {
                onClickFavoriteFood(this@SearchActivity, food, favorite)
            }

            override fun onClickCategoryOfFood(category: Category) {
                goToFoodByCategory(this@SearchActivity, category)
            }
        })
        mBinding!!.rcvSearchResult.adapter = mFoodAdapter
    }

    private fun initListener() {
        mBinding!!.edtSearchName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable) {
                val strKey = s.toString().trim { it <= ' ' }
                if (strKey == "" || strKey.isEmpty()) {
                    getListFoodFromFirebase("")
                }
            }
        })
        mBinding!!.imgSearch.setOnClickListener { searchFood() }
        mBinding!!.edtSearchName.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFood()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getListFoodFromFirebase(key: String) {
        mValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resetListFoodData()
                for (dataSnapshot in snapshot.children) {
                    val food = dataSnapshot.getValue(Food::class.java) ?: return
                    if (isEmpty(key)) {
                        mListFood!!.add(0, food)
                    } else {
                        if (getTextSearch(food.name).lowercase(Locale.getDefault())
                                .trim { it <= ' ' }
                                .contains(
                                    getTextSearch(key).lowercase(Locale.getDefault())
                                        .trim { it <= ' ' })
                        ) {
                            mListFood!!.add(0, food)
                        }
                    }
                }
                mFoodAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                showToastMessage(
                    this@SearchActivity,
                    getString(R.string.msg_get_date_error)
                )
            }
        }
        MyApplication[this].foodDatabaseReference()?.addValueEventListener(mValueEventListener!!)
    }

    private fun resetListFoodData() {
        if (mListFood == null) {
            mListFood = ArrayList()
        } else {
            mListFood!!.clear()
        }
    }

    private fun searchFood() {
        val strKey = mBinding!!.edtSearchName.text.toString().trim { it <= ' ' }
        if (mValueEventListener != null) {
            MyApplication[this].foodDatabaseReference()!!
                .removeEventListener(mValueEventListener!!)
        }
        getListFoodFromFirebase(strKey)
        hideSoftKeyboard(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mValueEventListener?.let {
            MyApplication[this].foodDatabaseReference()?.removeEventListener(it)
        }
    }
}