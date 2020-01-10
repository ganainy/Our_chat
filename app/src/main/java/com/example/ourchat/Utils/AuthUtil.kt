package com.example.ourchat.Utils

import com.google.firebase.auth.FirebaseAuth

object AuthUtil {

    val firebaseAuthInstance: FirebaseAuth by lazy {
        println("firebaseAuthInstance.:")
        FirebaseAuth.getInstance()
    }

    val authUid: String by lazy {
        println("firebaseAuthInstance.:")
        firebaseAuthInstance.currentUser!!.uid
    }
}