package com.example.chatuser.ui.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.lifecycle.ViewModelProvider
import com.example.chatuser.R
import com.example.chatuser.base.BaseActivity
import com.example.chatuser.databinding.ActivityHomeBinding
import com.example.chatuser.other.Constants
import com.example.chatuser.other.PreferenceManger
import com.example.chatuser.ui.addFriend.AddFriendActivity
import com.example.chatuser.ui.singIn.SignInActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>(), Navigator {
    private lateinit var preferenceManger: PreferenceManger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        viewModel.navigator = this
        preferenceManger = PreferenceManger(applicationContext)
        loadUserData()
        viewModel.getTokenFromFirebaseUtils()
        ifSignOut()
    }

    private fun ifSignOut() { // clear the session
        viewModel.prefClear.observe(this) {
            if (it) {
                preferenceManger.clear()
                Firebase.auth.signOut()
            }
        }
    }

    private fun loadUserData() {
        val bytes = Base64.decode(preferenceManger.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
        binding.textName.text = preferenceManger.getString(Constants.KEY_USERNAME)
    }

    override fun getLayoutId(): Int = R.layout.activity_home

    override fun initViewModel(): HomeViewModel =
        ViewModelProvider(this)[HomeViewModel::class.java]

    override fun signOutMoveToSignInActivity() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    override fun goToAddFriend() = startActivity(Intent(this, AddFriendActivity::class.java))
}