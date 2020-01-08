package com.example.ourchat.ui.different_user_profile

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.example.ourchat.R
import com.example.ourchat.Utils.ConstantsUtil
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

const val SENT_REQUEST_ARRAY = "sentRequests"
const val RECEIVED_REQUEST_ARRAY = "receivedRequests"

class DifferentUserProfileFragmentViewModel(val app: Application) : AndroidViewModel(app) {


    val friendRequestState = MutableLiveData<FriendRequestState>()

    var loadedImage = MutableLiveData<RequestBuilder<Drawable>>()

    fun downloadProfilePicture(profilePictureUrl: String?) {
        println("DifferentUserProfileFragmentViewModel.downloadProfilePicture:$profilePictureUrl")
        if (profilePictureUrl == "null") return
        val load: RequestBuilder<Drawable> =
            Glide.with(app).load(profilePictureUrl).placeholder(R.drawable.anonymous_profile)
        loadedImage.value = load
    }

    fun updateSentRequestsForSender(uid: String?) {


        //add id in sentRequest array for logged in user
        val db = FirebaseFirestore.getInstance()
        if (uid != null) {
            ConstantsUtil.AUTH_UID?.let {
                db.collection("users").document(it)
                    .update(SENT_REQUEST_ARRAY, FieldValue.arrayUnion(uid)).addOnSuccessListener {
                        //add loggedInUserId in receivedRequest array for other user
                        updateReceivedRequestsForReceiver(db, uid, ConstantsUtil.AUTH_UID)
                    }.addOnFailureListener {
                        throw it
                    }
            }
        }


    }

    private fun updateReceivedRequestsForReceiver(
        db: FirebaseFirestore,
        uid: String,
        loggedInUserId: String?
    ) {
        db.collection("users").document(uid)
            .update(RECEIVED_REQUEST_ARRAY, FieldValue.arrayUnion(loggedInUserId))
            .addOnSuccessListener {
            }.addOnFailureListener {
                throw it
            }
    }


    enum class FriendRequestState { SENT, NOT_SENT }


    //get document if logged in user and check if other user id is in the sentRequest list
    fun checkIfFriends(uid: String?) {
        val db = FirebaseFirestore.getInstance()
        if (uid != null) {
            ConstantsUtil.AUTH_UID?.let {
                db.collection("users").document(it)
                    .addSnapshotListener(EventListener { it, firebaseFirestoreException ->

                        println("DifferentUserProfileFragmentViewModel.checkIfFriends:")
                        if (firebaseFirestoreException == null) {
                            val user = it?.toObject(com.example.ourchat.data.model.User::class.java)

                            val sentRequests = user?.sentRequests
                            if (sentRequests != null) {
                                for (sentRequest in sentRequests) {
                                    if (sentRequest == uid) {
                                        friendRequestState.value = FriendRequestState.SENT
                                        return@EventListener
                                    }
                                }
                                friendRequestState.value = FriendRequestState.NOT_SENT
                            }

                        } else {
                            //error
                        }
                    })
            }


        }
    }

    fun cancelFriendRequest(uid: String?) {

        //remove id from sentRequest array for logged in user
        val db = FirebaseFirestore.getInstance()
        if (uid != null) {
            ConstantsUtil.AUTH_UID?.let {
                db.collection("users").document(it)
                    .update(SENT_REQUEST_ARRAY, FieldValue.arrayRemove(uid)).addOnSuccessListener {
                        //remove loggedInUserId from receivedRequest array for other user
                        db.collection("users").document(uid)
                            .update(
                                RECEIVED_REQUEST_ARRAY,
                                FieldValue.arrayRemove(ConstantsUtil.AUTH_UID)
                            )
                            .addOnSuccessListener {
                            }.addOnFailureListener {
                            }
                    }.addOnFailureListener {
                    }
            }
        }


    }

}
