package com.example.ourchat.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.AuthUtil
import com.example.ourchat.Utils.FirestoreUtil
import com.example.ourchat.data.model.ChatParticipant
import com.example.ourchat.data.model.User
import com.google.firebase.firestore.Query
import java.util.*


class HomeViewModel : ViewModel() {


    private val chatParticipantList: MutableList<ChatParticipant> by lazy { mutableListOf<ChatParticipant>() }
    private val chatParticipantsListMutableLiveData =
        MutableLiveData<MutableList<ChatParticipant>>()
    private val loggedUserMutableLiveData = MutableLiveData<User>()


    fun getChats(loggedUser: User): LiveData<MutableList<ChatParticipant>>? {

        if (chatParticipantsListMutableLiveData.value != null) return chatParticipantsListMutableLiveData

        val loggedUserId = loggedUser.uid.toString()

        val query: Query = FirestoreUtil.firestoreInstance.collection("messages")
            .whereArrayContains("chat_members", loggedUserId)

        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
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

                        //get message or photo url depending on last message type

                        val lastMessageType = lastMessage?.get("type") as Double?
                        chatParticipant.lastMessage = lastMessage?.get("text") as String?
                        chatParticipant.lastMessageType = lastMessageType
                        chatParticipant.lastMessageDate =
                            lastMessage?.get("created_at") as HashMap<String, Double>?
                        println("HomeViewModel.getChats:${chatParticipant.lastMessageDate?.get("seconds")}")
                        val lastMessageOwnerId = lastMessage?.get("from") as String?


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
                                                chatParticipantsListMutableLiveData.value =
                                                    chatParticipantList

                                            }.addOnFailureListener {

                                            }
                                    }
                            }
                        }

                    }
                } else {
                    //user has no chats
                    chatParticipantsListMutableLiveData.value = null
                }
            }
        }
        return chatParticipantsListMutableLiveData
    }

    fun getUserData(): LiveData<User> {

        if (loggedUserMutableLiveData.value != null) return loggedUserMutableLiveData

        FirestoreUtil.firestoreInstance.collection("users").document(AuthUtil.getAuthId())
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException == null) {
                    val loggedUser = documentSnapshot?.toObject(User::class.java)
                    loggedUserMutableLiveData.value = loggedUser
                }
            }
        return loggedUserMutableLiveData
    }


}
