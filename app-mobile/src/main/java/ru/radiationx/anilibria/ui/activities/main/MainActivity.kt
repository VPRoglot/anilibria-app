package ru.radiationx.anilibria.ui.activities.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_container.*
import kotlinx.android.synthetic.main.activity_main.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.radiationx.anilibria.App
import ru.radiationx.anilibria.BuildConfig
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.apptheme.AppThemeController
import ru.radiationx.anilibria.di.LocaleModule
import ru.radiationx.anilibria.extension.disableItemChangeAnimation
import ru.radiationx.anilibria.extension.getCompatColor
import ru.radiationx.anilibria.extension.getMainStyleRes
import ru.radiationx.anilibria.navigation.BaseAppScreen
import ru.radiationx.anilibria.navigation.Screens
import ru.radiationx.anilibria.presentation.checker.CheckerPresenter
import ru.radiationx.anilibria.presentation.checker.CheckerView
import ru.radiationx.anilibria.presentation.main.MainPresenter
import ru.radiationx.anilibria.presentation.main.MainView
import ru.radiationx.anilibria.ui.activities.BaseActivity
import ru.radiationx.anilibria.ui.common.BackButtonListener
import ru.radiationx.anilibria.ui.common.IntentHandler
import ru.radiationx.anilibria.ui.fragments.configuring.ConfiguringFragment
import ru.radiationx.anilibria.utils.DimensionHelper
import ru.radiationx.anilibria.utils.DimensionsProvider
import ru.radiationx.anilibria.utils.messages.SystemMessenger
import ru.radiationx.data.analytics.AnalyticsConstants
import ru.radiationx.data.datasource.remote.Api
import ru.radiationx.data.entity.app.updater.UpdateData
import ru.radiationx.data.entity.common.AuthState
import ru.radiationx.data.system.LocaleHolder
import ru.radiationx.shared.ktx.android.gone
import ru.radiationx.shared.ktx.android.visible
import ru.radiationx.shared_app.di.DI
import ru.radiationx.shared_app.di.getDependency
import ru.radiationx.shared_app.di.injectDependencies
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Back
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Replace
import javax.inject.Inject
import kotlin.math.max


class MainActivity : BaseActivity(), MainView, CheckerView {

