package com.example.ourchat.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.AuthUtil
import com.example.ourchat.Utils.FirestoreUtil
import com.example.ourchat.data.model.ChatParticipant
import com.example.ourchat.data.model.User
import java.util.*


class HomeViewModel : ViewModel() {


    private val chatParticipantList: MutableList<ChatParticipant> by lazy { mutableListOf<ChatParticipant>() }
    private val lastMessageOwnerListMutableLiveData =
        MutableLiveData<MutableList<ChatParticipant>>()
    private val loggedUserMutableLiveData = MutableLiveData<User>()

    /*   fun getChats(): LiveData<MutableList<ChatParticipant>>? {

           FirestoreUtil.firestoreInstance.collection("messages")
               .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                   if (firebaseFirestoreException == null) {

                       chatParticipantList.clear()

                       querySnapshot?.documents?.forEach { documentSnapshot ->
                           if (documentSnapshot.id.contains(AuthUtil.authUid, true)) {

                                                       val lastMessageOwner = ChatParticipant()

                               val messagesList =
                                   documentSnapshot.get("messages") as List<HashMap<String, Any>>?
                               val lastMessage = messagesList?.get(messagesList.size - 1)

                               lastMessageOwner.lastMessage = lastMessage?.get("text") as String
                               lastMessageOwner.lastMessageDate = lastMessage.get("date") as Long


                               val lastMessageOwnerId = lastMessage.get("from") as String


                               if (lastMessageOwnerId == AuthUtil.authUid) {
                                   //last message was typed by logged in user
                                   //leave ownerUser null
                                   lastMessageOwner.particpant = null
                                   chatParticipantList.add(lastMessageOwner)
                                   *//* lastMessageOwnerListMutableLiveData.value =
                                     lastMessageOwnerList*//*
                            } else {
                                //get user who wrote last message
                                FirestoreUtil.firestoreInstance.collection("users")
                                    .document(lastMessageOwnerId).get().addOnSuccessListener {
                                        val mLastMessageOwner = it.toObject(User::class.java)
                                        lastMessageOwner.particpant = mLastMessageOwner
                                        chatParticipantList.add(lastMessageOwner)
                                        *//*      lastMessageOwnerListMutableLiveData.value =
                                                  lastMessageOwnerList*//*
                                    }.addOnFailureListener {

                                    }
                            }


                        }

                        lastMessageOwnerListMutableLiveData.value = chatParticipantList
                    }
                }
            }
        return lastMessageOwnerListMutableLiveData
    }*/

    fun getChats(loggedUser: User): LiveData<MutableList<ChatParticipant>>? {

        val loggedUserId = loggedUser.uid.toString()

        FirestoreUtil.firestoreInstance.collection("messages").whereArrayContains(
            "chat_members",
            loggedUserId
        )
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException == null) {

                    chatParticipantList.clear()

                    if (!querySnapshot?.documents.isNullOrEmpty()) {
                        //user has chats , now get last message and receiver user
                        querySnapshot?.documents?.forEach { messageDocument ->

                            val chatParticipant = ChatParticipant()

                            //get last message & last message sender
                            val messagesList =
                                messageDocument.get("messages") as List<HashMap<String, Any>>?
                            val lastMessage = messagesList?.get(messagesList.size - 1)
                            chatParticipant.lastMessage = lastMessage?.get("text") as String
                            chatParticipant.lastMessageDate = lastMessage.get("date") as Long
                            val lastMessageOwnerId = lastMessage.get("from") as String

                            //set isLoggedUser to know if logged user typed last message or not
                            chatParticipant.isLoggedUser = (lastMessageOwnerId == loggedUserId)

                            //get other chat participant id and use it to get his information
                            val chatMembers = messageDocument.get("chat_members") as List<String>?
                            chatMembers?.forEach { chatMemberId ->
                                if (chatMemberId != loggedUserId) {
                                    //get profile of other chat member
                                    FirestoreUtil.firestoreInstance.collection("users")
                                        .document(chatMemberId).get()
                                        .addOnSuccessListener { chatMember ->
                                            FirestoreUtil.firestoreInstance.collection("users")
                                                .document(chatMemberId).get().addOnSuccessListener {
                                                    val particpant = it.toObject(User::class.java)
                                                    chatParticipant.particpant = particpant
                                                    chatParticipantList.add(chatParticipant)
                                                    lastMessageOwnerListMutableLiveData.value =
                                                        chatParticipantList

                                                }.addOnFailureListener {

                                                }
                                        }
                                }
                            }

                        }
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
