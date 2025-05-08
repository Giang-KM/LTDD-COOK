package com.app.cookbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.cookbook.adapter.FoodFeaturedAdapter.FoodFeaturedViewHolder
import com.app.cookbook.databinding.ItemFoodFeaturedBinding
import com.app.cookbook.listener.IOnClickFoodListener
import com.app.cookbook.model.Food
import com.app.cookbook.utils.GlideUtils.loadUrlBanner

class FoodFeaturedAdapter(private val mListFood: List<Food>?, private val mListener: IOnClickFoodListener) :
    RecyclerView.Adapter<FoodFeaturedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodFeaturedViewHolder {
        val binding = ItemFoodFeaturedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FoodFeaturedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodFeaturedViewHolder, position: Int) {
        val food = mListFood!![position]
        loadUrlBanner(food.image, holder.mBinding.imgFood)
        holder.mBinding.layoutItem.setOnClickListener { mListener.onClickItemFood(food) }
    }

    override fun getItemCount(): Int {
        return mListFood?.size ?: 0
    }

    class FoodFeaturedViewHolder(val mBinding: ItemFoodFeaturedBinding) : RecyclerView.ViewHolder(
        mBinding.root
    )
}