package com.example.ourchat.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.ourchat.Utils.ConstantsUtil
import com.example.ourchat.Utils.LoadState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore


class ProfileViewModel(val app: Application) : AndroidViewModel(app) {

    private var usersCollectionRef: CollectionReference =
        FirebaseFirestore.getInstance().collection("users")
    private var userDocRef: DocumentReference? = ConstantsUtil.AUTH_UID?.let {
        usersCollectionRef.document(
            it
        )
    }

    var bioLoadState = MutableLiveData<LoadState>()
    var bio = MutableLiveData<String>()


    fun updateBio(bio: String) {

        bioLoadState.value = LoadState.LOADING

        userDocRef?.update("bio", bio)
            ?.addOnSuccessListener {
                bioLoadState.value = LoadState.SUCCESS

            }
            ?.addOnFailureListener {
                bioLoadState.value = LoadState.FAILURE
            }

    }


    fun downloadBio() {
        bioLoadState.value = LoadState.LOADING

        userDocRef?.addSnapshotListener(EventListener { document, firebaseFirestoreException ->
            if (firebaseFirestoreException == null) {
                bioLoadState.value = LoadState.SUCCESS
                bio.value = document?.get("bio")?.toString() ?: "No bio"
            } else {
                bioLoadState.value = LoadState.FAILURE
            }
        })

    }






}
