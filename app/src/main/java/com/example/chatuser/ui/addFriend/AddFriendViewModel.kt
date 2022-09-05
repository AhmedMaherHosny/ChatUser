package com.example.chatuser.ui.addFriend

import com.example.chatuser.base.BaseViewModel


class AddFriendViewModel : BaseViewModel<Navigator>() {



    fun backToChat(){
        navigator?.back()
    }
}