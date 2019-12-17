package cs.ut.ee.bookface


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.user_profile.*
import java.net.URL


class UserProfile : MenuActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        supportActionBar!!.title = resources.getString(R.string.my_profile_button)
        val userId = intent.getStringExtra("userId")
        nameView.text = intent.getStringExtra("name")
        val pictureUrl = intent.getStringExtra("picture")
        val db = FirebaseFirestore.getInstance()
        val btn_data_delete = findViewById<Button>(R.id.delete_data_btn)

        val btnSave = findViewById<Button>(R.id.SaveBtn)
        val messageEditText = findViewById<EditText>(R.id.messageEditText)
        listenTouchOutside(userProfileWrapper)


        messageEditText.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                db.collection("users").whereEqualTo("id", userId).get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            document.reference.update(
                                "message_template",
                                messageEditText.text.toString()
                            )
                        }
                        hideSoftKeyboard(this)
                        Toast.makeText(this, getString(R.string.messageUpdate), Toast.LENGTH_LONG)
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, getString(R.string.SWW), Toast.LENGTH_LONG).show()

                    }
            }
            false
        }
        btn_data_delete.setOnClickListener {
            db.collection("books").whereEqualTo("ownerUserId", userId).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                        Toast.makeText(this, getString(R.string.booksDeleted), Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }

        btnSave.setOnClickListener {
            db.collection("users").whereEqualTo("id", userId).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.update(
                            "message_template",
                            messageEditText.text.toString()
                        )
                    }
                    hideSoftKeyboard(this)
                    Toast.makeText(this, getString(R.string.messageUpdate), Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, getString(R.string.SWW), Toast.LENGTH_LONG).show()

                }
        }
        MyAsyncTask().execute(pictureUrl)
    }

    fun hideSoftKeyboard(activity: Activity) {
        if (activity.currentFocus != null) {
            val inputMethodManager =
                activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE
                ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0
            )
        }
    }

    fun listenTouchOutside(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener({ v, event ->
                hideSoftKeyboard(this)
                false
            })
        }
    }

    inner class MyAsyncTask : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg params: String): Bitmap {
            return BitmapFactory.decodeStream(URL(params[0]).openConnection().getInputStream())
        }

        override fun onPostExecute(result: Bitmap?) {
            userPhotoView.setImageBitmap(result)
        }
    }
}