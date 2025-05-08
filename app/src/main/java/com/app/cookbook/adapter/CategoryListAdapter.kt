package com.app.cookbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.cookbook.adapter.CategoryListAdapter.CategoryListViewHolder
import com.app.cookbook.databinding.ItemCategoryListBinding
import com.app.cookbook.listener.IOnClickCategoryListener
import com.app.cookbook.model.Category
import com.app.cookbook.utils.GlideUtils.loadUrl

class CategoryListAdapter(
    private val listCategory: List<Category>?,
    private val mListener: IOnClickCategoryListener
) : RecyclerView.Adapter<CategoryListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val binding = ItemCategoryListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
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

    class CategoryListViewHolder(val mBinding: ItemCategoryListBinding) : RecyclerView.ViewHolder(
        mBinding.root
    )
}