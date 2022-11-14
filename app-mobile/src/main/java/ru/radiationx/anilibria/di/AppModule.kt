package ru.radiationx.anilibria.di

import android.app.Application
import android.content.Context
import ru.radiationx.anilibria.AppBuildConfig
import ru.radiationx.anilibria.AppMigrationExecutor
import ru.radiationx.anilibria.BuildConfig
import ru.radiationx.anilibria.MobileCheckerSources
import ru.radiationx.anilibria.apptheme.AnalyticsThemeProviderImpl
import ru.radiationx.anilibria.apptheme.AppThemeController
import ru.radiationx.anilibria.apptheme.AppThemeControllerImpl
import ru.radiationx.anilibria.navigation.CiceroneHolder
import ru.radiationx.anilibria.presentation.common.IErrorHandler
import ru.radiationx.anilibria.presentation.common.ILinkHandler
import ru.radiationx.anilibria.ui.common.ErrorHandler
import ru.radiationx.anilibria.ui.common.LinkRouter
import ru.radiationx.anilibria.ui.common.Templates
import ru.radiationx.anilibria.ui.fragments.comments.VkCommentsCss
import ru.radiationx.anilibria.utils.DimensionsProvider
import ru.radiationx.anilibria.utils.ShortcutHelper
import ru.radiationx.anilibria.utils.messages.SystemMessenger
import ru.radiationx.data.SharedBuildConfig
import ru.radiationx.data.analytics.AnalyticsErrorReporter
import ru.radiationx.data.analytics.AnalyticsSender
import ru.radiationx.data.analytics.profile.AnalyticsProfile
import ru.radiationx.data.analytics.profile.AnalyticsThemeProvider
import ru.radiationx.data.datasource.remote.common.CheckerReserveSources
import ru.radiationx.data.migration.MigrationExecutor
import ru.radiationx.shared_app.analytics.CodecsProfileAnalytics
import ru.radiationx.shared_app.analytics.errors.AppMetricaErrorReporter
import ru.radiationx.shared_app.analytics.errors.CombinedErrorReporter
import ru.radiationx.shared_app.analytics.errors.LoggingErrorReporter
import ru.radiationx.shared_app.analytics.events.AppMetricaAnalyticsSender
import ru.radiationx.shared_app.analytics.events.CombinedAnalyticsSender
import ru.radiationx.shared_app.analytics.events.LoggingAnalyticsSender
import ru.radiationx.shared_app.analytics.profile.AppMetricaAnalyticsProfile
import ru.radiationx.shared_app.analytics.profile.CombinedAnalyticsProfile
import ru.radiationx.shared_app.analytics.profile.LoggingAnalyticsProfile
import ru.radiationx.shared_app.imageloader.LibriaImageLoader
import ru.radiationx.shared_app.imageloader.impls.CoilLibriaImageLoaderImpl
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import toothpick.config.Module

class AppModule(application: Application) : Module() {


    init {
        bind(Application::class.java).toInstance(application)
        bind(Context::class.java).toInstance(application)

        bind(SharedBuildConfig::class.java).to(AppBuildConfig::class.java).singleton()
        bind(CheckerReserveSources::class.java).to(MobileCheckerSources::class.java).singleton()
        bind(MigrationExecutor::class.java).to(AppMigrationExecutor::class.java).singleton()

        bind(SystemMessenger::class.java).singleton()

        bind(ShortcutHelper::class.java).singleton()

        bind(Templates::class.java).singleton()
        bind(VkCommentsCss::class.java).singleton()
        bind(AppThemeController::class.java).to(AppThemeControllerImpl::class.java).singleton()
        bind(AnalyticsThemeProvider::class.java).to(AnalyticsThemeProviderImpl::class.java)
            .singleton()

        val ciceroneHolder = CiceroneHolder()
        bind(CiceroneHolder::class.java).toInstance(ciceroneHolder)

        val cicerone = ciceroneHolder.getCicerone("root")
        bind(Router::class.java).toInstance(cicerone.router)
        bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)


        bind(DimensionsProvider::class.java).singleton()

        bind(ILinkHandler::class.java).to(LinkRouter::class.java).singleton()
        bind(IErrorHandler::class.java).to(ErrorHandler::class.java).singleton()
        bind(LibriaImageLoader::class.java).to(CoilLibriaImageLoaderImpl::class.java).singleton()


        /* Analytics */
        bind(CodecsProfileAnalytics::class.java).singleton()

        bind(AppMetricaAnalyticsSender::class.java).singleton()
        bind(AppMetricaAnalyticsProfile::class.java).singleton()
        bind(AppMetricaErrorReporter::class.java).singleton()

        bind(LoggingAnalyticsSender::class.java).singleton()
        bind(LoggingAnalyticsProfile::class.java).singleton()
        bind(LoggingErrorReporter::class.java).singleton()

        if (BuildConfig.DEBUG) {
            bind(AnalyticsSender::class.java).to(CombinedAnalyticsSender::class.java).singleton()
            bind(AnalyticsProfile::class.java).to(CombinedAnalyticsProfile::class.java).singleton()
            bind(AnalyticsErrorReporter::class.java).to(CombinedErrorReporter::class.java)
                .singleton()
        } else {
            bind(AnalyticsSender::class.java).to(AppMetricaAnalyticsSender::class.java).singleton()
            bind(AnalyticsProfile::class.java).to(AppMetricaAnalyticsProfile::class.java)
                .singleton()
            bind(AnalyticsErrorReporter::class.java).to(AppMetricaErrorReporter::class.java)
                .singleton()
        }
    }

}