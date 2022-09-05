package com.example.chatuser.other

import com.example.chatuser.model.AppUser
import com.google.firebase.auth.FirebaseUser

object DataUtils {
    var user: AppUser? = null
    var firebaseUser: FirebaseUser? = null
    var fcmToken: String? = null
}