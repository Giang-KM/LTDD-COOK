package com.app.cookbook.constant

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.app.cookbook.MyApplication
import com.app.cookbook.activity.FoodByCategoryActivity
import com.app.cookbook.activity.FoodDetailActivity
import com.app.cookbook.model.Category
import com.app.cookbook.model.Food
import com.app.cookbook.model.UserInfo
import com.app.cookbook.prefs.DataStoreManager.Companion.user
import java.text.Normalizer
import java.util.regex.Pattern

object GlobalFunction {
    fun startActivity(context: Context, clz: Class<*>?) {
        val intent = Intent(context, clz)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    @JvmStatic
    fun startActivity(context: Context, clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(context, clz)
        intent.putExtras(bundle!!)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    @JvmStatic
    fun hideSoftKeyboard(activity: Activity) {
        try {
            val inputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    @JvmStatic
    fun onClickOpenGmail(context: Context) {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", AboutUsConfig.GMAIL, null
            )
        )
        context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
    }

    @JvmStatic
    fun onClickOpenSkype(context: Context) {
        try {
           // val skypeUri = Uri.parse("skype:" + AboutUsConfig.SKYPE_ID + "?chat")
            context.packageManager.getPackageInfo("com.skype.raider", 0)
           // val skypeIntent = Intent(Intent.ACTION_VIEW, skypeUri)
          //  skypeIntent.component = ComponentName("com.skype.raider", "com.skype.raider.Main")
          //  context.startActivity(skypeIntent)
        } catch (e: Exception) {
            openSkypeWebView(context)
        }
    }

    private fun openSkypeWebView(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                   // Uri.parse("skype:" + AboutUsConfig.SKYPE_ID + "?chat")
                )
            )
        } catch (exception: Exception) {
            val skypePackageName = "com.skype.raider"
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$skypePackageName")
                    )
                )
            } catch (ex: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$skypePackageName")
                    )
                )
            }
        }
    }

    @JvmStatic
    fun onClickOpenFacebook(context: Context) {
        var intent: Intent
        try {
           // var urlFacebook: String = AboutUsConfig.PAGE_FACEBOOK
            val packageManager = context.packageManager
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) {
               // urlFacebook = "fb://facewebmodal/f?href=" + AboutUsConfig.LINK_FACEBOOK
            }
          //  intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlFacebook))
        } catch (e: Exception) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.LINK_FACEBOOK))
        }
       // context.startActivity(intent)
    }

    @JvmStatic
    fun onClickOpenYoutubeChannel(context: Context) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
              //  Uri.parse(AboutUsConfig.LINK_YOUTUBE)
            )
        )
    }

    @JvmStatic
    fun onClickOpenZalo(context: Context) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                //Uri.parse(AboutUsConfig.ZALO_LINK)
            )
        )
    }

    @JvmStatic
    fun callPhoneNumber(activity: Activity) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    101
                )
                return
            }
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + AboutUsConfig.PHONE_NUMBER)
            activity.startActivity(callIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @JvmStatic
    fun showToastMessage(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun getTextSearch(input: String?): String {
        val nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("")
    }

    @JvmStatic
    fun goToFoodDetail(context: Context, foodId: Long) {
        val bundle = Bundle()
        bundle.putLong(Constant.FOOD_ID, foodId)
        startActivity(context, FoodDetailActivity::class.java, bundle)
    }

    @JvmStatic
    fun goToFoodByCategory(context: Context, category: Category?) {
        val bundle = Bundle()
        bundle.putSerializable(Constant.OBJECT_CATEGORY, category)
        startActivity(context, FoodByCategoryActivity::class.java, bundle)
    }

    @JvmStatic
    fun onClickFavoriteFood(context: Context?, food: Food, isFavorite: Boolean) {
        if (context == null) return
        if (isFavorite) {
            val userEmail = user!!.email
            val userInfo = UserInfo(System.currentTimeMillis(), userEmail)
            MyApplication[context].foodDatabaseReference()
                ?.child(food.id.toString())
                ?.child("favorite")
                ?.child(userInfo.id.toString())
                ?.setValue(userInfo)
        } else {
            val userInfo = getUserFavoriteFood(food)
            if (userInfo != null) {
                MyApplication[context].foodDatabaseReference()
                    ?.child(food.id.toString())
                    ?.child("favorite")
                    ?.child(userInfo.id.toString())
                    ?.removeValue()
            }
        }
    }

    private fun getUserFavoriteFood(food: Food): UserInfo? {
        var userInfo: UserInfo? = null
        if (food.favorite == null || food.favorite!!.isEmpty()) return null
        val listUsersFavorite: List<UserInfo> = ArrayList(
            food.favorite!!.values
        )
        for (userObject in listUsersFavorite) {
            if (user!!.email == userObject.emailUser) {
                userInfo = userObject
                break
            }
        }
        return userInfo
    }

    @JvmStatic
    fun isFavoriteFood(food: Food): Boolean {
        if (food.favorite == null || food.favorite!!.isEmpty()) return false
        val listUsersFavorite: List<UserInfo> = ArrayList(
            food.favorite!!.values
        )
        if (listUsersFavorite.isEmpty()) return false
        for (userInfo in listUsersFavorite) {
            if (user!!.email == userInfo.emailUser) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun isHistoryFood(food: Food): Boolean {
        if (food.history == null || food.history!!.isEmpty()) return false
        val listUsersHistory: List<UserInfo> = ArrayList(
            food.history!!.values
        )
        if (listUsersHistory.isEmpty()) return false
        for (userInfo in listUsersHistory) {
            if (user!!.email == userInfo.emailUser) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun encodeEmailUser(): Int {
        var hashCode = user!!.email.hashCode()
        if (hashCode < 0) {
            hashCode *= -1
        }
        return hashCode
    }
}