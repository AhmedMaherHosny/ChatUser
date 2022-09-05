package com.example.chatuser.other

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatuser.databinding.ItemUserBinding
import com.example.chatuser.model.AppUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UsersAdapter(private var users: MutableList<AppUser>?) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    companion object {
        fun decodeUserImage(encodedImage: String): Bitmap {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: AppUser) {
            binding.textName.text = user.userName
            binding.textEmail.text = user.email
            binding.imageProfile.setImageBitmap(decodeUserImage(user.imageId!!))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeData(users: MutableList<AppUser>?) {
        run loop@{
            users?.forEach {
                if (it.id.equals(Firebase.auth.currentUser!!.uid)) {
                    users.remove(it)
                    return@loop
                }
            }
        }
        this.users = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users!![position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position, user)
        }
    }

    override fun getItemCount(): Int = users?.size ?: 0

    var onItemClickListener :OnItemClickListener?=null
    interface OnItemClickListener{
        fun onItemClick(position: Int, user: AppUser)
    }

}