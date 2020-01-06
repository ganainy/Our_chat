package com.example.ourchat.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.data.model.Message
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*


class ChatViewModel(val senderId: String, val receiverId: String) : ViewModel() {

    private val messageCollectionReference = FirebaseFirestore.getInstance().collection("messages")

    init {
        //todo loadMessages()
    }

    private val messages = MutableLiveData<List<Message>>()

    private fun loadMessages(): MutableLiveData<List<Message>> {

        //todo
        messageCollectionReference.addSnapshotListener(EventListener { querySnapShot, firebaseFirestoreException ->
            if (firebaseFirestoreException == null) {

            }
        })




        return messages
    }


    fun sendMessage(message: String) {


        //create message map
        val date = Date()
        val timeMilli: Long = date.time

        val messageMap = mapOf(
            "date" to timeMilli,
            "from" to senderId,
            "text" to message
        )


        //so we don't create multiple nodes for same chat
        messageCollectionReference.document("${senderId}_${receiverId}").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    //this node exists send your message
                    messageCollectionReference.document("${senderId}_${receiverId}")
                        .update("messages", FieldValue.arrayUnion(messageMap))

                } else {
                    //senderId_receiverId node doesn't exist check receiverId_senderId
                    messageCollectionReference.document("${receiverId}_${senderId}").get()
                        .addOnSuccessListener { documentSnapshot ->

                            if (documentSnapshot.exists()) {
                                messageCollectionReference.document("${receiverId}_${senderId}")
                                    .update("messages", FieldValue.arrayUnion(messageMap))
                            } else {
                                //no previous chat history(senderId_receiverId & receiverId_senderId both don't exist)
                                //so we create document senderId_receiverId then messages array then add messageMap to messages
                                messageCollectionReference.document("${senderId}_${receiverId}")
                                    .set(
                                        mapOf("messages" to mutableListOf<Message>()),
                                        SetOptions.merge()
                                    ).addOnSuccessListener {
                                        //this node exists send your message
                                        messageCollectionReference.document("${senderId}_${receiverId}")
                                            .update("messages", FieldValue.arrayUnion(messageMap))

                                    }
                            }
                        }
                }
            }

    }
}
