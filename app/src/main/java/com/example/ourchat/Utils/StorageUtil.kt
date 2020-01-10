package com.example.ourchat.Utils

import com.google.firebase.storage.FirebaseStorage

object StorageUtil {

    val storageInstance: FirebaseStorage by lazy {
        println("StorageUtil.:")
        FirebaseStorage.getInstance()

    }
}