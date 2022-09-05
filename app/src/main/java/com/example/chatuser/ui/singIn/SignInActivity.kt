package com.example.chatuser.ui.singIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.chatuser.R
import com.example.chatuser.base.BaseActivity
import com.example.chatuser.databinding.ActivitySignInBinding
import com.example.chatuser.other.Constants
import com.example.chatuser.other.DataUtils
import com.example.chatuser.other.PreferenceManger
import com.example.chatuser.ui.home.HomeActivity
import com.example.chatuser.ui.signUp.SignUpActivity
import com.example.chatuser.ui.verification.VerificationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity<SignInViewModel, ActivitySignInBinding>(), Navigator {
    private lateinit var preferenceManger: PreferenceManger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManger = PreferenceManger(applicationContext)
        autoLogin()
        binding.vm = viewModel
        viewModel.navigator = this
        addPrefManger()

    }

    private fun autoLogin() {
        DataUtils.firebaseUser = Firebase.auth.currentUser
        if (preferenceManger.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            when {
                DataUtils.firebaseUser!!.isEmailVerified -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                else -> {
                    startActivity(Intent(this, VerificationActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun addPrefManger() {
        viewModel.addPrefManger.observe(this) {

            if (it == true) {
                preferenceManger.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                preferenceManger.putString(Constants.KEY_USER_ID, DataUtils.user!!.id!!.toString())
                preferenceManger.putString(Constants.KEY_USERNAME,
                    DataUtils.user!!.userName!!.toString())
                preferenceManger.putString(Constants.KEY_IMAGE,
                    DataUtils.user!!.imageId!!.toString())
            }

        }
    }

    override fun getLayoutId(): Int = R.layout.activity_sign_in

    override fun initViewModel(): SignInViewModel =
        ViewModelProvider(this)[SignInViewModel::class.java]

    override fun goToSignUpActivity() =
        startActivity(Intent(this, SignUpActivity::class.java))

    override fun openVerificationScreen() {
        val intent = Intent(this, VerificationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun openHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}