    companion object {
        private const val TABS_STACK = "TABS_STACK"

        fun getIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    @Inject
    lateinit var screenMessenger: SystemMessenger

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigationHolder: NavigatorHolder

    @Inject
    lateinit var dimensionsProvider: DimensionsProvider

    @Inject
    lateinit var appThemeHolder: AppThemeController

    private val tabsAdapter by lazy { BottomTabsAdapter(tabsListener) }

    private val allTabs = arrayOf(
        Tab(R.string.fragment_title_releases, R.drawable.ic_newspaper, Screens.MainFeed()),
        Tab(R.string.fragment_title_favorites, R.drawable.ic_star, Screens.Favorites()),
        Tab(R.string.fragment_title_search, R.drawable.ic_toolbar_search, Screens.ReleasesSearch()),
        Tab(R.string.fragment_title_youtube, R.drawable.ic_youtube, Screens.MainYouTube()),
        Tab(R.string.fragment_title_other, R.drawable.ic_other, Screens.MainOther())
    )
    private val tabs = mutableListOf<Tab>()

    private val tabsStack = mutableListOf<String>()

    private var dimensionHelper: DimensionHelper? = null

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun provideMainPresenter(): MainPresenter = getDependency(MainPresenter::class.java)


    @InjectPresenter
    lateinit var checkerPresenter: CheckerPresenter

    @ProvidePresenter
    fun provideCheckerPresenter(): CheckerPresenter = getDependency(CheckerPresenter::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            resources.configuration.locale
        }
        injectDependencies(LocaleModule(locale), DI.DEFAULT_SCOPE)
        setTheme(appThemeHolder.getTheme().getMainStyleRes())
        super.onCreate(savedInstanceState)

        if (Api.STORE_APP_IDS.contains(BuildConfig.APPLICATION_ID) && !LocaleHolder.checkAvail(
                locale.country
            )
        ) {
            startActivity(Screens.BlockedCountry().getActivityIntent(this))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        dimensionHelper = DimensionHelper(
            measure_view,
            measure_root_content,
            object : DimensionHelper.DimensionsListener {
                override fun onDimensionsChange(dimensions: DimensionHelper.Dimensions) {
                    root_container.post {
                        root_container.setPadding(
                            root_container.paddingLeft,
                            root_container.paddingTop,
                            root_container.paddingRight,
                            max(dimensions.keyboardHeight - tabsRecycler.height, 0)
                        )
                    }
                    dimensionsProvider.update(dimensions)
                }
            })

        tabsRecycler.apply {
            layoutManager = GridLayoutManager(this.context, allTabs.size)
            adapter = tabsAdapter
            disableItemChangeAnimation()
        }

        updateTabs()
        initContainers()

        savedInstanceState?.let {
            it.getStringArrayList(TABS_STACK)?.let {
                if (it.isNotEmpty()) {
                    tabsStack.addAll(it)
                    presenter.defaultScreen = it.last()
                }
            }
        }
        checkerPresenter.forceLoad = true
    }


    override fun setRefreshing(refreshing: Boolean) {}

    override fun showUpdateData(update: UpdateData) {
        val currentVersionCode = BuildConfig.VERSION_CODE

        if (update.code > currentVersionCode) {
            val context: Context = App.instance
            val channelId = "anilibria_channel_updates"
            val channelName = "Обновления"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                val manager = context.getSystemService(NotificationManager::class.java)
                manager?.createNotificationChannel(channel)
            }

            val mBuilder = NotificationCompat.Builder(context, channelId)

            val mNotificationManager = NotificationManagerCompat.from(context)

            mBuilder.setSmallIcon(R.drawable.ic_notify)
            mBuilder.color = context.getCompatColor(R.color.alib_red)

            mBuilder.setContentTitle("Обновление AniLibria")
            mBuilder.setContentText("Новая версия: ${update.name}")
            mBuilder.setChannelId(channelId)


            val notifyIntent =
                Screens.AppUpdateScreen(false, AnalyticsConstants.notification_local_update)
                    .getActivityIntent(context)
            val notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0)
            mBuilder.setContentIntent(notifyPendingIntent)

            mBuilder.setAutoCancel(true)

            mBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
            mBuilder.setCategory(NotificationCompat.CATEGORY_EVENT)

            var defaults = 0
            defaults = defaults or NotificationCompat.DEFAULT_SOUND
            defaults = defaults or NotificationCompat.DEFAULT_VIBRATE
            mBuilder.setDefaults(defaults)

            mNotificationManager.notify(update.code, mBuilder.build())
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigationHolder.setNavigator(navigatorNew)
    }

    override fun onMainLogicCompleted() {
        handleIntent(intent)
        checkerPresenter.checkUpdate()
    }

