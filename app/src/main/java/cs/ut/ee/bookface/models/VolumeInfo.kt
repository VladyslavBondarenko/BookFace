package cs.ut.ee.bookface.models

import com.google.gson.annotations.SerializedName

data class VolumeInfo(
        @SerializedName("title") val title: String,
        @SerializedName("authors") val authors: List<String>,
        @SerializedName("description") val description: String
)