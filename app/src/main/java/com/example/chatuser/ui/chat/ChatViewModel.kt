package com.example.chatuser.ui.chat

import android.annotation.SuppressLint
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.chatuser.base.BaseViewModel
import com.example.chatuser.database.addConversationFromFirebase
import com.example.chatuser.database.sendMessageToFireStore
import com.example.chatuser.database.updateConversationFromFirebase
import com.example.chatuser.model.AppUser
import com.example.chatuser.model.Message
import com.example.chatuser.other.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatViewModel : BaseViewModel<Navigator>() {
    val messageField = ObservableField<String>()
    var message: Message? = null
    var sendMessageToUser: AppUser? = null
    fun send() {
        if (!messageField.get()?.trim().isNullOrBlank()) {
            sendMessage()
        }
    }
    @SuppressLint("LongLogTag")
    private fun sendMessage() {
        message = Message(
            senderId = Firebase.auth.currentUser!!.uid,
            receiverId = sendMessageToUser!!.id,
            message = messageField.get()?.trim(),
            dateTime = Date(),
        )
        sendMessageToFireStore(message!!,{
            messageField.set("")
        },{
            msgLiveData.postValue(it.localizedMessage)
        })
    }

    fun goBack() {
        navigator?.back()
    }
}