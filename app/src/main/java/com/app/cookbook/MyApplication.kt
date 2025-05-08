package com.app.cookbook

import android.app.Application
import android.content.Context
import com.app.cookbook.prefs.DataStoreManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {

    private var mFirebaseDatabase: FirebaseDatabase? = null

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        mFirebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_URL)
        DataStoreManager.init(applicationContext)
    }

    fun categoryDatabaseReference(): DatabaseReference? {
        return mFirebaseDatabase?.getReference("/category")
    }

    fun foodDatabaseReference(): DatabaseReference? {
        return mFirebaseDatabase?.getReference("/food")
    }

    fun feedbackDatabaseReference(): DatabaseReference? {
        return mFirebaseDatabase?.getReference("/feedback")
    }

    fun requestFoodDatabaseReference(): DatabaseReference? {
        return mFirebaseDatabase?.getReference("/request")
    }

    fun foodDetailDatabaseReference(foodId: Long): DatabaseReference? {
        return mFirebaseDatabase?.getReference("food/$foodId")
    }

    fun ratingFoodDatabaseReference(foodId: Long): DatabaseReference? {
        return mFirebaseDatabase?.getReference("/food/$foodId/rating")
    }

    fun countFoodDatabaseReference(foodId: Long): DatabaseReference? {
        return mFirebaseDatabase?.getReference("/food/$foodId/count")
    }

    companion object {
        const val FIREBASE_URL = "https://cook-book-app-d91ec-default-rtdb.firebaseio.com"
        @JvmStatic
        operator fun get(context: Context): MyApplication {
            return context.applicationContext as MyApplication
        }
    }
}