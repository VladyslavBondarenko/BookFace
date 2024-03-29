package cs.ut.ee.bookface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

abstract class MenuActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_new_book -> {
                FBManager.getUserId { userId ->
                    DBUsers.getUserById(userId) { user ->
                        if (user != null) {
                            var user_id = user["id"]
                            val intent = Intent(this, BookSearchActivity::class.java)
                            intent.putExtra("userId", user_id)
                            startActivity(intent)
                        }
                    }
                }
                return true
            }
            R.id.my_books_button -> {
                FBManager.getUserId { userId ->
                    DBUsers.getUserById(userId) { user ->
                        if (user != null) {
                            var user_id = user["id"]
                            val intent = Intent(this, MyBooksActivity::class.java)
                            intent.putExtra("userId", user_id)
                            startActivity(intent)
                        }
                    }
                }
            }
            R.id.my_friends_books_button -> {
                FBManager.getUserId { userId ->
                    DBUsers.getUserById(userId) { user ->
                        if (user != null) {
                            var user_id = user["id"]
                            val intent = Intent(this, FriendsBooksActivity::class.java)
                            intent.putExtra("userId", user_id)
                            startActivity(intent)
                        }
                    }
                }
            }
            R.id.my_profile_button -> {
                FBManager.getUserId { userId ->
                    DBUsers.getUserById(userId) { user ->
                        if (user != null) {
                            var user_id = user["id"]
                            val intent = Intent(this, UserProfile::class.java)
                            intent.putExtra("name", user["name"] as String)
                            intent.putExtra("picture", user["picture"] as String)
                            intent.putExtra("message_template", user["message_template"] as String)
                            intent.putExtra("userId", user_id)
                            startActivity(intent)
                        }
                    }
                }
            }
            R.id.logout_button -> {
                FBManager.logout()
                finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
