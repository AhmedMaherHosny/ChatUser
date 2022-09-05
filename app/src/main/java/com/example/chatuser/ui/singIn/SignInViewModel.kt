package com.example.chatuser.ui.singIn

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.chatuser.base.BaseViewModel
import com.example.chatuser.database.signIn
import com.example.chatuser.model.AppUser
import com.example.chatuser.other.DataUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInViewModel : BaseViewModel<Navigator>() {

    val email = ObservableField<String>()
    val password = ObservableField<String>()
    private val auth = Firebase.auth
    var addPrefManger = MutableLiveData<Boolean>()

    fun login() {
        authWithFirebase()
    }

    private fun authWithFirebase() {
        showLoading.postValue(true)
        auth.signInWithEmailAndPassword(email.get().toString(), password.get().toString())
            .addOnCompleteListener {
                showLoading.postValue(false)
                if (it.isSuccessful) {
                    checkUserInFirestore(it.result.user?.uid)
                } else {
                    msgLiveData.postValue("Please enter a valid email and password")
                }
            }
    }

    private fun checkUserInFirestore(uid: String?) {
        showLoading.postValue(true)
        signIn(uid!!, {
            showLoading.postValue(false)
            when {
                it.exists() -> {
                    DataUtils.user = it.toObject(AppUser::class.java)!!
                    addPrefManger.postValue(true)
                    checkIfVerifiedEmail()
                }
                else -> {
                    msgLiveData.postValue("Please enter a valid email and password")
                    addPrefManger.postValue(false)
                }
            }
        }, {
            showLoading.postValue(false)
            msgLiveData.postValue(it.localizedMessage?.toString() ?: "none")
        })
    }

    private fun checkIfVerifiedEmail() {
        DataUtils.firebaseUser = auth.currentUser
        when {
            auth.currentUser!!.isEmailVerified ->
                navigator?.openHomeScreen()
            else ->
                navigator?.openVerificationScreen()
        }
    }

    fun goToSignUpActivity() {
        navigator?.goToSignUpActivity()
    }
}