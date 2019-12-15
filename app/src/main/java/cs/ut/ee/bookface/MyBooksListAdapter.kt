package cs.ut.ee.bookface

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class MyBooksListAdapter(
    var c: Context,
    var books_list: ArrayList<HashMap<String, Any>>,
    var user_id: String
) : BaseAdapter() {
    override fun getCount(): Int {
        return books_list.count()
    }

    override fun getItemId(position: Int): Long {
        return books_list[position].hashCode()
            .toLong() //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(position: Int): HashMap<String, Any> {
        return books_list[position] //To change body of created functions use File | Settings | File Templates.
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        view = if (convertView == null) {
            val layoutInflater = LayoutInflater.from(parent?.context)
            layoutInflater.inflate(R.layout.my_book_layout, parent, false)
        } else {
            convertView
        }
        val book: HashMap<String, Any> = getItem(position)
        view.findViewById<TextView>(R.id.book_title).text = book["title"] as String
        view.findViewById<TextView>(R.id.book_author).text = book["author"] as String

        var bookAvailable = c.getString(R.string.bookAvailable)
        var bookTaken: String = c.getString(R.string.bookTaken)

        if (book["isAvailable"] as Boolean) {
            view.findViewById<TextView>(R.id.is_available).text = bookAvailable
            view.findViewById<TextView>(R.id.is_available).setTextColor(Color.parseColor("#008000"))
        } else {
            view.findViewById<TextView>(R.id.is_available).text = bookTaken
            view.findViewById<TextView>(R.id.is_available).setTextColor(Color.parseColor("#FF8C00"))
        }

        // Add book to the list
        view.findViewById<TextView>(R.id.is_available).setOnClickListener {
            // CHECK IF BOOK ALREADY IN THE LIST
            var newState: Boolean =
                view.findViewById<TextView>(R.id.is_available).text != "Available"
            val db = FirebaseFirestore.getInstance()

            db.collection("books").document(book["documentId"] as String)
                .update("isAvailable", newState)
                .addOnSuccessListener {
                    if (newState) {
                        view.findViewById<TextView>(R.id.is_available).text = bookAvailable
                        view.findViewById<TextView>(R.id.is_available)
                            .setTextColor(Color.parseColor("#008000"))
                    } else {
                        view.findViewById<TextView>(R.id.is_available).text = bookTaken
                        view.findViewById<TextView>(R.id.is_available)
                            .setTextColor(Color.parseColor("#FF8C00"))
                    }
                }
        }
        return view

    }

}