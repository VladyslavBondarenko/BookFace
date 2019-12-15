package cs.ut.ee.bookface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo

import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import cs.ut.ee.bookface.models.BookResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import cs.ut.ee.bookface.models.Book
import kotlinx.android.synthetic.main.book_search_activity.*
import java.io.Serializable


class MyBooksActivity : MenuActivity() {
    lateinit var adapter : MyBooksListAdapter
    var my_books_list = ArrayList<HashMap<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_books_activity)
        var userId = intent.getStringExtra("userId")

        adapter = MyBooksListAdapter(this, my_books_list, userId)
        book_listview.setAdapter(adapter)


        val db = FirebaseFirestore.getInstance()
        db.collection("books").whereEqualTo("ownerUserId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val book = hashMapOf(
                        "documentId" to document.id,
                        "title" to document.get("title") as String,
                        "author" to document.get("author") as String,
                        "isAvailable" to document.get("isAvailable") as Boolean)
                    my_books_list.add(book)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error getting documents: ", e)
            }
    }

}
