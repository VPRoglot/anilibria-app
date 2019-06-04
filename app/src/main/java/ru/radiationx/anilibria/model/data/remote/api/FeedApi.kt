package ru.radiationx.anilibria.model.data.remote.api

import android.util.Log
import io.reactivex.Single
import org.json.JSONArray
import ru.radiationx.anilibria.di.qualifier.ApiClient
import ru.radiationx.anilibria.entity.app.feed.FeedItem
import ru.radiationx.anilibria.model.data.remote.Api
import ru.radiationx.anilibria.model.data.remote.ApiResponse
import ru.radiationx.anilibria.model.data.remote.IClient
import ru.radiationx.anilibria.model.data.remote.address.ApiConfig
import ru.radiationx.anilibria.model.data.remote.parsers.FeedParser
import ru.radiationx.anilibria.model.data.remote.parsers.ReleaseParser
import ru.radiationx.anilibria.model.data.remote.parsers.YoutubeParser
import javax.inject.Inject

class FeedApi @Inject constructor(
        @ApiClient private val client: IClient,
        private val releaseParser: ReleaseParser,
        private val youtubeParser: YoutubeParser,
        private val feedParser: FeedParser,
        private val apiConfig: ApiConfig
) {

    fun getFeed(page: Int): Single<List<FeedItem>> {
        val args: MutableMap<String, String> = mutableMapOf(
                "query" to "feed",
                "page" to page.toString(),
                "filter" to "id,torrents,playlist,favorite,moon,blockedInfo",
                "rm" to "true"
        )
        return client.post(apiConfig.apiUrl, args)
                .compose(ApiResponse.fetchResult<JSONArray>())
                .map { feedParser.feed(it, releaseParser, youtubeParser) }
                .doOnError {
                    Log.e("bobobo", "catch error $it")
                }
    }

}