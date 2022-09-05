package com.example.chatuser.ui.home

import androidx.lifecycle.MutableLiveData
import com.example.chatuser.base.BaseViewModel
import com.example.chatuser.database.getToken
import com.example.chatuser.database.signOut
import com.example.chatuser.database.updateToken
import com.example.chatuser.other.DataUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeViewModel : BaseViewModel<Navigator>() {
    var prefClear = MutableLiveData<Boolean>()

    fun getTokenFromFirebaseUtils() {
        getToken {
            DataUtils.fcmToken = it
            updateToken(it, Firebase.auth.currentUser!!.uid, {

            }, { ex ->
                msgLiveData.postValue(ex.localizedMessage)
            })
        }
    }

    fun signOutFromFirebaseUtils() {
        signOut(Firebase.auth.currentUser!!.uid, {
            prefClear.postValue(true)
            navigator?.signOutMoveToSignInActivity()
        }, { ex ->
            msgLiveData.postValue(ex.localizedMessage)
        })
    }

    fun addFriend() {
        navigator?.goToAddFriend()
    }
}