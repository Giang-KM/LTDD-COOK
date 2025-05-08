package com.app.cookbook.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.app.cookbook.MyApplication
import com.app.cookbook.R
import com.app.cookbook.adapter.CategoryHomeAdapter
import com.app.cookbook.adapter.CategoryMenuAdapter
import com.app.cookbook.adapter.FoodAdapter
import com.app.cookbook.adapter.FoodFeaturedAdapter
import com.app.cookbook.constant.Constant
import com.app.cookbook.constant.GlobalFunction.goToFoodByCategory
import com.app.cookbook.constant.GlobalFunction.goToFoodDetail
import com.app.cookbook.constant.GlobalFunction.hideSoftKeyboard
import com.app.cookbook.constant.GlobalFunction.isFavoriteFood
import com.app.cookbook.constant.GlobalFunction.isHistoryFood
import com.app.cookbook.constant.GlobalFunction.onClickFavoriteFood
import com.app.cookbook.constant.GlobalFunction.showToastMessage
import com.app.cookbook.constant.GlobalFunction.startActivity
import com.app.cookbook.databinding.ActivityMainBinding
import com.app.cookbook.listener.IOnClickCategoryListener
import com.app.cookbook.listener.IOnClickFoodListener
import com.app.cookbook.model.Category
import com.app.cookbook.model.Food
import com.app.cookbook.model.RequestFood
import com.app.cookbook.prefs.DataStoreManager.Companion.user
import com.app.cookbook.utils.StringUtil.isEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.*

@SuppressLint("NonConstantResourceId")
class MainActivity : BaseActivity(), View.OnClickListener {

    private var mBinding: ActivityMainBinding? = null
    private var mCategoryMenuAdapter: CategoryMenuAdapter? = null
    private var mListCategory: MutableList<Category>? = null
    private var mListCategoryHome: MutableList<Category>? = null
    private var mListFood: MutableList<Food>? = null
    private var mListFoodFeatured: MutableList<Food>? = null
    private var mCategoryValueEventListener: ValueEventListener? = null
    private var mFoodValueEventListener: ValueEventListener? = null
    private val mHandlerBanner = Handler(Looper.getMainLooper())
    private val mRunnableBanner = Runnable {
        if (mListFoodFeatured == null || mListFoodFeatured!!.isEmpty()) return@Runnable
        if (mBinding!!.viewPager.currentItem == mListFoodFeatured!!.size - 1) {
            mBinding!!.viewPager.currentItem = 0
            return@Runnable
        }
        mBinding!!.viewPager.currentItem = mBinding!!.viewPager.currentItem + 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        initToolbar()
        initListener()
        initNavigationMenuLeft()
    }

    private fun initToolbar() {
        mBinding!!.header.imgToolbar.setImageResource(R.drawable.ic_menu)
        mBinding!!.header.tvToolbarTitle.text = getString(R.string.app_name)
    }

