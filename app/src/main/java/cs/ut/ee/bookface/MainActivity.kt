package cs.ut.ee.bookface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import com.facebook.*

class MainActivity : AppCompatActivity() {

    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)

        callbackManager = CallbackManager.Factory.create()

        FBManager.isLoggedIn { isLoggedIn ->
            if (isLoggedIn) {
                FBManager.getUserId { userId ->
                    DBUsers.getUserById(userId) { user ->
                        if (user != null) {
                            val username = user.get("name") as String
                            displayMainFragment(username)
                        }
                    }
                }
            } else {
                hideMainFragment()
            }
        }

        login_button.setReadPermissions(FBManager.permissions_list)
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.i("loginResult", "Facebook token: " + loginResult.accessToken.token)
                FBManager.requestUserData { user, userFriends ->
                    DBUsers.getUserById(user.get("id") as String) { dbUser ->
                        if (dbUser.isNullOrEmpty()) {
                            DBUsers.addUserToDatabase(user)
                        }
                    }
                }
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

    fun displayMainFragment(username: String) {
        val fragmentManager = supportFragmentManager
        val mainFragment = MainFragment()
//        val anotherFragment = supportFragmentManager.findFragmentByTag("anotherFragmentTag")

        val arguments = Bundle()
        arguments.putString("username", username)
        mainFragment.arguments = arguments

        val transaction = fragmentManager.beginTransaction()
//        if (anotherFragment != null) transaction.remove(anotherFragment)
        transaction.add(R.id.fragment_container, mainFragment, "mainFragmentTag")
            .commit()
    }

    fun hideMainFragment() {
        val fragmentManager = supportFragmentManager
        val mainFragment = supportFragmentManager.findFragmentByTag("mainFragmentTag")

        val transaction = fragmentManager.beginTransaction()
        if (mainFragment != null)
            transaction.remove(mainFragment)
            .commit()
    }
}
