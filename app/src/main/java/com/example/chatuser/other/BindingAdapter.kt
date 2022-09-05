package com.example.chatuser.other

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.makeramen.roundedimageview.RoundedImageView

@BindingAdapter("app:error")
fun setError(textInputLayout: TextInputLayout, error: String?) {
    textInputLayout.error = error
}

@BindingAdapter("app:imageSrc")
fun setImage(imageview: RoundedImageView, imageId: Int) {
    imageview.setImageResource(imageId)
}