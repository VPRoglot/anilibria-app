package ru.radiationx.anilibria.ui.adapters.release.detail

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_release_episode_control.*
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.entity.app.release.ReleaseFull
import ru.radiationx.anilibria.extension.visible
import ru.radiationx.anilibria.ui.adapters.ListItem
import ru.radiationx.anilibria.ui.adapters.ReleaseEpisodeControlItem
import ru.radiationx.anilibria.ui.common.adapters.AppAdapterDelegate

/**
 * Created by radiationx on 13.01.18.
 */
class ReleaseEpisodeControlDelegate(
        private val itemListener: Listener
) : AppAdapterDelegate<ReleaseEpisodeControlItem, ListItem, ReleaseEpisodeControlDelegate.ViewHolder>(
        R.layout.item_release_episode_control,
        { it is ReleaseEpisodeControlItem },
        { ViewHolder(it, itemListener) }
) {

    override fun bindData(item: ReleaseEpisodeControlItem, holder: ViewHolder) =
            holder.bind(item.item)

    class ViewHolder(
            override val containerView: View,
            private val itemListener: Listener
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            full_button_watch_all.setOnClickListener {
                itemListener.onClickWatchAll()
            }
            full_button_continue.setOnClickListener {
                itemListener.onClickContinue()
            }
        }

        fun bind(item: ReleaseFull) {
            val hasEpisodes = !item.episodes.isEmpty()
            val hasViewed = item.episodes.firstOrNull { it.isViewed } != null
            full_button_watch_all.isEnabled = hasEpisodes
            full_button_continue.visible(hasViewed)
        }
    }

    interface Listener {
        fun onClickWatchAll()

        fun onClickContinue()
    }
}