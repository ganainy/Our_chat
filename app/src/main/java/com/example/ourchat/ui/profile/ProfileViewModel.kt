package com.example.ourchat.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.ourchat.Utils.AuthUtil
import com.example.ourchat.Utils.FirestoreUtil
import com.example.ourchat.Utils.LoadState
import com.google.firebase.firestore.DocumentReference


class ProfileViewModel(val app: Application) : AndroidViewModel(app) {


    private var userDocRef: DocumentReference? = AuthUtil.authUid.let {
        FirestoreUtil.firestoreInstance.collection("users").document(it)
    }

    var bioLoadState = MutableLiveData<LoadState>()


    fun updateBio(bio: String) {


        userDocRef?.update("bio", bio)
            ?.addOnSuccessListener {
                bioLoadState.value = LoadState.SUCCESS

            }
            ?.addOnFailureListener {
                bioLoadState.value = LoadState.FAILURE
            }

    }

/*

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
*/






}
