package com.example.chatuser.database

import com.example.chatuser.model.AppUser
import com.example.chatuser.model.Message
import com.example.chatuser.other.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.collections.HashMap

fun getCollection(collectionName: String): CollectionReference {
    val db = Firebase.firestore
    return db.collection(collectionName)
}

fun addUserToFirestore(
    user: AppUser,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener,
) {
    val userCollection = getCollection(Constants.KEY_COLLECTION_USERS)
    val userDocument = userCollection.document(user.id!!)
    userDocument.set(user)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}

fun signIn(
    uid: String,
    onSuccessListener: OnSuccessListener<DocumentSnapshot>,
    onFailureListener: OnFailureListener,
) {
    val userCollection = getCollection(Constants.KEY_COLLECTION_USERS)
    userCollection.document(uid)
        .get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getToken(onSuccessListener: OnSuccessListener<in String>) {
    FirebaseMessaging.getInstance().token.addOnSuccessListener(onSuccessListener)
}

fun updateToken(
    token: String,
    uid: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener,
) {
    val userCollection = getCollection(Constants.KEY_COLLECTION_USERS)
    val doc = userCollection.document(uid)
    doc.update(Constants.KEY_FCM_TOKEN, token)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun signOut(
    uid: String,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener,
) {
    val userCollection = getCollection(Constants.KEY_COLLECTION_USERS)
    val doc = userCollection.document(uid)
    val updates = HashMap<String, Any>()
    updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
    doc.update(updates)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun getAllUsers(
    onSuccessListener: OnSuccessListener<QuerySnapshot>,
    onFailureListener: OnFailureListener,
) {
    val userCollection = getCollection(Constants.KEY_COLLECTION_USERS)
    userCollection.get()
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun sendMessageToFireStore(
    message: Message,
    onSuccessListener: OnSuccessListener<Void>,
    onFailureListener: OnFailureListener,
) {
    val chatCollection = getCollection(Constants.KEY_COLLECTION_CHAT)
    val messageRef = chatCollection.document()
    message.messageId = messageRef.id
    messageRef.set(message)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)

}

fun getAllMessages(
    senderId: String,
    receiverId: String,
    addSnapshotListener: EventListener<QuerySnapshot>,
) {
    val chatCollectionSender = getCollection(Constants.KEY_COLLECTION_CHAT)
        .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
        .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
        .addSnapshotListener(addSnapshotListener)
    val chatCollectionReceiver = getCollection(Constants.KEY_COLLECTION_CHAT)
        .whereEqualTo(Constants.KEY_SENDER_ID, receiverId)
        .whereEqualTo(Constants.KEY_RECEIVER_ID, senderId)
        .addSnapshotListener(addSnapshotListener)
}

fun checkForConversationFromFirebaseRemotely(
    senderId: String,
    receiverId: String,
    addOnCompleteListener: OnCompleteListener<QuerySnapshot>,
) {
    val conversationColl = getCollection(Constants.KEY_CONVERSATIONS)
    conversationColl.whereEqualTo(Constants.KEY_SENDER_ID, senderId)
        .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
        .get()
        .addOnCompleteListener(addOnCompleteListener)
}

fun addConversationFromFirebase(
    conversation: HashMap<String, Any>,
    addOnSuccessListener: OnSuccessListener<DocumentReference>,
) {
    val col = getCollection(Constants.KEY_CONVERSATIONS)
    col.add(conversation)
        .addOnSuccessListener(addOnSuccessListener)
}

fun updateConversationFromFirebase(
    message:String,
    conversationId:String,
){
    val col = getCollection(Constants.KEY_CONVERSATIONS)
    col.document(conversationId).update(
        Constants.KEY_LAST_MESSAGE, message,
        Constants.KEY_TIMESTAMP, Date()
    )
}

