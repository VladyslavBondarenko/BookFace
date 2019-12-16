package cs.ut.ee.bookface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import kotlinx.android.synthetic.main.user_profile.*
import java.net.URL

class UserProfile : MenuActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        supportActionBar!!.title = resources.getString(R.string.my_profile_button)

        nameView.text = intent.getStringExtra("name")
        val pictureUrl = intent.getStringExtra("picture")

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