    private fun initListener() {
        mBinding!!.header.imgToolbar.setOnClickListener(this)
        mBinding!!.tvRequestFood.setOnClickListener(this)
        mBinding!!.layoutFeedback.setOnClickListener(this)
        mBinding!!.layoutContact.setOnClickListener(this)
        mBinding!!.layoutChangePassword.setOnClickListener(this)
        mBinding!!.layoutSignOut.setOnClickListener(this)
        mBinding!!.viewAllCategory.setOnClickListener(this)
        mBinding!!.viewAllFood.setOnClickListener(this)
        mBinding!!.layoutSearch.setOnClickListener(this)
        mBinding!!.layoutFavorite.setOnClickListener(this)
        mBinding!!.layoutHistory.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.img_toolbar -> mBinding!!.drawerLayout.openDrawer(GravityCompat.START)
            R.id.tv_request_food -> {
                mBinding!!.drawerLayout.closeDrawer(GravityCompat.START)
                showDialogRequestFood()
            }
            R.id.layout_feedback -> {
                mBinding!!.drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(this, FeedbackActivity::class.java)
            }
            R.id.layout_contact -> {
                mBinding!!.drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(this, ContactActivity::class.java)
            }
            R.id.layout_change_password -> {
                mBinding!!.drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(this, ChangePasswordActivity::class.java)
            }
            R.id.layout_sign_out -> onClickSignOut()
            R.id.view_all_category -> startActivity(this, ListCategoryActivity::class.java)
            R.id.view_all_food -> startActivity(this, ListFoodActivity::class.java)
            R.id.layout_search -> startActivity(this, SearchActivity::class.java)
            R.id.layout_favorite -> startActivity(this, FavoriteActivity::class.java)
            R.id.layout_history -> startActivity(this, HistoryActivity::class.java)
        }
    }

    private fun initNavigationMenuLeft() {
        displayUserInformation()
        val linearLayoutManager = LinearLayoutManager(this)
        mBinding!!.rcvCategory.layoutManager = linearLayoutManager
        mListCategory = ArrayList()
        mCategoryMenuAdapter = CategoryMenuAdapter(mListCategory,
            object : IOnClickCategoryListener {
                override fun onClickItemCategory(category: Category) {
                    goToFoodByCategory(
                        this@MainActivity,
                        category
                    )
                }
            })
        mBinding!!.rcvCategory.adapter = mCategoryMenuAdapter
        loadListCategoryFromFirebase()
    }

    private fun displayUserInformation() {
        val user = user
        mBinding!!.tvUserEmail.text = user!!.email
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadListCategoryFromFirebase() {
        showProgressDialog(true)
        mCategoryValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resetListCategory()
                for (dataSnapshot in snapshot.children) {
                    val category = dataSnapshot.getValue(
                        Category::class.java
                    ) ?: return
                    mListCategory!!.add(0, category)
                }
                mCategoryMenuAdapter?.notifyDataSetChanged()
                displayListCategoryHome()
                loadListFoodFromFirebase()
            }

            override fun onCancelled(error: DatabaseError) {
                showProgressDialog(true)
            }
        }
        MyApplication[this].categoryDatabaseReference()?.addValueEventListener(mCategoryValueEventListener!!)
    }

    private fun displayListCategoryHome() {
        val layoutManagerHorizontal = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        mBinding!!.rcvCategoryHome.layoutManager = layoutManagerHorizontal
        val categoryHomeAdapter = CategoryHomeAdapter(loadListCategoryHome(),
            object : IOnClickCategoryListener {
                override fun onClickItemCategory(category: Category) {
                    goToFoodByCategory(
                        this@MainActivity,
                        category
                    )
                }
            })
        mBinding!!.rcvCategoryHome.adapter = categoryHomeAdapter
    }

    private fun loadListCategoryHome(): List<Category>? {
        resetListCategoryHome()
        for (category in mListCategory!!) {
            if (mListCategoryHome!!.size < Constant.MAX_SIZE_LIST_CATEGORY) {
                mListCategoryHome!!.add(category)
            }
        }
        return mListCategoryHome
    }

    private fun resetListCategory() {
        if (mListCategory == null) {
            mListCategory = ArrayList()
        } else {
            mListCategory!!.clear()
        }
    }

    private fun resetListCategoryHome() {
        if (mListCategoryHome == null) {
            mListCategoryHome = ArrayList()
        } else {
            mListCategoryHome!!.clear()
        }
    }

    private fun resetListFood() {
        if (mListFood == null) {
            mListFood = ArrayList()
        } else {
            mListFood!!.clear()
        }
    }

    private fun resetListFoodFeatured() {
        if (mListFoodFeatured == null) {
            mListFoodFeatured = ArrayList()
        } else {
            mListFoodFeatured!!.clear()
        }
    }

    private fun loadListFoodFromFirebase() {
        mFoodValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showProgressDialog(false)
                resetListFood()
                for (dataSnapshot in snapshot.children) {
                    val food = dataSnapshot.getValue(Food::class.java) ?: return
                    mListFood!!.add(0, food)
                }
                displayListFoodFeatured()
                displayListPopularFood()
                displayCountFoodOfCategory()
                displayCountFavorite()
                displayCountHistory()
            }

            override fun onCancelled(error: DatabaseError) {
                showProgressDialog(false)
                showToastMessage(this@MainActivity, getString(R.string.msg_get_date_error))
            }
        }
        MyApplication[this].foodDatabaseReference()?.addValueEventListener(mFoodValueEventListener!!)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun displayCountFoodOfCategory() {
        if (mListCategory == null || mListCategory!!.isEmpty()) return
        for (category in mListCategory!!) {
            category.count = loadCountFoodOfCategory(category.id)
        }
        mCategoryMenuAdapter?.notifyDataSetChanged()
    }

    private fun loadCountFoodOfCategory(categoryId: Long): Int {
        if (mListFood == null || mListFood!!.isEmpty()) return 0
        val listFoods: MutableList<Food> = ArrayList()
        for (food in mListFood!!) {
            if (categoryId == food.categoryId) {
                listFoods.add(food)
            }
        }
        return listFoods.size
    }

    private fun displayCountFavorite() {
        var countFavorite = 0
        if (mListFood != null && mListFood!!.isNotEmpty()) {
            val listFavorite: MutableList<Food> = ArrayList()
            for (food in mListFood!!) {
                if (isFavoriteFood(food)) {
                    listFavorite.add(food)
                }
            }
            countFavorite = listFavorite.size
        }
        mBinding!!.tvCountFavorite.text = countFavorite.toString()
    }

    private fun displayCountHistory() {
        var countHistory = 0
        if (mListFood != null && mListFood!!.isNotEmpty()) {
            val listHistory: MutableList<Food> = ArrayList()
            for (food in mListFood!!) {
                if (isHistoryFood(food)) {
                    listHistory.add(food)
                }
            }
            countHistory = listHistory.size
        }
        mBinding!!.tvCountHistory.text = countHistory.toString()
    }

    private fun displayListFoodFeatured() {
        val foodFeaturedAdapter =
            FoodFeaturedAdapter(loadListFoodFeatured(), object : IOnClickFoodListener {
                override fun onClickItemFood(food: Food) {
                    goToFoodDetail(this@MainActivity, food.id)
                }

                override fun onClickFavoriteFood(food: Food, favorite: Boolean) {}
                override fun onClickCategoryOfFood(category: Category) {}
            })
        mBinding!!.viewPager.adapter = foodFeaturedAdapter
        mBinding!!.indicator.setViewPager(mBinding!!.viewPager)
        mBinding!!.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mHandlerBanner.removeCallbacks(mRunnableBanner)
                mHandlerBanner.postDelayed(mRunnableBanner, 3000)
            }
        })
    }

    private fun loadListFoodFeatured(): List<Food>? {
        resetListFoodFeatured()
        for (food in mListFood!!) {
            if (food.isFeatured && mListFoodFeatured!!.size < Constant.MAX_SIZE_LIST_FEATURED) {
                mListFoodFeatured!!.add(food)
            }
        }
        return mListFoodFeatured
    }

    private fun displayListPopularFood() {
        val linearLayoutManager = LinearLayoutManager(this)
        mBinding!!.rcvFoodPopular.layoutManager = linearLayoutManager
        val foodAdapter = FoodAdapter(loadListPopularFood(), object : IOnClickFoodListener {
            override fun onClickItemFood(food: Food) {
                goToFoodDetail(this@MainActivity, food.id)
            }

            override fun onClickFavoriteFood(food: Food, favorite: Boolean) {
                onClickFavoriteFood(this@MainActivity, food, favorite)
            }

            override fun onClickCategoryOfFood(category: Category) {
                goToFoodByCategory(this@MainActivity, category)
            }
        })
        mBinding!!.rcvFoodPopular.adapter = foodAdapter
    }

    private fun loadListPopularFood(): List<Food> {
        val list: MutableList<Food> = ArrayList()
        val allFoods: List<Food> = ArrayList(mListFood!!)
        Collections.sort(allFoods) { food1: Food, food2: Food -> food2.count - food1.count }
        for (food in allFoods) {
            if (list.size < Constant.MAX_SIZE_LIST_POPULAR) {
                list.add(food)
            }
        }
        return list
    }

    private fun showDialogRequestFood() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_dialog_request_food)
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        // Get view
        val imgClose = dialog.findViewById<ImageView>(R.id.img_close)
        val edtFoodName = dialog.findViewById<EditText>(R.id.edt_food_name)
        val tvSendRequest = dialog.findViewById<TextView>(R.id.tv_send_request)
        imgClose.setOnClickListener { dialog.dismiss() }
        tvSendRequest.setOnClickListener {
            val strFoodName = edtFoodName.text.toString().trim { it <= ' ' }
            if (isEmpty(strFoodName)) {
                showToastMessage(
                    this,
                    getString(R.string.msg_name_food_request)
                )
            } else {
                showProgressDialog(true)
                val requestFood = RequestFood(strFoodName)
                MyApplication[this].requestFoodDatabaseReference()
                    ?.child(System.currentTimeMillis().toString())
                    ?.setValue(requestFood) { _: DatabaseError?, _: DatabaseReference? ->
                        showProgressDialog(false)
                        hideSoftKeyboard(this)
                        showToastMessage(
                            this,
                            getString(R.string.msg_send_request_food_success)
                        )
                        dialog.dismiss()
                    }
            }
        }
        dialog.show()
    }

    private fun onClickSignOut() {
        FirebaseAuth.getInstance().signOut()
        user = null
        startActivity(this, LoginActivity::class.java)
        finishAffinity()
    }

    override fun onBackPressed() {
        showConfirmExitApp()
    }

    private fun showConfirmExitApp() {
        MaterialDialog.Builder(this)
            .title(getString(R.string.app_name))
            .content(getString(R.string.msg_exit_app))
            .positiveText(getString(R.string.action_ok))
            .onPositive { _: MaterialDialog?, _: DialogAction? -> finish() }
            .negativeText(getString(R.string.action_cancel))
            .cancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCategoryValueEventListener?.let {
            MyApplication[this].categoryDatabaseReference()?.removeEventListener(it)
        }
        mFoodValueEventListener?.let {
            MyApplication[this].foodDatabaseReference()?.removeEventListener(it)
        }
    }
}