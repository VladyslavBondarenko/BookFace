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
        supportActionBar!!.title = resources.getString(R.string.my_friends_books_button)

        val userId = intent.getStringExtra("userId")
        expandableListView = this.findViewById(R.id.friends_books_listview)

        if (expandableListView != null) {
            adapter = FriendsBooksListAdapter(this, friends_books_list)
            expandableListView.setAdapter(adapter)
            expandableListView.setGroupIndicator(null)
            val db = FirebaseFirestore.getInstance()
            db.collection("users").whereEqualTo("id", userId).get()
                .addOnSuccessListener { documents1 ->
                    for (document1 in documents1) {
                        for (friendId in document1.data["friends"] as ArrayList<*>) {
                            db.collection("books").whereEqualTo("ownerUserId", friendId.toString())
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        var owner = ""
                                        DBUsers.getUserById(document.get("ownerUserId") as String) { user ->
                                            if (user != null) {
                                                owner = user.get("name") as String
                                            }
                                        }
                                        val book = hashMapOf(
                                            "documentId" to document.id,
                                            "title" to document.get("title") as String,
                                            "author" to document.get("author") as String,
                                            "description" to document.get("description") as String,
                                            "isAvailable" to document.get("isAvailable") as Boolean,
                                            "owner" to owner
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
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error getting documents: ", e)
                }
        }

    }

}
