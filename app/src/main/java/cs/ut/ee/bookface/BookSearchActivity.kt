package cs.ut.ee.bookface

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo

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


class BookSearchActivity : MenuActivity() {
    lateinit var adapter: BooksAdapter
    var book_list = ArrayList<Book>()
    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().addInterceptor(interceptor).build())
        .build()!!
    val bookService: BooksService = retrofit.create(BooksService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.book_search_activity)

        supportActionBar!!.title = resources.getString(R.string.add_new_book)

        val userId = intent.getStringExtra("userId") as String
        adapter = BooksAdapter(this, book_list, userId)
        book_listview.adapter = adapter

        search_box.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = v.text.toString()
                if (query.isNotEmpty()) {
                    searchBooks(query)
                }
            }
            true
        }
    }

    fun searchBooks(query: String) {
        progressBar.visibility = View.VISIBLE
        bookService.getBooks(query, 0, 20)
            .enqueue(object : Callback<BookResponse> {
                override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<BookResponse>,
                    response: Response<BookResponse>
                ) {
                    book_list.clear()
                    var returned_books = response.body()?.books ?: listOf()
                    for (b: Book in returned_books) {
                        book_list.add(b)
                    }
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                }
            })
    }

}
