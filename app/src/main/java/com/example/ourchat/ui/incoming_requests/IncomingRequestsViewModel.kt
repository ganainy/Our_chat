package com.example.ourchat.ui.incoming_requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ourchat.Utils.AuthUtil
import com.example.ourchat.Utils.FirestoreUtil
import com.example.ourchat.data.model.User
import com.example.ourchat.ui.different_user_profile.RECEIVED_REQUEST_ARRAY
import com.example.ourchat.ui.different_user_profile.SENT_REQUEST_ARRAY
import com.google.firebase.firestore.FieldValue

const val FRIENDS = "friends"


class IncomingRequestsViewModel : ViewModel() {

    private val usersRef = FirestoreUtil.firestoreInstance.collection("users")
    private val friendRequestersMutableLiveData = MutableLiveData<MutableList<User>?>()


    //get info of the users that sent friend requests
    fun downloadRequests(receivedRequests: List<String>): LiveData<MutableList<User>?> {

        val friendRequesters = mutableListOf<User>()

        for (receivedRequest in receivedRequests) {
            usersRef.document(receivedRequest).get().addOnSuccessListener {
                val user = it?.toObject(User::class.java)
                user?.let { it1 -> friendRequesters.add(it1) }
                friendRequestersMutableLiveData.value = friendRequesters

            }.addOnFailureListener {
                friendRequestersMutableLiveData.value = null
            }
        }
        return friendRequestersMutableLiveData
    }


    fun addToFriends(
        requesterId: String?,
        loggedUser: User
    ) {

        deleteRequest(requesterId, loggedUser)

        //add id in sentRequest array for logged in user
        if (requesterId != null) {
            AuthUtil.authUid.let { requesterId ->
                FirestoreUtil.firestoreInstance.collection("users").document(requesterId)
                    .update(FRIENDS, FieldValue.arrayUnion(requesterId)).addOnSuccessListener {
                        //add loggedInUserId in receivedRequest array for other user
                        FirestoreUtil.firestoreInstance.collection("users").document(requesterId)
                            .update(FRIENDS, FieldValue.arrayUnion(AuthUtil.authUid))
                            .addOnSuccessListener {

                            }.addOnFailureListener {

                            }
                    }.addOnFailureListener {

                    }
            }
        }


    }


    fun deleteRequest(
        requesterId: String?,
        loggedUser: User
    ) {

        //remove id from sentRequest array for logged in user
        if (requesterId != null) {
            loggedUser.uid?.let { loggedUserId ->
                FirestoreUtil.firestoreInstance.collection("users").document(loggedUserId)
                    .update(RECEIVED_REQUEST_ARRAY, FieldValue.arrayRemove(requesterId))
                    .addOnSuccessListener {
                        //remove loggedInUserId from receivedRequest array for other user
                        FirestoreUtil.firestoreInstance.collection("users").document(requesterId)
                            .update(
                                SENT_REQUEST_ARRAY,
                                FieldValue.arrayRemove(AuthUtil.authUid)
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

