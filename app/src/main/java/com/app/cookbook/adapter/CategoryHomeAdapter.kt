package com.app.cookbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.cookbook.adapter.CategoryHomeAdapter.CategoryHomeViewHolder
import com.app.cookbook.databinding.ItemCategoryHomeBinding
import com.app.cookbook.listener.IOnClickCategoryListener
import com.app.cookbook.model.Category
import com.app.cookbook.utils.GlideUtils.loadUrl

class CategoryHomeAdapter(
    private val listCategory: List<Category>?,
    private val mListener: IOnClickCategoryListener
) : RecyclerView.Adapter<CategoryHomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHomeViewHolder {
        val binding = ItemCategoryHomeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryHomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryHomeViewHolder, position: Int) {
        val category = listCategory!![position]
        loadUrl(category.image, holder.mBinding.imgCategory)
        holder.mBinding.tvName.text = category.name
        holder.mBinding.layoutItem.setOnClickListener {
            mListener.onClickItemCategory(
                category
            )
        }
    }

    override fun getItemCount(): Int {
        return listCategory?.size ?: 0
    }

    class CategoryHomeViewHolder(val mBinding: ItemCategoryHomeBinding) : RecyclerView.ViewHolder(
        mBinding.root
    )
}