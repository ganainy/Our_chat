package com.example.ourchat.ui.main_activity

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.*
import com.example.ourchat.data.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference


class SharedViewModel : ViewModel() {

    val loadStateMutableLiveData = MutableLiveData<LoadState>()
    val uploadImageLoadStateMutableLiveData = MutableLiveData<LoadState>()
    private var friendsListMutableLiveData =
        MutableLiveData<List<com.example.ourchat.data.model.User>>()
    private lateinit var mStorageRef: StorageReference
    private var usersCollectionRef: CollectionReference =
        FirestoreUtil.firestoreInstance.collection("users")



    fun uploadImageAsBytearray(bytes: ByteArray) {

        //show upload ui
        loadStateMutableLiveData.value = LoadState.LOADING

        mStorageRef = StorageUtil.storageInstance.reference
        val ref = mStorageRef.child("profile_pictures/" + System.currentTimeMillis())
        var uploadTask = bytes.let { ref.putBytes(it) }

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                uploadImageLoadStateMutableLiveData.value = LoadState.FAILURE
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                saveImageUriInFirebase(downloadUri)
            } else {
                uploadImageLoadStateMutableLiveData.value = LoadState.FAILURE
            }
        }


    }

    //save download uri of image in the user document
    private fun saveImageUriInFirebase(downloadUri: Uri?) {

        AuthUtil.getAuthId().let {
            FirestoreUtil.firestoreInstance.collection("users").document(it)
                .update(PROFILE_PICTURE_URL, downloadUri.toString())
                .addOnSuccessListener {
                    uploadImageLoadStateMutableLiveData.value = LoadState.SUCCESS

                }
                .addOnFailureListener {
                    uploadImageLoadStateMutableLiveData.value = LoadState.FAILURE
                }
        }

    }


    fun uploadImageByUri(data: Uri?) {
        //show upload ui
        uploadImageLoadStateMutableLiveData.value = LoadState.LOADING

        mStorageRef = StorageUtil.storageInstance.reference
        val ref = mStorageRef.child("profile_pictures/" + data?.path)
        var uploadTask = data?.let { ref.putFile(it) }

        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                uploadImageLoadStateMutableLiveData.value = LoadState.FAILURE
            }
            ref.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                saveImageUriInFirebase(downloadUri)
            } else {
                uploadImageLoadStateMutableLiveData.value = LoadState.FAILURE
            }
        }

    }


    fun loadFriends(loggedUser: User): LiveData<List<User>> {

        val friendsIds = loggedUser.friends
                if (friendsIds != null && friendsIds.isNotEmpty()) {
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
                    friendsListMutableLiveData.value = null
                }

        return friendsListMutableLiveData
    }


    //used by facebook login fragment to show loading layout from main activity
    fun showLoadState(mLoadState: LoadState) {
        loadStateMutableLiveData.value = mLoadState
    }

    //used to pass images from main activity(on activity result) to profile fragment to show new choosen picture
    val imageBitmap = MutableLiveData<Bitmap>()
    val galleryImageUri = MutableLiveData<Uri>()





}