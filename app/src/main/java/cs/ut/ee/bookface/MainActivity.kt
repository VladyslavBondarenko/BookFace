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
import com.facebook.login.LoginManager
import com.facebook.GraphRequest



class MainActivity : AppCompatActivity() {

    lateinit var callbackManager: CallbackManager
    val permissions_list = asList("public_profile","email","user_friends")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)

        callbackManager = CallbackManager.Factory.create()

        login_button.setReadPermissions(permissions_list)

        custom_button.setOnClickListener() {
            LoginManager.getInstance().logInWithReadPermissions(this, permissions_list)
        }
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.i("loginResult", "Facebook token: " + loginResult.accessToken.token)

                val request = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                    try {
                        Log.i("FBLOGIN_JSON_RES", `object`.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "name,email,id,picture.type(large),friends")
                request.parameters = parameters
                request.executeAsync()
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
}
