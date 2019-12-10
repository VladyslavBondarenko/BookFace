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
    lateinit var fbManager: FBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)

        callbackManager = CallbackManager.Factory.create()
        fbManager = FBManager()

        fbManager.isLoggedIn { isLoggedIn ->
            if (isLoggedIn) {
                displayMainFragment()
            } else {
                hideMainFragment()
            }
        }

        login_button.setReadPermissions(fbManager.permissions_list)
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.i("loginResult", "Facebook token: " + loginResult.accessToken.token)
                fbManager.requestUserData { user, userFriends ->
                    fbManager.userExistsInDatabase(user) { exists ->
                        if (!exists) {
                            fbManager.addUserToDatabase(user)
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

    fun displayMainFragment() {
        val fragmentManager = supportFragmentManager
        val mainFragment = MainFragment()
//        val anotherFragment = supportFragmentManager.findFragmentByTag("anotherFragmentTag")

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
