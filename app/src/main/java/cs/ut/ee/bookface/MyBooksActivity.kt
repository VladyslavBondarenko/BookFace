package cs.ut.ee.bookface

import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.my_books_activity.*


class MyBooksActivity : MenuActivity() {
    lateinit var adapter: MyBooksListAdapter
    var my_books_list = ArrayList<HashMap<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_books_activity)

        supportActionBar!!.title = resources.getString(R.string.my_books_button)

        var userId = intent.getStringExtra("userId")

        adapter = MyBooksListAdapter(this, my_books_list, userId)
        book_listview.adapter = adapter


        val db = FirebaseFirestore.getInstance()
        db.collection("books").whereEqualTo("ownerUserId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val book = hashMapOf(
                        "documentId" to document.id,
                        "title" to document.get("title") as String,
                        "author" to document.get("author") as String,
                        "isAvailable" to document.get("isAvailable") as Boolean
                    )
                    my_books_list.add(book)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error getting documents: ", e)
            }
    }

}
