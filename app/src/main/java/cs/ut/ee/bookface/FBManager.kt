package cs.ut.ee.bookface

import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FBManager {
    companion object {
        val permissions_list = listOf("public_profile", "email", "user_friends")!!

        fun getUserId(callback: (String) -> Unit) {
            val request =
                GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { result, _ ->
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

        fun requestUserData(callback: (HashMap<String, Serializable>) -> Unit) {
            val request =
                GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { result, _ ->
                    try {
                        Log.i("Facebook user data", result.toString())
                        val user = getUserDataFromJson(result)
                        callback(user)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            val parameters = Bundle()
            parameters.putString("fields", "name,email,id,picture.type(large),friends")
            request.parameters = parameters
            request.executeAsync()
        }

        fun getUserDataFromJson(result: JSONObject): HashMap<String, Serializable> {
            return hashMapOf(
                "id" to result.get("id") as String,
                "name" to result.get("name") as String,
                "email" to result.get("email") as String,
                "picture" to ((result.get("picture") as JSONObject).get("data") as JSONObject).get("url") as String,
                "friends" to getUserFriendsIDsFromJson(result)
            )
        }

        fun getUserFriendsIDsFromJson(result: JSONObject): ArrayList<String> {
            val friends = mutableListOf<String>()
            val friendsJSONArray = (result.get("friends") as JSONObject).get("data") as JSONArray
            for (i in 0 until friendsJSONArray.length()) {
                val item = friendsJSONArray.getJSONObject(i)
                friends.add(item.get("id").toString())
            }
            return friends as ArrayList<String>
        }

        fun logout() {
            LoginManager.getInstance().logOut();
        }
    }
}