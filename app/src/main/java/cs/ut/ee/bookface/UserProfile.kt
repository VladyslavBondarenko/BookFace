package cs.ut.ee.bookface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
        btn_data_delete.setOnClickListener {
            db.collection("books").whereEqualTo("ownerUserId", userId).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
        }
        MyAsyncTask().execute(pictureUrl)
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