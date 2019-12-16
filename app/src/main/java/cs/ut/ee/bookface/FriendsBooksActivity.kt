package cs.ut.ee.bookface

import android.os.Bundle
import android.util.Log
import android.widget.ExpandableListView
import com.google.firebase.firestore.FirebaseFirestore


class FriendsBooksActivity : MenuActivity() {
    lateinit var adapter: FriendsBooksListAdapter
    var friends_books_list = ArrayList<HashMap<String, Any>>()
    lateinit var expandableListView: ExpandableListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_books_activity)
        var userId = intent.getStringExtra("userId")
        expandableListView = this.findViewById(R.id.friends_books_listview)
        if (expandableListView != null) {
            adapter = FriendsBooksListAdapter(this, friends_books_list, userId)
            expandableListView.setAdapter(adapter)
            expandableListView.setGroupIndicator(null)
            //TODO Get friends books not my own
            val db = FirebaseFirestore.getInstance()
            db.collection("books").whereEqualTo("ownerUserId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val book = hashMapOf(
                            "documentId" to document.id,
                            "title" to document.get("title") as String,
                            "author" to document.get("author") as String,
                            "description" to document.get("description") as String,
                            "isAvailable" to document.get("isAvailable") as Boolean
                        )
                        friends_books_list.add(book)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error getting documents: ", e)
                }
        }

    }

}
