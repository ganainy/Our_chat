package com.example.ourchat.ui.chat

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.FirestoreUtil
import com.example.ourchat.Utils.StorageUtil
import com.example.ourchat.data.model.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*


class ChatViewModel(val senderId: String?, val receiverId: String) : ViewModel() {

    private lateinit var mStorageRef: StorageReference
    private val messageCollectionReference = FirestoreUtil.firestoreInstance.collection("messages")
    private val messagesList: MutableList<Message> by lazy { mutableListOf<Message>() }
    private val chatFileMapMutableLiveData = MutableLiveData<Map<String, Any?>>()
    private val messagesMutableLiveData = MutableLiveData<List<Message>>()
    private val chatImageDownloadUriMutableLiveData = MutableLiveData<Uri>()
    private val chatRecordDownloadUriMutableLiveData = MutableLiveData<Uri>()


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
                        messagesFromFirestore.forEach { messageHashMap ->

                            val message = when (messageHashMap["type"] as Double?) {
                                0.0 -> {
                                    messageHashMap.toDataClass<TextMessage>()
                                }
                                1.0 -> {
                                    messageHashMap.toDataClass<ImageMessage>()
                                }
                                2.0 -> {
                                    messageHashMap.toDataClass<FileMessage>()
                                }
                                3.0 -> {
                                    messageHashMap.toDataClass<RecordMessage>()
                                }
                                else -> {
                                    throw Exception("unknown type")
                                }
                            }


                            messagesList.add(message)
                        }

