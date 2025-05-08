package com.app.cookbook.prefs

import android.content.Context
import com.app.cookbook.model.User
import com.app.cookbook.utils.StringUtil.isEmpty
import com.google.gson.Gson

class DataStoreManager {

    private var sharedPreferences: MySharedPreferences? = null

    companion object {
        private const val PREF_USER_INFO = "pref_user_info"
        private var instance: DataStoreManager? = null

        fun init(context: Context) {
            instance = DataStoreManager()
            instance!!.sharedPreferences = MySharedPreferences(context)
        }

        private fun getInstance(): DataStoreManager? {
            return if (instance != null) {
                instance
            } else {
                throw IllegalStateException("Not initialized")
            }
        }

        @JvmStatic
        var user: User?
            get() {
                val jsonUser = getInstance()!!.sharedPreferences!!.getStringValue(PREF_USER_INFO)
                return if (!isEmpty(jsonUser)) {
                    Gson().fromJson(
                        jsonUser,
                        User::class.java
                    )
                } else User()
            }
            set(user) {
                var jsonUser: String? = ""
                if (user != null) {
                    jsonUser = user.toJSon()
                }
                getInstance()!!.sharedPreferences!!.putStringValue(PREF_USER_INFO, jsonUser)
            }
    }
}