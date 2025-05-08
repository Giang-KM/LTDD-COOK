package com.app.cookbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.cookbook.adapter.CategoryMenuAdapter.CategoryViewHolder
import com.app.cookbook.databinding.ItemCategoryMenuBinding
import com.app.cookbook.listener.IOnClickCategoryListener
import com.app.cookbook.model.Category

class CategoryMenuAdapter(
    private val listCategory: List<Category>?,
    private val mListener: IOnClickCategoryListener
) : RecyclerView.Adapter<CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = listCategory!![position]
        holder.mBinding.tvTitle.text = category.name
        val strCount = category.count.toString() + " món"
        holder.mBinding.tvCount.text = strCount
        holder.mBinding.layoutItem.setOnClickListener {
            mListener.onClickItemCategory(
                category
            )
        }
    }

    override fun getItemCount(): Int {
        return listCategory?.size ?: 0
    }

    class CategoryViewHolder(val mBinding: ItemCategoryMenuBinding) : RecyclerView.ViewHolder(
        mBinding.root
    )
}