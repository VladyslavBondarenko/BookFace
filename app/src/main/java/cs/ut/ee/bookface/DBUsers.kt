package cs.ut.ee.bookface

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class DBUsers {
    companion object {
        fun getUserById(userId: String, callback: (HashMap<String, String>?)->Unit) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").whereEqualTo("id", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("Firebase exists", "${document.id} => ${document.data}")
                        val user = hashMapOf(
                            "id" to document.get("id") as String,
                            "name" to document.get("name") as String,
                            "email" to document.get("email") as String,
                            "picture" to document.get("picture") as String
                        )
                        callback(user)
                    }
                    callback(null)
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error getting documents: ", e)
                }
        }

        fun addUserToDatabase(user: HashMap<String, String>) {
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
    }
}