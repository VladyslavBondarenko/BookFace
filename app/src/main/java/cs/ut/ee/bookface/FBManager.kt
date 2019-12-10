package cs.ut.ee.bookface

import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class FBManager {
    val permissions_list = Arrays.asList("public_profile", "email", "user_friends")

    fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }

    fun requestUserData(callback: (HashMap<String,String>, MutableList<FacebookFriend>) -> Unit) {
        val request =
            GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { result, response ->
                try {
                    Log.i("Facebook user data", result.toString())

                    val user = getUserDataFromJson(result)
                    val userFriends = getUserFriendsFromJson(result)

                    callback(user, userFriends)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        val parameters = Bundle()
        parameters.putString("fields", "name,email,id,picture.type(large),friends")
        request.parameters = parameters
        request.executeAsync()
    }

    fun getUserDataFromJson(result: JSONObject): HashMap<String, String> {
        return hashMapOf(
            "id" to result.get("id") as String,
            "name" to result.get("name") as String,
            "email" to result.get("email") as String,
            "picture" to ((result.get("picture") as JSONObject).get("data") as JSONObject).get("url") as String
        )
    }

    fun getUserFriendsFromJson(result: JSONObject): MutableList<FacebookFriend> {
        val friends = mutableListOf<FacebookFriend>()
        val friendsJSONArray = (result.get("friends") as JSONObject).get("data") as JSONArray
        for (i in 0 until friendsJSONArray.length()) {
            val item = friendsJSONArray.getJSONObject(i)
            friends.add(FacebookFriend(item.get("name").toString(), item.get("id").toString()))
        }
        return friends
    }

    fun userExistsInDatabase(user : HashMap<String,String>, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("id", user.get("id"))
            .get()
            .addOnSuccessListener { documents ->
                var exists = false
                for (document in documents) {
                    exists = true
                    Log.d("Firebase exists", "${document.id} => ${document.data}")
                }
                callback(exists)
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error getting documents: ", e)
            }
    }

    fun addUserToDatabase(user : HashMap<String,String>) {
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

class FacebookFriend {
    var name: String? = null
    var id: String? = null

    constructor(name: String, id: String) {
        this.name = name
        this.id = id
    }
}