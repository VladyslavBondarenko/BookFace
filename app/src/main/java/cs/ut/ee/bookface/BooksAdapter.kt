package cs.ut.ee.bookface

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import cs.ut.ee.bookface.models.Book
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import cs.ut.ee.bookface.R


class BooksAdapter(var c : Context, var books_list: List<Book>, var user_id : String) : BaseAdapter() {
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
        view.findViewById<TextView>(R.id.book_title).text = book.volumeInfo.title
        val bookAuthor : String
        if (book.volumeInfo.authors == null){
            bookAuthor = "Unknown author"

        }else{
            bookAuthor = book.volumeInfo.authors.joinToString(separator = ",", postfix = "", prefix= "")
        }
        view.findViewById<TextView>(R.id.book_author).text = bookAuthor

        // Add book to the list
        view.findViewById<TextView>(R.id.add_book_btn).setOnClickListener {
            // CHECK IF BOOK ALREADY IN THE LIST
            val db = FirebaseFirestore.getInstance()

            var booksInList : ArrayList<String> = ArrayList()

            db.collection("books").whereEqualTo("ownerUserId", user_id)
                .get()
                .addOnSuccessListener { documents ->

                    for (document in documents) {
                        Log.i("AddNewBook", document.id)
                        booksInList.add(document.get("id") as String)
                    }
                    if (booksInList.contains(book.id)){
                        Toast.makeText(c, "The book is already in your list!", Toast.LENGTH_LONG).show()
                        Log.d("AddNewBook", "Book with if ${book.id} is already in the list")

                    }else{
                        val bookDbObject = hashMapOf(
                            "id" to book.id,
                            "title" to book.volumeInfo.title,
                            "author" to bookAuthor,
                            "description" to book.volumeInfo.description,
                            "ownerUserId" to user_id,
                            "isAvailable" to true
                        )
                        db.collection("books")
                            .add(bookDbObject)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Firebase", "DocumentSnapshot added with ID: ${documentReference.id}")
                                Toast.makeText(c, "Book is added to your list", Toast.LENGTH_LONG).show()

                            }
                            .addOnFailureListener { e ->
                                Log.w("Firebase", "Error adding document", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error getting documents: ", e)
                }


//            val bookDbObject = hashMapOf(
//                "id" to book.id,
//                "title" to book.volumeInfo.title,
//                "author" to bookAuthor,
//                "description" to book.volumeInfo.description,
//                "ownerUserId" to user_id,
//                "isAvailiable" to true
//            )
//            db.collection("books")
//                .add(bookDbObject)
//                .addOnSuccessListener { documentReference ->
//                    Log.d("Firebase", "DocumentSnapshot added with ID: ${documentReference.id}")
//                    Toast.makeText(c, "Book is added to your list", Toast.LENGTH_LONG).show()
//
//                }
//                .addOnFailureListener { e ->
//                    Log.w("Firebase", "Error adding document", e)
//                }
        }
        return view

    }

}