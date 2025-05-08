package com.app.cookbook.listener

import com.app.cookbook.model.Category
import com.app.cookbook.model.Food

interface IOnClickFoodListener {
    fun onClickItemFood(food: Food)
    fun onClickFavoriteFood(food: Food, favorite: Boolean)
    fun onClickCategoryOfFood(category: Category)
}