    override fun showConfiguring() {
        configuring_container.visible()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.configuring_container, ConfiguringFragment())
            .commitNow()
    }

    override fun hideConfiguring() {
        configuring_container.gone()
        supportFragmentManager.findFragmentById(R.id.configuring_container)?.also {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commitNow()
        }
    }

    override fun onPause() {
        navigationHolder.removeNavigator()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.putStringArrayList(TABS_STACK, ArrayList(tabsStack))
    }

    override fun onDestroy() {
        super.onDestroy()
        dimensionHelper?.destroy()
        ImageLoader.getInstance().clearMemoryCache()
        ImageLoader.getInstance().stop()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(tabsStack.lastOrNull())
        val check = fragment != null
                && fragment is BackButtonListener
                && (fragment as BackButtonListener).onBackPressed()
        if (check) {
            return
        } else {
            presenter.onBackPressed()
        }
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.also { intentData ->
            val url = intentData.toString()
            var handled = findTabIntentHandler(url, tabsStack.asReversed())
            if (!handled) {
                handled = findTabIntentHandler(url, tabs.map { it.screen.screenKey })
            }
        }
        intent?.data = null
    }

    private fun findTabIntentHandler(url: String, tabs: List<String>): Boolean {
        val fm = supportFragmentManager
        tabs.forEach {
            fm.findFragmentByTag(it)?.let {
                if (it is IntentHandler && it.handle(url)) {
                    return true
                }
            }
        }
        return false
    }

    private fun initContainers() {
        val fm = supportFragmentManager
        val ta = fm.beginTransaction()
        allTabs.forEach { tab ->
            var fragment: Fragment? = fm.findFragmentByTag(tab.screen.screenKey)
            if (fragment == null) {
                fragment = Screens.TabScreen(tab.screen).fragment
                ta.add(R.id.root_container, fragment, tab.screen.screenKey)
                if (tabsStack.contains(tab.screen.screenKey)) {
                    ta.attach(fragment)
                } else {
                    ta.detach(fragment)
                }
            }
        }
        ta.commitNow()
    }

    private fun updateBottomTabs() {
        tabsAdapter.bindItems(tabs)
        (tabsRecycler.layoutManager as GridLayoutManager).spanCount = tabs.size
    }

    override fun updateTabs() {
        tabs.clear()
        if (presenter.getAuthState() == AuthState.AUTH) {
            tabs.addAll(allTabs)
        } else {
            tabs.addAll(allTabs.filter { it.screen !is Screens.Favorites })
        }
        updateBottomTabs()
    }

    override fun highlightTab(screenKey: String) {
        tabsAdapter.setSelected(screenKey)
        val screen = tabs.first { it.screen.screenKey == screenKey }.screen
        presenter.submitScreenAnalytics(screen)
        router.replaceScreen(screen)
    }

    fun addInStack(screenKey: String) {
        tabsStack.remove(screenKey)
        tabsStack.add(screenKey)
    }

    fun removeFromStack(screenKey: String) {
        tabsStack.remove(screenKey)
    }

    private val tabsListener = object : BottomTabsAdapter.Listener {
        override fun onTabClick(tab: Tab) {
            presenter.selectTab(tab.screen.screenKey)
        }
    }

    private val navigatorNew = object : SupportAppNavigator(this, R.id.root_container) {

        override fun applyCommand(command: Command?) {
            if (command is Back) {
                if (tabsStack.size <= 1) {
                    activityBack()
                    return
                }
                val fm = supportFragmentManager
                val ta = fm.beginTransaction()
                val fragment = fm.findFragmentByTag(tabsStack.last())
                fragment?.also { ta.detach(it) }
                removeFromStack(tabsStack.last())
                ta.commitNow()
                if (tabsStack.isNotEmpty()) {
                    presenter.selectTab(tabsStack.last())
                } else {
                    activityBack()
                }
                return
            } else if (command is Replace) {
                val inTabs =
                    allTabs.firstOrNull { it.screen.screenKey == command.screen.screenKey } != null
                if (inTabs) {
                    val fm = supportFragmentManager
                    val ta = fm.beginTransaction()
                    allTabs.forEach {
                        val fragment = fm.findFragmentByTag(it.screen.screenKey)
                        if (fragment != null) {
                            if (it.screen.screenKey == command.screen.screenKey) {
                                if (fragment.isDetached) {
                                    ta.attach(fragment)
                                }
                                ta.show(fragment)
                                addInStack(it.screen.screenKey)
                            } else {
                                ta.hide(fragment)
                            }
                        }
                    }
                    ta.commitNow()
                    return
                }
            }

            super.applyCommand(command)
        }

        private var exitToastShowed: Boolean = false
        override fun activityBack() {
            if (!exitToastShowed) {
                screenMessenger.showMessage("Нажмите кнопку назад снова, чтобы выйти из программы")
                exitToastShowed = true
                Handler().postDelayed({ exitToastShowed = false }, 3L * 1000)
            } else {
                super.activityBack()
            }
        }
    }

    data class Tab(
        val title: Int,
        val icon: Int,
        val screen: BaseAppScreen
    )
}
