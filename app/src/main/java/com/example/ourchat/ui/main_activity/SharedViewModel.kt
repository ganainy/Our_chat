package com.example.ourchat.ui.main_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.FirestoreUtil
import com.example.ourchat.data.model.User
import com.google.firebase.firestore.CollectionReference


class SharedViewModel : ViewModel() {


    private var friendsListMutableLiveData =
        MutableLiveData<List<com.example.ourchat.data.model.User>>()
    private var usersCollectionRef: CollectionReference =
        FirestoreUtil.firestoreInstance.collection("users")




    fun loadFriends(loggedUser: User): LiveData<List<User>> {

        val friendsIds = loggedUser.friends
        if (!friendsIds.isNullOrEmpty()) {
                    val mFriendList = mutableListOf<User>()
                    for (friendId in friendsIds) {
                        usersCollectionRef.document(friendId).get()
                            .addOnSuccessListener { friendUser ->
                            val friend =
                                friendUser.toObject(User::class.java)
                            friend?.let { user -> mFriendList.add(user) }
                            friendsListMutableLiveData.value = mFriendList
                        }
                    }
                } else {
            //user has no friends
                    friendsListMutableLiveData.value = null
                }

        return friendsListMutableLiveData
    }



}