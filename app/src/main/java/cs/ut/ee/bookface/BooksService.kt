package cs.ut.ee.bookface

import cs.ut.ee.bookface.models.BookResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksService {
    @GET("volumes")
    fun getBooks(
        @Query("q") search: String,
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int
    ): Call<BookResponse>

}