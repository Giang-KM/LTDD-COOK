package com.app.cookbook.model

import java.io.Serializable

class Category : Serializable {
    var id: Long = 0
    var name: String? = null
    var image: String? = null
    var count = 0

    constructor()
    constructor(id: Long, name: String?) {
        this.id = id
        this.name = name
    }
}