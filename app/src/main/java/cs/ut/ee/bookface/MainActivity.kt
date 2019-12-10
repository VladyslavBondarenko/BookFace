package cs.ut.ee.bookface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import java.util.Arrays.asList
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import com.facebook.*
import com.facebook.GraphRequest
import com.facebook.AccessToken
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    lateinit var callbackManager: CallbackManager
    val permissions_list = asList("public_profile","email","user_friends")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)

        callbackManager = CallbackManager.Factory.create()

        save_button.setOnClickListener() {
            val accessToken = AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken != null && !accessToken.isExpired

            if (isLoggedIn) {
                val request =
                    GraphRequest.newMeRequest(accessToken) { result, response ->
                        try {
                            Log.i("Facebook user data", result.toString())

                            val user = getUserDataFromJson(result)
                            val userFriends = getUserFriendsFromJson(result)

                            userExistsInDatabase(user)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                val parameters = Bundle()
                parameters.putString("fields", "name,email,id,picture.type(large),friends")
                request.parameters = parameters
                request.executeAsync()
            } else {
                Log.i("FBLOGIN_JSON_RES", "not logged in")
            }
        }

        login_button.setReadPermissions(permissions_list)
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.i("loginResult", "Facebook token: " + loginResult.accessToken.token)
            }
            override fun onCancel() {
                Log.i("loginResult", "cancel")
            }
            override fun onError(exception: FacebookException) {
                Log.i("loginResult", exception.toString())
            }
        })
    }

    fun getUserDataFromJson(result: JSONObject): HashMap<String, String> {
        return hashMapOf(
            "id" to result.get("id") as String,
            "name" to result.get("name") as String,
            "email" to result.get("email") as String,
            "picture" to ((result.get("picture") as JSONObject).get("data") as JSONObject).get("url") as String
        )
    }

    fun getUserFriendsFromJson(result: JSONObject): MutableList<Friend> {
        val friends = mutableListOf<Friend>()
        val friendsJSONArray = (result.get("friends") as JSONObject).get("data") as JSONArray
        for (i in 0 until friendsJSONArray.length()) {
            val item = friendsJSONArray.getJSONObject(i)
            friends.add(Friend(item.get("name").toString(), item.get("id").toString()))
        }
        return friends
    }

    fun userExistsInDatabase(user : HashMap<String,String>) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("id", user.get("id"))
            .get()
            .addOnSuccessListener { documents ->
                var notExists = true
                for (document in documents) {
                    notExists = false
                    Log.d("Firebase exists", "${document.id} => ${document.data}")
                }
                if (notExists) {
                    addUserToDatabase(user)
                }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}

class Friend {
    var name: String? = null
    var id: String? = null

    constructor(name: String, id: String) {
        this.name = name
        this.id = id
    }
}
