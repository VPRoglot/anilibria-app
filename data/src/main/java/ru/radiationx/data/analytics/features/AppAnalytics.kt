package ru.radiationx.data.analytics.features

import ru.radiationx.data.analytics.AnalyticsConstants
import ru.radiationx.data.analytics.AnalyticsSender
import ru.radiationx.data.analytics.features.extensions.toPreciseTimeParam
import toothpick.InjectConstructor

@InjectConstructor
class AppAnalytics(
    private val sender: AnalyticsSender
) {

    fun timeToCreate(timeInMillis: Long) {
        sender.send(
            AnalyticsConstants.app_time_to_create,
            timeInMillis.toPreciseTimeParam()
        )
    }

    fun timeToInit(timeInMillis: Long) {
        sender.send(
            AnalyticsConstants.app_time_to_init,
            timeInMillis.toPreciseTimeParam()
        )
    }

    fun timeToActivity(timeInMillis: Long) {
        sender.send(
            AnalyticsConstants.app_time_to_activity,
            timeInMillis.toPreciseTimeParam()
        )
    }
}