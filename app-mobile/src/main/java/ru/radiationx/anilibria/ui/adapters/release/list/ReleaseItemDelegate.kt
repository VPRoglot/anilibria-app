package ru.radiationx.anilibria.ui.adapters.release.list

import android.text.Html
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.databinding.ItemFeedReleaseBinding
import ru.radiationx.anilibria.model.ReleaseItemState
import ru.radiationx.anilibria.ui.adapters.BaseItemListener
import ru.radiationx.anilibria.ui.adapters.ListItem
import ru.radiationx.anilibria.ui.adapters.ReleaseListItem
import ru.radiationx.anilibria.ui.common.adapters.AppAdapterDelegate
import ru.radiationx.anilibria.ui.common.adapters.OptimizeDelegate
import ru.radiationx.shared.ktx.android.visible
import ru.radiationx.shared_app.imageloader.showImageUrl

/**
 * Created by radiationx on 13.01.18.
 */
class ReleaseItemDelegate(
    private val itemListener: Listener
) : AppAdapterDelegate<ReleaseListItem, ListItem, ReleaseItemDelegate.ViewHolder>(
    R.layout.item_feed_release,
    { it is ReleaseListItem },
    { ViewHolder(it, itemListener) }
), OptimizeDelegate {

    override fun getPoolSize(): Int = 10

    override fun bindData(item: ReleaseListItem, holder: ViewHolder) = holder.bind(item)

    class ViewHolder(
        itemView: View,
        private val itemListener: Listener
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding by viewBinding<ItemFeedReleaseBinding>()

        fun bind(item: ReleaseListItem) {
            val releaseItem = item.item
            binding.itemTitle.text = releaseItem.title

            binding.itemDesc.text = Html.fromHtml(releaseItem.description)
            ViewCompat.setTransitionName(
                binding.itemImage,
                "${item.javaClass.simpleName}_${releaseItem.id}"
            )
            binding.itemNewIndicator.visible(releaseItem.isNew)
            binding.itemImage.showImageUrl(releaseItem.posterUrl)

            binding.root.setOnClickListener {
                itemListener.onItemClick(layoutPosition, binding.itemImage)
                itemListener.onItemClick(releaseItem, layoutPosition)
            }
            binding.root.setOnLongClickListener {
                itemListener.onItemLongClick(releaseItem)
            }
        }
    }

    interface Listener : BaseItemListener<ReleaseItemState> {
        fun onItemClick(position: Int, view: View)
    }
}