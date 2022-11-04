package ru.radiationx.data.datasource.storage

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import ru.radiationx.data.DataPreferences
import ru.radiationx.data.datasource.holders.ReleaseUpdateHolder
import ru.radiationx.data.entity.app.release.ReleaseItem
import ru.radiationx.data.entity.app.release.ReleaseUpdate
import javax.inject.Inject

/**
 * Created by radiationx on 18.02.18.
 */
class ReleaseUpdateStorage @Inject constructor(
    @DataPreferences private val sharedPreferences: SharedPreferences,
) : ReleaseUpdateHolder {

    companion object {
        private const val LOCAL_HISTORY_KEY = "data.release_update"
    }

    private val localReleasesRelay by lazy {
        MutableStateFlow(loadAll())
    }

    override fun observeEpisodes(): Flow<List<ReleaseUpdate>> = localReleasesRelay

    override suspend fun getReleases(): List<ReleaseUpdate> =
        localReleasesRelay.value.toList()

    override fun getRelease(id: Int): ReleaseUpdate? {
        return localReleasesRelay.value.firstOrNull { it.id == id }
    }

    override fun updRelease(release: ReleaseUpdate) {
        updAllRelease(listOf(release))
    }

    override fun updAllRelease(releases: List<ReleaseUpdate>) {
        localReleasesRelay.update { updates ->
            val mutableUpdates = updates.toMutableList()
            releases.forEach { release ->
                mutableUpdates
                    .firstOrNull { it.id == release.id }
                    ?.let { mutableUpdates.remove(it) }
                mutableUpdates.add(
                    ReleaseUpdate(
                        id = release.id,
                        timestamp = release.timestamp,
                        lastOpenTimestamp = release.lastOpenTimestamp
                    )
                )
            }
            mutableUpdates
        }
        saveAll()
    }

    override fun putRelease(release: ReleaseItem) {
        putAllRelease(listOf(release))
    }

    override fun putAllRelease(releases: List<ReleaseItem>) {
        localReleasesRelay.update { updates ->
            val mutableUpdates = updates.toMutableList()
            releases.forEach { release ->
                mutableUpdates
                    .firstOrNull { it.id == release.id }
                    ?.let { mutableUpdates.remove(it) }
                mutableUpdates.add(
                    ReleaseUpdate(
                        id = release.id,
                        timestamp = release.torrentUpdate,
                        lastOpenTimestamp = Int.MAX_VALUE
                    )
                )
            }
            mutableUpdates
        }
        saveAll()
    }

    private fun saveAll() {
        val jsonEpisodes = JSONArray()
        localReleasesRelay.value.forEach {
            jsonEpisodes.put(JSONObject().apply {
                put("id", it.id)
                put("timestamp", it.timestamp)
                put("lastOpenTimestamp", it.lastOpenTimestamp)
            })
        }
        sharedPreferences
            .edit()
            .putString(LOCAL_HISTORY_KEY, jsonEpisodes.toString())
            .apply()
    }

    private fun loadAll(): List<ReleaseUpdate> {
        val result = mutableListOf<ReleaseUpdate>()
        val savedEpisodes = sharedPreferences.getString(LOCAL_HISTORY_KEY, null)
        savedEpisodes?.let {
            val jsonEpisodes = JSONArray(it)
            (0 until jsonEpisodes.length()).forEach { index ->
                jsonEpisodes.getJSONObject(index).let {
                    result.add(
                        ReleaseUpdate(
                            id = it.getInt("id"),
                            timestamp = it.getInt("timestamp"),
                            lastOpenTimestamp = it.getInt("lastOpenTimestamp")
                        )
                    )
                }
            }
        }
        return result
    }
}