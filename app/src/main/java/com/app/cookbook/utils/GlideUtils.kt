package com.app.cookbook.utils

import android.widget.ImageView
import com.app.cookbook.R
import com.bumptech.glide.Glide

object GlideUtils {
    @JvmStatic
    fun loadUrlBanner(url: String?, imageView: ImageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.img_banner_no_image)
            return
        }
        Glide.with(imageView.context)
            .load(url)
            .error(R.drawable.img_banner_no_image)
            .dontAnimate()
            .into(imageView)
    }

    @JvmStatic
    fun loadUrl(url: String?, imageView: ImageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.img_logo_app)
            return
        }
        Glide.with(imageView.context)
            .load(url)
            .error(R.drawable.img_logo_app)
            .dontAnimate()
            .into(imageView)
    }
}