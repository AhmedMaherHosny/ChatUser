package com.example.chatuser.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppUser(
    val id: String? = null,
    val imageId: String? = null,
    val userName: String? = null,
    val email: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val fcmToken:String?=null,
    ) : Parcelable {

}