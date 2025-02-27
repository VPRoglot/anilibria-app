package ru.radiationx.anilibria.ui.adapters

import ru.radiationx.anilibria.model.*
import ru.radiationx.anilibria.ui.activities.main.MainActivity
import ru.radiationx.anilibria.ui.adapters.release.detail.EpisodeControlPlace
import ru.radiationx.anilibria.ui.fragments.feed.FeedAppWarning
import ru.radiationx.anilibria.ui.fragments.other.OtherMenuItemState
import ru.radiationx.anilibria.ui.fragments.other.ProfileItemState
import ru.radiationx.anilibria.ui.fragments.release.details.*

open class ListItem(private val idData: Any?) {

    open fun getItemId(): Long {
        return generateIdentifier(this::class.java, idData)
    }

    open fun getItemHash(): Int {
        return hashCode()
    }

    open fun getPayloadBy(oldItem: ListItem): Any? {
        return null
    }

    private fun generateIdentifier(vararg obj: Any?): Long {
        var identifier = 0L
        obj.forEach {
            if (it != null) {
                identifier = identifier * 31 + it.hashCode()
            }
        }
        return identifier
    }
}

/* Other screen*/

data class ProfileListItem(val id: Any, val state: ProfileItemState) : ListItem(id)
data class MenuListItem(val menuItem: OtherMenuItemState) : ListItem(menuItem.title)
data class DividerShadowListItem(val id: Any) : ListItem(id)


/* Common */

data class LoadMoreListItem(val id: Any, val needNotify: Boolean) : ListItem(id)
data class LoadErrorListItem(val id: Any) : ListItem(id)
data class CommentRouteListItem(val id: Any) : ListItem(id)
data class BottomTabListItem(
    val item: MainActivity.Tab,
    val selected: Boolean
) : ListItem(item.screen.screenKey)

data class PlaceholderListItem(
    val icRes: Int,
    val titleRes: Int,
    val descRes: Int
) : ListItem(titleRes)

/* Releases list screen */

data class ReleaseListItem(val item: ReleaseItemState) : ListItem(item.id)


/* Release detail screen */

data class ReleaseEpisodeListItem(
    val state: ReleaseEpisodeItemState,
    val isEven: Boolean
) : ListItem("${state.id}")

data class ReleaseTorrentListItem(val state: ReleaseTorrentItemState) : ListItem(state.id)
data class ReleaseExpandListItem(val title: String) : ListItem(title)
data class ReleaseEpisodeControlItem(
    val state: ReleaseEpisodesControlState,
    val place: EpisodeControlPlace
) : ListItem(place)

data class ReleaseEpisodesHeadListItem(
    val id: Any,
    val tabs: List<EpisodesTabState>,
    val selectedTag: String?
) : ListItem(id)

data class ReleaseDonateListItem(val state: DonationCardItemState) : ListItem(state.tag)
data class ReleaseRemindListItem(val text: String) : ListItem(text)
data class ReleaseBlockedListItem(val state: ReleaseBlockedInfoState) : ListItem(state.title)
data class ReleaseHeadListItem(
    val id: Any,
    val item: ReleaseInfoState,
    val modifiers: ReleaseDetailModifiersState
) : ListItem(id)


/* Search screen */

data class SuggestionLocalListItem(val state: SuggestionLocalItemState) : ListItem(state.id)

data class SuggestionListItem(val state: SuggestionItemState) : ListItem(state.id)

data class YoutubeListItem(val state: YoutubeItemState) : ListItem(state.id)

data class SocialAuthListItem(val state: SocialAuthItemState) : ListItem(state.key)

data class FeedScheduleListItem(val state: ScheduleItemState) : ListItem(state.releaseId)
data class FeedSchedulesListItem(val id: Any, val items: List<ScheduleItemState>) : ListItem(id)
data class FeedSectionListItem(
    val tag: String,
    val title: String,
    val route: String?,
    val routeIconRes: Int?,
    val hasBg: Boolean = false,
    val center: Boolean = false
) : ListItem(tag)

data class FeedListItem(val item: FeedItemState) :
    ListItem("${item.id}")

data class FeedRandomBtnListItem(val id: Any) : ListItem(id)
data class AppInfoCardListItem(val warning: FeedAppWarning) : ListItem(warning.tag)
data class AppWarningCardListItem(val warning: FeedAppWarning) : ListItem(warning.tag)
data class DonationCardListItem(val state: DonationCardItemState) : ListItem(state.tag)
