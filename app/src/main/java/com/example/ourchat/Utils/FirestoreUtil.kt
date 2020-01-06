package com.example.ourchat.Utils

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreUtil {

    val firestoreInstance: FirebaseFirestore by lazy {
        println("FirestoreUtil.:")
        FirebaseFirestore.getInstance()

    }
}