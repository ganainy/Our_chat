package com.example.ourchat.ui.profile

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.example.ourchat.data.LoadState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel(val app: Application) : AndroidViewModel(app) {



    private lateinit var db: FirebaseFirestore

    var bioUploadState = MutableLiveData<LoadState>()
    var bioDownloadState = MutableLiveData<LoadState>()
    var profileImageDownloadState = MutableLiveData<LoadState>()
    var loadedImage = MutableLiveData<RequestBuilder<Drawable>>()


    var bio = MutableLiveData<String>()

    fun uploadBio(bio: String) {

        bioUploadState.value = LoadState.UPLOADING
        db = FirebaseFirestore.getInstance()
        db.collection("users").document(FirebaseAuth.getInstance().uid!!)
            .update("bio", bio)
            .addOnSuccessListener {
                bioUploadState.value = LoadState.SUCCESS

            }
            .addOnFailureListener {
                bioUploadState.value = LoadState.FAILURE
            }
    }

    fun downloadBio() {
        bioDownloadState.value = LoadState.DOWNLOADING
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(FirebaseAuth.getInstance().uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                bioDownloadState.value = LoadState.SUCCESS
                bio.value = document.get("bio")?.toString() ?: "No bio"

            }
            .addOnFailureListener { exception ->
                bioDownloadState.value = LoadState.FAILURE
            }
    }

    fun downloadProfileImage() {

        profileImageDownloadState.value = LoadState.DOWNLOADING
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(FirebaseAuth.getInstance().uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                var profileImageUrl = document.get("profile_picture_url").toString()
                loadedImage.value = Glide.with(app).load(profileImageUrl)
                profileImageDownloadState.value = LoadState.SUCCESS

            }
            .addOnFailureListener { exception ->
                profileImageDownloadState.value = LoadState.FAILURE
            }


    }
}
