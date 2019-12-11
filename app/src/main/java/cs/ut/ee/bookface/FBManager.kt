package cs.ut.ee.bookface

import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.GraphRequest
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class FBManager {
    companion object {
        val permissions_list = Arrays.asList("public_profile", "email", "user_friends")

        fun isLoggedIn(callback: (Boolean) -> Unit) {
            val accessTokenTracker = object : AccessTokenTracker() {
                override fun onCurrentAccessTokenChanged(
                    oldAccessToken: AccessToken?,
                    newAccessToken: AccessToken?
                ) {
                    callback(newAccessToken != null && !newAccessToken.isExpired)
                }
            }
            accessTokenTracker.startTracking()
        }

        fun getUserId(callback: (String) -> Unit) {
            val request =
                GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { result, response ->
                    try {
                        callback(result.get("id").toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            val parameters = Bundle()
            parameters.putString("fields", "id")
            request.parameters = parameters
            request.executeAsync()
        }

        fun requestUserData(callback: (HashMap<String, String>, MutableList<String>) -> Unit) {
            val request =
                GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { result, response ->
                    try {
                        Log.i("Facebook user data", result.toString())

                        val user = getUserDataFromJson(result)
                        val userFriends = getUserFriendsIDsFromJson(result)

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

        fun getUserFriendsIDsFromJson(result: JSONObject): MutableList<String> {
            val friends = mutableListOf<String>()
            val friendsJSONArray = (result.get("friends") as JSONObject).get("data") as JSONArray
            for (i in 0 until friendsJSONArray.length()) {
                val item = friendsJSONArray.getJSONObject(i)
                friends.add(item.get("id").toString())
            }
            return friends
        }
    }
}