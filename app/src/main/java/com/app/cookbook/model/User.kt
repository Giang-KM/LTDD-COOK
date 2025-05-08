package com.app.cookbook.model

import com.google.gson.Gson

class User {
    var email: String? = null
    var password: String? = null

    constructor()
    constructor(email: String?, password: String?) {
        this.email = email
        this.password = password
    }

    fun toJSon(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}