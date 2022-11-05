package ru.radiationx.data.entity.app.release

import java.io.Serializable

/* Created by radiationx on 31.10.17. */

open class ReleaseItem(
    val id: Int,
    val code: String?,
    val names: List<String>,
    val series: String?,
    val poster: String?,
    val torrentUpdate: Int,
    val status: String?,
    val statusCode: String?,
    val types: List<String>,
    val genres: List<String>,
    val voices: List<String>,
    val seasons: List<String>,
    val days: List<String>,
    val description: String?,
    val announce: String?,
    val favoriteInfo: FavoriteInfo,
    val link: String?
) : Serializable {

    val title: String?
        get() = names.firstOrNull()

    val titleEng: String?
        get() = names.lastOrNull()

    // todo TR-274 remove this shiet. replace this class to data-class
    fun copy(
        id: Int = this.id,
        code: String? = this.code,
        names: List<String> = this.names,
        series: String? = this.series,
        poster: String? = this.poster,
        torrentUpdate: Int = this.torrentUpdate,
        status: String? = this.status,
        statusCode: String? = this.statusCode,
        types: List<String> = this.types,
        genres: List<String> = this.genres,
        voices: List<String> = this.voices,
        seasons: List<String> = this.seasons,
        days: List<String> = this.days,
        description: String? = this.description,
        announce: String? = this.announce,
        favoriteInfo: FavoriteInfo = this.favoriteInfo,
        link: String? = this.link
    ): ReleaseItem = ReleaseItem(
        id = id,
        code = code,
        names = names,
        series = series,
        poster = poster,
        torrentUpdate = torrentUpdate,
        status = status,
        statusCode = statusCode,
        types = types,
        genres = genres,
        voices = voices,
        seasons = seasons,
        days = days,
        description = description,
        announce = announce,
        favoriteInfo = favoriteInfo,
        link = link
    )

    companion object {
        const val STATUS_CODE_PROGRESS = "1"
        const val STATUS_CODE_COMPLETE = "2"
        const val STATUS_CODE_HIDDEN = "3"
        const val STATUS_CODE_NOT_ONGOING = "4"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReleaseItem) return false

        if (id != other.id) return false
        if (code != other.code) return false
        if (names != other.names) return false
        if (series != other.series) return false
        if (poster != other.poster) return false
        if (torrentUpdate != other.torrentUpdate) return false
        if (status != other.status) return false
        if (statusCode != other.statusCode) return false
        if (types != other.types) return false
        if (genres != other.genres) return false
        if (voices != other.voices) return false
        if (seasons != other.seasons) return false
        if (days != other.days) return false
        if (description != other.description) return false
        if (announce != other.announce) return false
        if (favoriteInfo != other.favoriteInfo) return false
        if (link != other.link) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + names.hashCode()
        result = 31 * result + (series?.hashCode() ?: 0)
        result = 31 * result + (poster?.hashCode() ?: 0)
        result = 31 * result + torrentUpdate
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (statusCode?.hashCode() ?: 0)
        result = 31 * result + types.hashCode()
        result = 31 * result + genres.hashCode()
        result = 31 * result + voices.hashCode()
        result = 31 * result + seasons.hashCode()
        result = 31 * result + days.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (announce?.hashCode() ?: 0)
        result = 31 * result + favoriteInfo.hashCode()
        result = 31 * result + (link?.hashCode() ?: 0)
        return result
    }
}
