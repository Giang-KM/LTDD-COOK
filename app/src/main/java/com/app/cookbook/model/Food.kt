package com.app.cookbook.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class Food {
    var id: Long = 0
    var name: String? = null
    var image: String? = null
    var url: String? = null
    var isFeatured = false
    var count = 0
    var favorite: HashMap<String, UserInfo>? = null
    var history: HashMap<String, UserInfo>? = null
    var rating: HashMap<String, Rating>? = null
    var categoryId: Long = 0
    var categoryName: String? = null
    fun countFavorites(): String {
        return if (favorite == null || favorite!!.isEmpty()) {
            "0"
        } else {
            favorite!!.size.toString()
        }
    }

    val rate: Double
        get() {
            if (rating == null || rating!!.isEmpty()) return 0.0
            var sum = 0.0
            for (ratingEntity in rating!!.values) {
                sum += ratingEntity.rate
            }
            val symbols = DecimalFormatSymbols()
            symbols.decimalSeparator = '.'
            val formatter = DecimalFormat("#.#")
            formatter.decimalFormatSymbols = symbols
            return formatter.format(sum / rating!!.size).toDouble()
        }
    val countReviews: Int
        get() = if (rating == null || rating!!.isEmpty()) 0 else rating!!.size
}