package ru.radiationx.data.entity.response.release

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class FavoriteInfoResponse(
    @Json(name = "rating") val rating: Int,
    @Json(name = "added") val isAdded: Boolean
) : Serializable