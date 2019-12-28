package com.example.ourchat.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    enum class BioState { UPLOADING, SUCCESS, FAILURE, DOWNLOADING }

    private lateinit var db: FirebaseFirestore

    var bioUploadState = MutableLiveData<BioState>()
    var bioDownloadState = MutableLiveData<BioState>()
    var bio = MutableLiveData<String>()

    fun uploadBio(bio: String) {

        bioUploadState.value = BioState.UPLOADING
        db = FirebaseFirestore.getInstance()
        db.collection("users").document(FirebaseAuth.getInstance().uid!!)
            .update("bio", bio)
            .addOnSuccessListener {
                bioUploadState.value = BioState.SUCCESS

            }
            .addOnFailureListener {
                bioUploadState.value = BioState.FAILURE
            }
    }

    fun downloadBio() {
        bioDownloadState.value = BioState.DOWNLOADING
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(FirebaseAuth.getInstance().uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                bioDownloadState.value = BioState.SUCCESS
                bio.value = document.get("bio")?.toString() ?: "No bio"

            }
            .addOnFailureListener { exception ->
                bioDownloadState.value = BioState.FAILURE
            }
    }

    fun downloadProfileImage() {

    }

}
