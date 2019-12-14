package cs.ut.ee.bookface.models

import com.google.gson.annotations.SerializedName

data class Book(
        @SerializedName("volumeInfo") val volumeInfo: VolumeInfo
)
