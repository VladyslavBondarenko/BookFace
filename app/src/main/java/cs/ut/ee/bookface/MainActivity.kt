package cs.ut.ee.bookface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import com.facebook.*
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        callbackManager = CallbackManager.Factory.create()

        trackLogin()

        val FBLoginCallback = object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.i("loginResult", "Facebook token: " + loginResult.accessToken.token)
                FBManager.requestUserData { user ->
                    DBUsers.getUserById(user["id"] as String) { dbUser ->
                        if (dbUser == null) {
                            DBUsers.addUserToDatabase(user)
                        } else {
                            DBUsers.updateUser(dbUser.get("documentId") as String, user)
                        }
                    }
                }
                actionsAfterLogin()
            }

            override fun onCancel() {
                Log.i("loginResult", "cancel")
            }

            override fun onError(exception: FacebookException) {
                Log.i("loginResult", exception.toString())
            }
        }

        login_button.setReadPermissions(FBManager.permissions_list)
        login_button.registerCallback(callbackManager, FBLoginCallback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun trackLogin() {
        val accessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken?,
                newAccessToken: AccessToken?
            ) {
                if (newAccessToken != null && !newAccessToken.isExpired) {
                    actionsAfterLogin()
                }
            }
        }
        accessTokenTracker.startTracking()
    }

    fun actionsAfterLogin() {
        FBManager.getUserId { userId ->
            DBUsers.getUserById(userId) { user ->
                if (user != null) {
                    openUserProfile(user)
                }
            }
        }
    }

    fun openUserProfile(user: HashMap<String, Serializable>) {
        var user_id = user["id"]
        val intent = Intent(this, UserProfile::class.java)
        intent.putExtra("name", user["name"] as String)
        intent.putExtra("picture", user["picture"] as String)
        intent.putExtra("userId", user_id)
        startActivity(intent)
    }
}
