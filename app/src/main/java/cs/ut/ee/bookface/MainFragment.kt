package cs.ut.ee.bookface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.net.URL

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val bundle =  this.getArguments()
        view.nameView.text = bundle?.getString("username")
        val pictureUrl = bundle?.getString("picture") as String
        MyAsyncTask().execute(pictureUrl)
        return view
    }

    inner class MyAsyncTask : AsyncTask<String, Void, Bitmap>(){
        override fun doInBackground(vararg params: String): Bitmap {
            return BitmapFactory.decodeStream(URL(params[0]).openConnection().getInputStream())
        }
        override fun onPostExecute(result: Bitmap?) {
            view?.userPhotoView?.setImageBitmap(result)
        }
    }
}