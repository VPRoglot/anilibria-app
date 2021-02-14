package ru.radiationx.data.analytics.features

import ru.radiationx.data.analytics.AnalyticsConstants
import ru.radiationx.data.analytics.AnalyticsSender
import toothpick.InjectConstructor

@InjectConstructor
class FeedAnalytics(
    private val sender: AnalyticsSender
) {

    fun open(from: String) {
        sender.send(
            AnalyticsConstants.feed_open,
            "from" to from
        )
    }

    fun loadPage(page: Int) {
        sender.send(
            AnalyticsConstants.feed_load_page,
            "page" to page.toString()
        )
    }

    fun onRandomClick() {
        sender.send(AnalyticsConstants.feed_random_click)
    }
}