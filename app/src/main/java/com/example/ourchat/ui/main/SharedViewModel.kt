package com.example.ourchat.ui.main

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.LoadState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class SharedViewModel : ViewModel() {

    val uploadState = MutableLiveData<LoadState>()
    val loadState = MutableLiveData<LoadState>()
    private lateinit var mStorageRef: StorageReference

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
        db.collection("users").document(FirebaseAuth.getInstance().uid!!)
            .update("profile_picture_url", downloadUri.toString())
            .addOnSuccessListener {
                uploadState.value = LoadState.SUCCESS

            }
            .addOnFailureListener {
                uploadState.value = LoadState.FAILURE
            }


    }


    fun showLoadState(mLoadState: LoadState) {
        loadState.value = mLoadState
        println("SharedViewModel.showLoadState:${loadState.value.toString()}")
    }

    val imageBitmap = MutableLiveData<Bitmap>()
    val galleryImageUri = MutableLiveData<Uri>()
}