package com.example.chatuser.ui.chat

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.chatuser.R
import com.example.chatuser.base.BaseActivity
import com.example.chatuser.database.checkForConversationFromFirebaseRemotely
import com.example.chatuser.database.getAllMessages
import com.example.chatuser.databinding.ActivityChatBinding
import com.example.chatuser.model.AppUser
import com.example.chatuser.model.Message
import com.example.chatuser.other.ChatAdapter
import com.example.chatuser.other.Constants
import com.example.chatuser.other.PreferenceManger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.HashMap

class ChatActivity : BaseActivity<ChatViewModel, ActivityChatBinding>(), Navigator {
    private lateinit var pressedUser: AppUser
    private var messages = mutableListOf<Message>()
    private var adapter = ChatAdapter()
    private lateinit var preferenceManger: PreferenceManger
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        viewModel.navigator = this
        loadUserData()
        init()
        getMessages()
    }

    private fun initConversation() {
        val conversation = HashMap<String, Any>()
        conversation[Constants.KEY_SENDER_ID] = preferenceManger.getString(Constants.KEY_USER_ID)!!
        conversation[Constants.KEY_SENDER_NAME] = preferenceManger.getString(Constants.KEY_USERNAME)!!
        conversation[Constants.KEY_SENDER_IMAGE] = preferenceManger.getString(Constants.KEY_IMAGE)!!
        conversation[Constants.KEY_RECEIVER_ID] = pressedUser.id!!
        conversation[Constants.KEY_RECEIVER_NAME] = pressedUser.userName!!
        conversation[Constants.KEY_RECEIVER_IMAGE] = pressedUser.imageId!!
        conversation[Constants.KEY_TIMESTAMP] = Date()
    }

    private fun getBitmapFromEncodedImage(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun getMessages() {
        getAllMessages(preferenceManger.getString(Constants.KEY_USER_ID)!!,
            pressedUser.id!!,
            eventListener)
    }

    private fun init() {
        viewModel.sendMessageToUser = pressedUser
        preferenceManger = PreferenceManger(applicationContext)
        adapter.receiverProfileImage = getBitmapFromEncodedImage(pressedUser.imageId!!)
        binding.chatRecyclerView.adapter = adapter
    }


    private fun loadUserData() {
        pressedUser = intent.getParcelableExtra(Constants.KEY_COLLECTION_USERS)!!
        binding.textName.text = pressedUser.userName
    }

    override fun getLayoutId(): Int = R.layout.activity_chat

    override fun initViewModel(): ChatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
    override fun back() = onBackPressed()

    @SuppressLint("NotifyDataSetChanged")
    private val eventListener: EventListener<QuerySnapshot> = EventListener { value, error ->
        if (error != null)
            return@EventListener
        messages.clear()
        value?.documentChanges?.forEach {
            if (it.type == DocumentChange.Type.ADDED) {
                messages.add(it.document.toObject(Message::class.java))
            }
        }
        adapter.addMessagesToAdapter(messages)
        binding.chatRecyclerView.smoothScrollToPosition(adapter.itemCount)
    }



}
