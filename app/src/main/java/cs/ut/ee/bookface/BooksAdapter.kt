package cs.ut.ee.bookface

import android.util.Log
import android.view.LayoutInflater
import cs.ut.ee.bookface.models.Book
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import cs.ut.ee.bookface.R


class BooksAdapter(var books_list: List<Book>, var user_id : String) : BaseAdapter() {
    override fun getCount(): Int {
        return books_list.count()
    }

    override fun getItemId(position: Int): Long {
        return books_list[position].hashCode().toLong() //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(position: Int): Book {
        return books_list[position] //To change body of created functions use File | Settings | File Templates.
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(parent?.context)
            view = layoutInflater.inflate(R.layout.found_book_layout, parent, false)
        } else {
            view = convertView
        }
        val book : Book = getItem(position)
        Log.i("book info", book.volumeInfo.title)
        view.findViewById<TextView>(R.id.book_title).text = book.volumeInfo.title
        val bookAuthor : String
        if (book.volumeInfo.authors == null){
            bookAuthor = "Unknown author"

        }else{
            bookAuthor = book.volumeInfo.authors.joinToString(separator = ",", postfix = "", prefix= "")
        }
        view.findViewById<TextView>(R.id.book_author).text = bookAuthor
        view.findViewById<TextView>(R.id.add_book_btn).setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val bookDbObject = hashMapOf(
                    "title" to book.volumeInfo.title,
                    "author" to bookAuthor,
                    "author" to bookAuthor,
                    "description" to book.volumeInfo.description,
                    "ownerUserId" to user_id
            )
            db.collection("books")
                    .add(bookDbObject)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Firebase", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firebase", "Error adding document", e)
                    }
        }
        return view

    }

}