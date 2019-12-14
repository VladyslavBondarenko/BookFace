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

    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        callbackManager = CallbackManager.Factory.create()

        hideMainFragment()
        trackLogin()

        login_button.setReadPermissions(FBManager.permissions_list)
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.i("loginResult", "Facebook token: " + loginResult.accessToken.token)
                FBManager.requestUserData { user ->
                    DBUsers.getUserById(user["id"] as String) { dbUser ->
                        if (dbUser == null) {
                            DBUsers.addUserToDatabase(user)
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
        })
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
                } else {
                    hideMainFragment()
                }
            }
        }
        accessTokenTracker.startTracking()
    }

    fun actionsAfterLogin() {
        FBManager.getUserId { userId ->
            DBUsers.getUserById(userId) { user ->
                if (user != null) {
                    displayMainFragment(user)
                }
            }
        }
    }

    fun displayMainFragment(user: HashMap<String,Serializable>) {
        val fragmentManager = supportFragmentManager
        val mainFragment = MainFragment()
        val mainLoggedOutFragment = supportFragmentManager.findFragmentByTag("mainLoggedOutFragmentTag")

        val arguments = Bundle()
        arguments.putString("username", user["name"] as String)
        arguments.putString("picture", user["picture"] as String)
        mainFragment.arguments = arguments

        val transaction = fragmentManager.beginTransaction()
        if (mainLoggedOutFragment != null) transaction.remove(mainLoggedOutFragment)
        transaction.add(R.id.fragment_container, mainFragment, "mainFragmentTag")
            .commit()
    }

    fun hideMainFragment() {
        val fragmentManager = supportFragmentManager
        val mainLoggedOutFragment = MainLoggedOutFragment()
        val mainFragment = supportFragmentManager.findFragmentByTag("mainFragmentTag")

        val transaction = fragmentManager.beginTransaction()

        //fragmentManager doesn't want to remove mainFragment
        if (mainFragment != null) transaction.remove(mainFragment)
        transaction.add(R.id.fragment_container, mainLoggedOutFragment, "mainLoggedOutFragmentTag")
                .commit()
    }
}
