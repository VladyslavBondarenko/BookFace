package cs.ut.ee.bookface

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class DBUsers {
    companion object {
        fun getUserById(userId: String, callback: (HashMap<String, Serializable>?) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").whereEqualTo("id", userId)
                .get()
                .addOnSuccessListener { documents ->
                    var exists = false
                    for (document in documents) {
                        exists = true
                        Log.d("Firebase exists", "${document.id} => ${document.data}")
                        val user = hashMapOf(
                            "id" to document.get("id") as String,
                            "name" to document.get("name") as String,
                            "email" to document.get("email") as String,
                            "picture" to document.get("picture") as String,
                            "friends" to document.get("friends") as ArrayList<String>,
                            "documentId" to document.id
                        )
                        callback(user)
                    }
                    if (!exists) {
                        callback(null)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error getting documents: ", e)
                }
        }

        fun addUserToDatabase(user: HashMap<String, Serializable>) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firebase", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error adding document", e)
                }
        }

        fun updateUser(documentId: String, user: HashMap<String, Serializable>) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .document(documentId).set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firebase", "DocumentSnapshot updated: $documentReference")
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error updating document", e)
                }
        }
    }
}