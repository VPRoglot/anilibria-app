package ru.radiationx.anilibria.ui.fragments.release.details

/* Created by radiationx on 18.11.17. */

import android.util.Log
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.model.DonationCardItemState
import ru.radiationx.anilibria.ui.adapters.*
import ru.radiationx.anilibria.ui.adapters.feed.FeedSectionDelegate
import ru.radiationx.anilibria.ui.adapters.global.CommentRouteDelegate
import ru.radiationx.anilibria.ui.adapters.other.DividerShadowItemDelegate
import ru.radiationx.anilibria.ui.adapters.release.detail.*
import ru.radiationx.anilibria.ui.common.adapters.ListItemAdapter

class ReleaseInfoAdapter(
    private val headListener: ReleaseHeadDelegate.Listener,
    private val episodeListener: ReleaseEpisodeDelegate.Listener,
    private val episodeControlListener: ReleaseEpisodeControlDelegate.Listener,
    private val donationListener: (DonationCardItemState) -> Unit,
    private val donationCloseListener: (DonationCardItemState) -> Unit,
    private val torrentClickListener: (ReleaseTorrentItemState) -> Unit,
    private val commentsClickListener: () -> Unit,
    private val episodesTabListener: (String) -> Unit,
    private val remindCloseListener: () -> Unit,
    private val torrentInfoListener: () -> Unit
) : ListItemAdapter() {

    companion object {
        private const val TORRENT_TAG = "torrents"
    }

    init {
        addDelegate(ReleaseHeadDelegate(headListener))
        addDelegate(FeedSectionDelegate {
            if (it.tag == TORRENT_TAG) {
                torrentInfoListener.invoke()
            }
        })
        addDelegate(ReleaseExpandDelegate {})
        addDelegate(ReleaseEpisodeDelegate(episodeListener))
        addDelegate(ReleaseTorrentDelegate(torrentClickListener))
        addDelegate(ReleaseEpisodeControlDelegate(episodeControlListener))
        addDelegate(ReleaseEpisodesHeadDelegate(episodesTabListener))
        addDelegate(ReleaseDonateDelegate(donationListener, donationCloseListener))
        addDelegate(ReleaseRemindDelegate(remindCloseListener))
        addDelegate(ReleaseBlockedDelegate())
        addDelegate(CommentRouteDelegate(commentsClickListener))
        addDelegate(DividerShadowItemDelegate())
    }

    fun bindState(releaseState: ReleaseDetailState, screenState: ReleaseDetailScreenState) {
        val modifications = screenState.modifiers
        val newItems = mutableListOf<ListItem>()

        if (releaseState.episodesControl != null && releaseState.episodesControl.hasEpisodes) {
            newItems.add(
                ReleaseEpisodeControlItem(
                    releaseState.episodesControl.copy(hasWeb = false),
                    EpisodeControlPlace.TOP
                )
            )
        } else if (modifications.detailLoading) {
            newItems.add(
                ReleaseEpisodeControlItem(
                    ReleaseEpisodesControlState(
                        hasWeb = false,
                        hasEpisodes = true,
                        hasViewed = false,
                        continueTitle = "Загрузка..."
                    ),
                    EpisodeControlPlace.TOP
                )
            )
        }

        newItems.add(
            ReleaseHeadListItem(
                "head",
                releaseState.info,
                modifications
            )
        )
        newItems.add(DividerShadowListItem("head"))

        if (releaseState.blockedInfo != null) {
            newItems.add(ReleaseBlockedListItem(releaseState.blockedInfo))
            newItems.add(DividerShadowListItem("blocked"))
        }

        if (releaseState.blockedInfo == null && screenState.donationCardState != null) {
            newItems.add(ReleaseDonateListItem(screenState.donationCardState))
            newItems.add(DividerShadowListItem("donate"))
        }

        if (releaseState.torrents.isNotEmpty()) {
            newItems.add(
                FeedSectionListItem(
                    TORRENT_TAG,
                    "Torrent раздачи",
                    null,
                    R.drawable.ic_help_circle_outline,
                    hasBg = true
                )
            )
            newItems.addAll(releaseState.torrents.map { ReleaseTorrentListItem(it) })
            newItems.add(DividerShadowListItem("torrents"))
        }

        if (releaseState.blockedInfo == null && screenState.remindText != null) {
            newItems.add(ReleaseRemindListItem(screenState.remindText))
            newItems.add(DividerShadowListItem("remind"))
        }

        if (releaseState.episodesControl != null) {
            newItems.add(
                ReleaseEpisodeControlItem(
                    releaseState.episodesControl,
                    EpisodeControlPlace.BOTTOM
                )
            )
        }

        if (releaseState.episodesTabs.isNotEmpty()) {
            val selectedEpisodesTabTag =
                modifications.selectedEpisodesTabTag ?: releaseState.episodesTabs.firstOrNull()?.tag

            if (releaseState.episodesTabs.size > 1) {
                newItems.add(
                    ReleaseEpisodesHeadListItem(
                        "tabs",
                        releaseState.episodesTabs,
                        selectedEpisodesTabTag
                    )
                )
            }

            val episodes = releaseState.episodesTabs
                .firstOrNull { it.tag == selectedEpisodesTabTag }
                ?.episodes.orEmpty()

            val episodeListItems = episodes.mapIndexed { index, episode ->
                ReleaseEpisodeListItem(episode, index % 2 == 0)
            }
            if (modifications.episodesReversed) {
                newItems.addAll(episodeListItems.asReversed())
            } else {
                newItems.addAll(episodeListItems)
            }
        }

        newItems.add(DividerShadowListItem("episodes"))
        newItems.add(CommentRouteListItem("comments"))
        newItems.add(DividerShadowListItem("comments"))

        items = newItems
    }
}