                        if (!messagesList.isNullOrEmpty())
                            messagesMutableLiveData.value = messagesList
                    }

                }
            }
        })

        return messagesMutableLiveData
    }


    /*  fun sendMessage(message: String?, uri: String?, fileName: String?, type: Long) {
          */
    /**
         * 0-> text
         * 1-> photo
     * 2-> file
     * 3-> audio
     * *//*

        //create date
        val date = Date()
        val timeMilli: Long = date.time

//todo create message hashmap depending on message type

        val messageMap = when (type) {
            0L -> {

            }
            1L -> {

            }
            2L -> {

            }
            3L -> {

            }
            else -> {
throw Exception("unknown type")
            }
        }

        mapOf(
            "date" to timeMilli,
            "from" to senderId,
            "uri" to uri,
            "text" to message,
            "name" to fileName,
            "type" to type
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

    fun sendMessage(message: Message) {

        //so we don't create multiple nodes for same chat
        messageCollectionReference.document("${senderId}_${receiverId}").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    //this node exists send your message
                    messageCollectionReference.document("${senderId}_${receiverId}")
                        .update("messages", FieldValue.arrayUnion(message.serializeToMap()))

                } else {
                    //senderId_receiverId node doesn't exist check receiverId_senderId
                    messageCollectionReference.document("${receiverId}_${senderId}").get()
                        .addOnSuccessListener { documentSnapshot ->

                            if (documentSnapshot.exists()) {
                                messageCollectionReference.document("${receiverId}_${senderId}")
                                    .update(
                                        "messages",
                                        FieldValue.arrayUnion(message.serializeToMap())
                                    )
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
                                            .update(
                                                "messages",
                                                FieldValue.arrayUnion(message.serializeToMap())
                                            )

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


    fun uploadChatFileByUri(filePath: Uri?): LiveData<Map<String, Any?>> {

        mStorageRef = StorageUtil.storageInstance.reference
        val ref = mStorageRef.child("chat_files/" + filePath.toString())
        var uploadTask = filePath?.let { ref.putFile(it) }

        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                //error
                println("SharedViewModel.uploadChatImageByUri:error1 ${task.exception?.message}")
            }
            ref.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                println("SharedViewModel.uploadChatImageByUri:on complete")
                chatFileMapMutableLiveData.value = mapOf<String, Any?>(
                    "downloadUri" to downloadUri,
                    "fileName" to filePath
                )


            } else {
                //error
                println("SharedViewModel.uploadChatImageByUri:error2 ${task.exception?.message}")
            }
        }
        return chatFileMapMutableLiveData
    }


    fun uploadRecord(filePath: String): LiveData<Uri> {

        mStorageRef = StorageUtil.storageInstance.reference
        val ref = mStorageRef.child("records/" + Date().time)
        var uploadTask = ref.putFile(Uri.fromFile(File(filePath)))


        println("ChatViewModel.uploadRecord:${Uri.fromFile(File(filePath))}")
        println("ChatViewModel.uploadRecord:${filePath}")

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                //error
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                chatRecordDownloadUriMutableLiveData.value = downloadUri
            } else {
                //error
            }
        }

        return chatRecordDownloadUriMutableLiveData
    }

    fun uploadChatImageByUri(data: Uri?): LiveData<Uri> {
        mStorageRef = StorageUtil.storageInstance.reference
        val ref = mStorageRef.child("chat_pictures/" + data?.path)
        var uploadTask = data?.let { ref.putFile(it) }

        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                //error
            }
            ref.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                chatImageDownloadUriMutableLiveData.value = downloadUri

            } else {
                //error
            }
        }
        return chatImageDownloadUriMutableLiveData
    }


    fun updateRecord(recordMessage: RecordMessage) {
        /*  FirestoreUtil.firestoreInstance.collection("messages").document("${senderId}_${receiverId}").get().addOnSuccessListener {
                  if(it.exists()){
                      val messagesList = it["messages"] as MutableList<Message>?
                      if (messagesList != null) {
                          for ((index, message) in messagesList.withIndex()){
                              if (message.date==recordMessage.date){
                                  //this is the record we want to update

                              }
                          }
                      }
                  }else{
                      //try receiver_sender document
                      val messagesList = it["messages"] as MutableList<Message>?
                      if (messagesList != null) {
                          for ((index, message) in messagesList.withIndex()){
                              if (message.date==recordMessage.date){
                                  //this is the record we want to update
                                  FirestoreUtil.firestoreInstance.collection("messages").document("${receiverId}_${senderId}")
                                      .update("messages",FieldValue.arrayRemove(recordMessage))
                              }
                          }
                      }
                  }
              }*/
        recordMessage.currentProgress = null
        recordMessage.duration = null
        println("ChatViewModel.updateRecord:${recordMessage.serializeToMap()}")


        //todo replace this hard coded parts with  recordMessage and add  senderId_receiver and then add new record updated map and connect this with
        //play pause or remove the damn whole thing
        FirestoreUtil.firestoreInstance.collection("messages").document("${receiverId}_${senderId}")
            .update("messages", FieldValue.arrayRemove(recordMessage.serializeToMap()))/*mapOf("date" to 1579731366117,
                "from" to "Hi0eUYTQ0LWnSQU2kWnZ5dFuLRv2",
                "name" to null,
                "text" to null,
                "type" to 3 ,
                "uri" to "https://firebasestorage.googleapis.com/v0/b/our-chat-a76bb.appspot.com/o/records%2F1579731363763?alt=media&token=6ba12e4e-f1ac-445b-9abd-4dce4ae8abc4")))
               */.addOnSuccessListener {
            println("ChatViewModel.updateRecord:")
        }.addOnFailureListener {
                println("ChatViewModel.updateRecord:${it.message}")
            }


        /*   FirestoreUtil.firestoreInstance.collection("messages").document("${receiverId}_${senderId}")
               .update("messages",FieldValue.arrayRemove(recordMessage.serializeToMap())).addOnSuccessListener {
                   println("ChatViewModel.updateRecord:2")
               }.addOnFailureListener {
                   println("ChatViewModel.updateRecord:${it.message}")
               }
   */


    }


}


val gson = Gson()

//convert a data class to a map
fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}

//convert a map to a data class
inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}



