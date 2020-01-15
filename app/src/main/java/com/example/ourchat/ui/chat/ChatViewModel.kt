package com.example.ourchat.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.FirestoreUtil
import com.example.ourchat.data.model.Message
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import java.util.*


class ChatViewModel(val senderId: String?, val receiverId: String) : ViewModel() {

    private val messageCollectionReference = FirestoreUtil.firestoreInstance.collection("messages")
    private val messagesList: MutableList<Message> by lazy { mutableListOf<Message>() }


    private val messagesMutableLiveData = MutableLiveData<List<Message>>()

    fun loadMessages(): LiveData<List<Message>> {

        messageCollectionReference.addSnapshotListener(EventListener { querySnapShot, firebaseFirestoreException ->
            if (firebaseFirestoreException == null) {
                messagesList.clear()//clear message list so won't get duplicated with each new message
                querySnapShot?.documents?.forEach {
                    if (it.id == "${senderId}_${receiverId}" || it.id == "${receiverId}_${senderId}") {
                        //this is the chat document we should read messages array
                        val messagesFromFirestore =
                            it.get("messages") as List<HashMap<String, Any>>?
                                ?: throw Exception("My cast can't be done")
                        messagesFromFirestore.forEach { message ->
                            val message = Message(
                                message["from"].toString(),
                                message["date"] as Long,
                                message["text"].toString(),
                                message["image_uri"].toString(),
                                message["file_uri"].toString(),
                                message["file_name"].toString(),
                                message["type"] as Long?
                            )
                            messagesList.add(message)
                        }
                        messagesMutableLiveData.value = messagesList
                    }

                }
            }
        })

        return messagesMutableLiveData
    }


    fun sendMessage(message: String?, uri: String?, fileName: String?, type: Long) {
        /**
         * 0-> text
         * 1-> photo
         * 2-> audio
         * 3-> file
         * */

        //create date
        val date = Date()
        val timeMilli: Long = date.time

//create message hashmap depending on message type

        val messageMap = when (type) {

            0L -> {
                mapOf(
                    "date" to timeMilli,
                    "from" to senderId,
                    "text" to message,
                    "type" to 0
                )
            }
            1L -> {
                mapOf(
                    "date" to timeMilli,
                    "from" to senderId,
                    "image_uri" to uri,
                    "type" to 1
        )
            }
            3L -> {
                mapOf(
                    "date" to timeMilli,
                    "from" to senderId,
                    "file_uri" to uri,
                    "file_name" to fileName,
                    "type" to 3
                )
            }
            else -> throw java.lang.Exception("uknown type")
        }




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

                                        //add ids of chat members
                                        messageCollectionReference.document("${senderId}_${receiverId}")
                                            .update(
                                                "chat_members",
                                                FieldValue.arrayUnion(senderId, receiverId)
                                            )

                                    }
                            }
                        }
                }
            }

    }


    /* fun sendImageMessage(chatImageUri: Uri) {


         //create message map
         val date = Date()
         val timeMilli: Long = date.time

         val messageMap = mapOf(
             "date" to timeMilli,
             "from" to senderId,
             "image_uri" to chatImageUri.toString()
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

                                         //add ids of chat members
                                         messageCollectionReference.document("${senderId}_${receiverId}")
                                             .update(
                                                 "chat_members",
                                                 FieldValue.arrayUnion(senderId, receiverId)
                                             )

                                     }
                             }
                         }
                 }
             }

     }*/
}
