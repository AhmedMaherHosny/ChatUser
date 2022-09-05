package com.example.chatuser.other

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatuser.R
import com.example.chatuser.databinding.MessageReceivedBinding
import com.example.chatuser.databinding.MessageSentBinding
import com.example.chatuser.model.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var messageList = mutableListOf<Message?>()
    lateinit var receiverProfileImage: Bitmap


    class MessageSentViewHolder(val binding: MessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textMessage.text = message.message
            binding.textDateTime.text = Message.getTimeDate(message.dateTime!!)
        }
    }

    class MessageReceivedViewHolder(val binding: MessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, receiverProfileImage: Bitmap) {
            binding.textMessage.text = message.message
            binding.textDateTime.text = Message.getTimeDate(message.dateTime!!)
            binding.imageProfile.setImageBitmap(receiverProfileImage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return when (message?.senderId) {
            Firebase.auth.currentUser?.uid -> Constants.KEY_SENT
            else -> Constants.KEY_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            Constants.KEY_SENT -> {
                val binding: MessageSentBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.message_sent,
                    parent,
                    false
                )
                return MessageSentViewHolder(binding)
            }
            else -> {
                val binding: MessageReceivedBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.message_received,
                    parent,
                    false
                )
                return MessageReceivedViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageSentViewHolder -> {
                holder.bind(messageList[position]!!)
            }
            is MessageReceivedViewHolder -> {
                holder.bind(messageList[position]!!, receiverProfileImage)
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    fun addMessagesToAdapter(messages: MutableList<Message>) {
        messageList.addAll(messages)
        messageList.sortWith { p0, p1 ->
            p0!!.dateTime!!.compareTo(p1!!.dateTime)
        }
        notifyItemRangeInserted(messageList.size + 1, messages.size)
    }

}