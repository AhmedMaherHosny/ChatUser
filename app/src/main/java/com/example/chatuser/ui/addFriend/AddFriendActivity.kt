package com.example.chatuser.ui.addFriend

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.chatuser.R
import com.example.chatuser.base.BaseActivity
import com.example.chatuser.database.getAllUsers
import com.example.chatuser.databinding.ActivityAddFriendBinding
import com.example.chatuser.model.AppUser
import com.example.chatuser.other.Constants
import com.example.chatuser.other.UsersAdapter
import com.example.chatuser.ui.chat.ChatActivity

class AddFriendActivity : BaseActivity<AddFriendViewModel, ActivityAddFriendBinding>(), Navigator {
    private val adapter = UsersAdapter(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        viewModel.navigator = this
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter.onItemClickListener = object : UsersAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, user: AppUser) {
                startChatActivity(user)
            }
        }
        binding.recyclerView.adapter = adapter
    }

    fun startChatActivity(user:AppUser){
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_COLLECTION_USERS, user)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        getAllUsers({
            val users = it.toObjects(AppUser::class.java)
            adapter.changeData(users)
            binding.textErrorMessage.isVisible = (users.size == 0)
        }, {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
        })
    }

    override fun getLayoutId(): Int = R.layout.activity_add_friend

    override fun initViewModel(): AddFriendViewModel =
        ViewModelProvider(this)[AddFriendViewModel::class.java]

    override fun back() = onBackPressed()
}