package com.example.ourchat.ui.main_activity

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.ConstantsUtil
import com.example.ourchat.Utils.LoadState
import com.example.ourchat.data.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class SharedViewModel : ViewModel() {

    val uploadState = MutableLiveData<LoadState>()
    val loadState = MutableLiveData<LoadState>()
    private lateinit var mStorageRef: StorageReference
    private var usersCollectionRef: CollectionReference =
        FirebaseFirestore.getInstance().collection("users")
    private val userDocRef: DocumentReference by lazy { usersCollectionRef.document(ConstantsUtil.AUTH_UID!!) }
    var friendsList = MutableLiveData<List<com.example.ourchat.data.model.User>>()



    fun uploadImageAsBytearray(bytes: ByteArray) {

        //show upload ui
        uploadState.value = LoadState.LOADING

        mStorageRef = FirebaseStorage.getInstance().reference
        val ref = mStorageRef.child("profile_pictures/" + System.currentTimeMillis())
        var uploadTask = bytes.let { ref.putBytes(it) }

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                saveImageUriInFirebase(downloadUri)
            } else {
                println("SharedViewModel.uploadImageByUri:${task.exception}")
                uploadState.value = LoadState.FAILURE
            }
        }


    }


    fun uploadImageByUri(data: Uri?) {

        //show upload ui
        uploadState.value = LoadState.LOADING

        mStorageRef = FirebaseStorage.getInstance().reference
        val ref = mStorageRef.child("profile_pictures/" + data?.path)
        var uploadTask = data?.let { ref.putFile(it) }

        val urlTask = uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                saveImageUriInFirebase(downloadUri)
            } else {
                println("SharedViewModel.uploadImageByUri:${task.exception}")
                uploadState.value = LoadState.FAILURE
            }
        }


    }


    //save field of storage uri of image in the user document
    private fun saveImageUriInFirebase(downloadUri: Uri?) {
        val db = FirebaseFirestore.getInstance()

        ConstantsUtil.AUTH_UID?.let {
            db.collection("users").document(it)
                .update("profile_picture_url", downloadUri.toString())
                .addOnSuccessListener {
                    uploadState.value = LoadState.SUCCESS

                }
                .addOnFailureListener {
                    uploadState.value = LoadState.FAILURE
                }
        }



    }


    fun loadFriends(): LiveData<List<User>> {

        if (ConstantsUtil.AUTH_UID == null) return friendsList



        userDocRef.addSnapshotListener(EventListener { snapShopt, firebaseFirestoreException ->
            if (firebaseFirestoreException == null) {
                val user = snapShopt?.toObject(User::class.java)
                val friendsIds = user?.friends
                if (friendsIds != null && friendsIds.isNotEmpty()) {
                    val mFriendList = mutableListOf<User>()
                    for (friendId in friendsIds) {
                        usersCollectionRef.document(friendId).get().addOnSuccessListener {
                            val friend =
                                it.toObject(User::class.java)
                            friend?.let { user -> mFriendList.add(user) }
                            friendsList.value = mFriendList
                        }
                    }

                } else {
                    friendsList.value = null
                }
            } else {
                friendsList.value = null
            }
        })
        return friendsList
    }




    fun showLoadState(mLoadState: LoadState) {
        loadState.value = mLoadState
        println("SharedViewModel.showLoadState:${loadState.value.toString()}")
    }

    val imageBitmap = MutableLiveData<Bitmap>()
    val galleryImageUri = MutableLiveData<Uri>()


    var profileImageUrlMutableLiveData = MutableLiveData<String>()


    fun downloadProfileImage(): LiveData<String> {

        userDocRef.get().addOnSuccessListener { document ->
            var profileImageUrl = document.get("profile_picture_url").toString()
            profileImageUrlMutableLiveData.value = profileImageUrl


        }
            .addOnFailureListener { exception ->
            }

        return profileImageUrlMutableLiveData
    }
}