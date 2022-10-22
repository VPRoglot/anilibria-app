package ru.radiationx.anilibria.ui.fragments.donation.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_donation_button.*
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.extension.getColorFromAttr
import ru.radiationx.anilibria.extension.getCompatColor
import ru.radiationx.anilibria.extension.getCompatDrawable
import ru.radiationx.anilibria.model.asDataColorRes
import ru.radiationx.anilibria.model.asDataIconRes
import ru.radiationx.anilibria.ui.adapters.ListItem
import ru.radiationx.anilibria.ui.common.adapters.AppAdapterDelegate
import ru.radiationx.data.entity.domain.donation.DonationContentButton

class DonationButtonDelegate(
    private val clickListener: (DonationContentButton) -> Unit
) : AppAdapterDelegate<DonationButtonListItem, ListItem, DonationButtonDelegate.ViewHolder>(
    R.layout.item_donation_button,
    { it is DonationButtonListItem },
    { ViewHolder(it, clickListener) }
) {

    override fun bindData(item: DonationButtonListItem, holder: ViewHolder) =
        holder.bind(item.data)

    class ViewHolder(
        override val containerView: View,
        private val clickListener: (DonationContentButton) -> Unit
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(data: DonationContentButton) {
            btAction.text = data.text
            btAction.setOnClickListener { clickListener.invoke(data) }

            val textColor = data.brand?.asDataColorRes()
                ?.let { btAction.getCompatColor(it) }
                ?: btAction.context.getColorFromAttr(R.attr.colorAccent)

            btAction.icon = data.icon?.asDataIconRes()?.let { btAction.getCompatDrawable(it) }
            btAction.setTextColor(textColor)
            btAction.iconTint = ColorStateList.valueOf(textColor)
        }
    }
}