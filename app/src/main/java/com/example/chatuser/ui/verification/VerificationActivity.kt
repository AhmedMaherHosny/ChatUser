package com.example.chatuser.ui.verification

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.chatuser.R
import com.example.chatuser.base.BaseActivity
import com.example.chatuser.databinding.ActivityVerificationBinding
import com.example.chatuser.other.DataUtils
import com.example.chatuser.ui.home.HomeActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class VerificationActivity : BaseActivity<VerificationViewModel, ActivityVerificationBinding>(),
    Navigator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        viewModel.navigator = this
    }

    override fun onResume() {
        super.onResume()
        Firebase.auth.currentUser?.reload()?.addOnSuccessListener {
            if (Firebase.auth.currentUser!!.isEmailVerified) {
                DataUtils.firebaseUser = Firebase.auth.currentUser
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

    }

    override fun getLayoutId(): Int = R.layout.activity_verification

    override fun initViewModel(): VerificationViewModel =
        ViewModelProvider(this)[VerificationViewModel::class.java]
}