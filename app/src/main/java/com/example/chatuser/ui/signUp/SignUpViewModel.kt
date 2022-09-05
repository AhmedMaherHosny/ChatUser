package com.example.chatuser.ui.signUp

import android.util.Patterns
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.chatuser.base.BaseViewModel
import com.example.chatuser.database.addUserToFirestore
import com.example.chatuser.model.AppUser
import com.example.chatuser.other.DataUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpViewModel : BaseViewModel<Navigator>() {

    var userName = ObservableField<String>()
    var email = ObservableField<String>()
    var password = ObservableField<String>()
    var confirmPassword = ObservableField<String>()
    var phoneNumber = ObservableField<String>()
    var userNameError = ObservableField<String>()
    var emailError = ObservableField<String>()
    var passwordError = ObservableField<String>()
    var confirmPasswordError = ObservableField<String>()
    var phoneNumberError = ObservableField<String>()
    var imageUri = ObservableField<String>()
    var verificationDialog = MutableLiveData<Boolean>()
    var userToAddPref : AppUser?= null
    private val auth = Firebase.auth

    @JvmName("isValidEmail1")
    fun isValidEmail(Email: CharSequence?): Boolean {
        return !Email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email.get()!!).matches()
    }


    fun createAccount() {
        when {
            validation() -> {
                addAccountToFirebase()
            }
        }
    }

    private fun addAccountToFirebase() {
        showLoading.postValue(true)
        auth.createUserWithEmailAndPassword(email.get()!!, password.get()!!).addOnCompleteListener {
            showLoading.postValue(false)
            when {
                it.isSuccessful -> {
                    addInformation(it.result.user?.uid)
                }
                else -> {
                    msgLiveData.postValue(it.exception?.localizedMessage.toString())
                }
            }
        }
    }

    private fun addInformation(uid: String?) {
        showLoading.postValue(true)
        val user = AppUser(
            id = uid,
            imageId = imageUri.get(),
            userName = userName.get(),
            email = email.get(),
            password = password.get(),
            phone = phoneNumber.get(),
        )
        addUserToFirestore(user, {
            showLoading.postValue(false)
            verifyEmail()
            userToAddPref = user
            DataUtils.user = user
        }, {
            showLoading.postValue(false)
            msgLiveData.postValue(it.localizedMessage?.toString() ?: "none")
        })
    }

    private fun verifyEmail() {
        val userf = auth.currentUser
        DataUtils.firebaseUser = userf
        userf!!.sendEmailVerification().addOnSuccessListener {
            verificationDialog.postValue(true)
        }.addOnFailureListener { e ->
            msgLiveData.postValue(e.localizedMessage!!.toString())
        }
    }

    private fun validation(): Boolean {
        var valid = true

        when {
            userName.get()?.trim().isNullOrBlank() -> {
                userNameError.set("please enter your user name")
                valid = false
            }
            else -> {
                userNameError.set(null)
            }
        }
        when {
            imageUri.get() == null -> {
                msgLiveData.postValue("Please chose your avatar")
                valid = false
            }
            else -> {
            }
        }

        when {
            !isValidEmail(email.get()?.trim()) -> {
                emailError.set("please enter your real email")
                valid = false
            }
            else -> {
                emailError.set(null)
            }
        }
        when {
            password.get()?.trim().isNullOrBlank() -> {
                passwordError.set("please enter your password")
                valid = false
            }
            else -> {
                passwordError.set(null)
            }
        }
        when {
            confirmPassword.get()?.trim().isNullOrBlank() -> {
                confirmPasswordError.set("please enter your password")
                valid = false
            }
            else -> {
                confirmPasswordError.set(null)
                when {
                    password.get()?.trim() != confirmPassword.get()?.trim() -> {
                        confirmPasswordError.set("Password doesn't match")
                        valid = false
                    }
                    else -> {
                        confirmPasswordError.set(null)
                    }
                }
            }
        }
        when {
            phoneNumber.get()?.trim().isNullOrBlank() -> {
                phoneNumberError.set("please enter your phone number")
                valid = false
            }
            else -> {
                phoneNumberError.set(null)
            }
        }
        return valid
    }

    fun goToSignInActivity() {
        navigator?.goToSignInActivity()
    }

    fun openGalleryToSelectImage() {
        navigator?.openGalleryToSelectImage()
    }
}