package com.example.ourchat.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.AuthUtil
import com.example.ourchat.Utils.FirestoreUtil
import com.example.ourchat.data.model.LastMessageOwner
import com.example.ourchat.data.model.User
import java.util.*


class HomeViewModel : ViewModel() {


    private val lastMessageOwnerList: MutableList<LastMessageOwner> by lazy { mutableListOf<LastMessageOwner>() }
    private val lastMessageOwnerListMutableLiveData =
        MutableLiveData<MutableList<LastMessageOwner>>()
    private val loggedUserMutableLiveData = MutableLiveData<User>()

    //todo with first message store chat id in all participants  so we here get chats of those ids
    fun getChats(): LiveData<MutableList<LastMessageOwner>>? {

        FirestoreUtil.firestoreInstance.collection("messages")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException == null) {

                    lastMessageOwnerList.clear()

                    querySnapshot?.documents?.forEach { documentSnapshot ->
                        if (documentSnapshot.id.contains(AuthUtil.authUid, true)) {

                            val lastMessageOwner = LastMessageOwner()

                            val messagesList =
                                documentSnapshot.get("messages") as List<HashMap<String, Any>>?
                            val lastMessage = messagesList?.get(messagesList.size - 1)

                            lastMessageOwner.text = lastMessage?.get("text") as String
                            lastMessageOwner.date = lastMessage.get("date") as Long


                            val lastMessageOwnerId = lastMessage.get("from") as String


                            if (lastMessageOwnerId == AuthUtil.authUid) {
                                //last message was typed by logged in user
                                //leave ownerUser null
                                lastMessageOwner.ownerUser = null
                                lastMessageOwnerList.add(lastMessageOwner)
                                /* lastMessageOwnerListMutableLiveData.value =
                                     lastMessageOwnerList*/
                            } else {
                                //get user who wrote last message
                                FirestoreUtil.firestoreInstance.collection("users")
                                    .document(lastMessageOwnerId).get().addOnSuccessListener {
                                        val mLastMessageOwner = it.toObject(User::class.java)
                                        lastMessageOwner.ownerUser = mLastMessageOwner
                                        lastMessageOwnerList.add(lastMessageOwner)
                                        /*      lastMessageOwnerListMutableLiveData.value =
                                                  lastMessageOwnerList*/
                                    }.addOnFailureListener {

                                    }
                            }


                        }

                        lastMessageOwnerListMutableLiveData.value = lastMessageOwnerList
                    }
                }
            }
        return lastMessageOwnerListMutableLiveData
    }


    fun getUserData(): LiveData<User> {
        if (loggedUserMutableLiveData.value != null) return loggedUserMutableLiveData

        FirestoreUtil.firestoreInstance.collection("users").document(AuthUtil.authUid)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException == null) {
                    val loggedUser = documentSnapshot?.toObject(User::class.java)
                    loggedUserMutableLiveData.value = loggedUser
                }
        }
        return loggedUserMutableLiveData
    }


}
