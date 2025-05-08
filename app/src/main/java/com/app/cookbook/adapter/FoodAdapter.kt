package com.app.cookbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.cookbook.R
import com.app.cookbook.adapter.FoodAdapter.FoodViewHolder
import com.app.cookbook.constant.GlobalFunction.isFavoriteFood
import com.app.cookbook.databinding.ItemFoodBinding
import com.app.cookbook.listener.IOnClickFoodListener
import com.app.cookbook.model.Category
import com.app.cookbook.model.Food
import com.app.cookbook.utils.GlideUtils.loadUrl

class FoodAdapter(private val listFood: List<Food>?, private val mListener: IOnClickFoodListener) :
    RecyclerView.Adapter<FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = listFood!![position]
        loadUrl(food.image, holder.mBinding.imgFood)
        holder.mBinding.tvName.text = food.name
        holder.mBinding.tvRate.text = food.rate.toString()
        holder.mBinding.tvCategory.text = food.categoryName
        val isFavorite = isFavoriteFood(food)
        holder.mBinding.tvCountHistory.text = food.count.toString()
        holder.mBinding.tvCountFavorite.text = food.countFavorites()
        if (isFavorite) {
            holder.mBinding.imgFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            holder.mBinding.imgFavorite.setImageResource(R.drawable.ic_unfavorite)
        }
        holder.mBinding.imgFavorite.setOnClickListener {
            mListener.onClickFavoriteFood(
                food,
                !isFavorite
            )
        }
        holder.mBinding.layoutImage.setOnClickListener { mListener.onClickItemFood(food) }
        holder.mBinding.layoutInfo.setOnClickListener { mListener.onClickItemFood(food) }
        holder.mBinding.tvCategory.setOnClickListener {
            mListener.onClickCategoryOfFood(
                Category(food.categoryId, food.categoryName)
            )
        }
    }

    override fun getItemCount(): Int {
        return listFood?.size ?: 0
    }

    class FoodViewHolder(val mBinding: ItemFoodBinding) : RecyclerView.ViewHolder(
        mBinding.root
    )
}