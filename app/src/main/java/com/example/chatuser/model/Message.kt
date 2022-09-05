package com.example.chatuser.model

import java.text.SimpleDateFormat
import java.util.*

data class Message(
    var messageId: String? = null,
    val senderId: String? = null,
    val receiverId: String? = null,
    var message: String? = null,
    val dateTime: Date? = null,
    ) {
    companion object {
        fun getTimeDate(date: Date): String? {
            return try {
                val sfd = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault())
                sfd.format(date)
            } catch (e: Exception) {
                "date"
            }
        }
    }
}
