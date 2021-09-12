package ru.radiationx.anilibria.ui.fragments.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_main_base.*
import kotlinx.android.synthetic.main.fragment_webview.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.presentation.auth.social.AuthSocialPresenter
import ru.radiationx.anilibria.presentation.auth.social.AuthSocialView
import ru.radiationx.anilibria.ui.common.webpage.WebPageStateWebViewClient
import ru.radiationx.anilibria.ui.common.webpage.WebPageViewState
import ru.radiationx.anilibria.ui.common.webpage.compositeWebViewClientOf
import ru.radiationx.anilibria.ui.fragments.BaseFragment
import ru.radiationx.anilibria.ui.fragments.auth.otp.AuthPatternWebViewClient
import ru.radiationx.anilibria.utils.Utils
import ru.radiationx.data.datasource.remote.address.ApiConfig
import ru.radiationx.data.entity.app.auth.SocialAuth
import ru.radiationx.shared.ktx.android.gone
import ru.radiationx.shared_app.analytics.LifecycleTimeCounter
import ru.radiationx.shared_app.di.injectDependencies
import javax.inject.Inject


/**
 * Created by radiationx on 31.12.17.
 */
class AuthSocialFragment : BaseFragment(), AuthSocialView {

    companion object {
        private const val ARG_KEY = "key"

        fun newInstance(key: String) = AuthSocialFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_KEY, key)
            }
        }
    }

    private val authPatternWebViewClient by lazy { AuthPatternWebViewClient(presenter::onSuccessAuthResult) }

    private val analyticsWebViewClient by lazy { AnalyticsWebViewClient(presenter::sendAnalyticsPageError) }

    private val webPageWebViewClient by lazy { WebPageStateWebViewClient(presenter::onPageStateChanged) }

    private val compositeWebViewClient by lazy {
        compositeWebViewClientOf(
            authPatternWebViewClient,
            analyticsWebViewClient,
            webPageWebViewClient
        )
    }

    private val useTimeCounter by lazy {
        LifecycleTimeCounter(presenter::submitUseTime)
    }

    @Inject
    lateinit var apiConfig: ApiConfig

    @InjectPresenter
    lateinit var presenter: AuthSocialPresenter

    @ProvidePresenter
    fun providePresenter(): AuthSocialPresenter =
        getDependency(AuthSocialPresenter::class.java, screenScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(screenScope)
        super.onCreate(savedInstanceState)
        arguments?.let {
            presenter.argKey = it.getString(ARG_KEY, presenter.argKey)
        }
    }

    override fun getLayoutResource(): Int = R.layout.fragment_webview

    override val statusBarVisible: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(useTimeCounter)
        appbarLayout.gone()

        webView.apply {
            settings.apply {
                setAppCacheEnabled(false)
                cacheMode = WebSettings.LOAD_NO_CACHE
            }
            webViewClient = compositeWebViewClient
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onDestroyView() {
        webView.webViewClient = null
        webView.stopLoading()
        super.onDestroyView()
    }

    override fun loadPage(data: SocialAuth) {
        authPatternWebViewClient.authData = data
        webView.loadUrl(data.socialUrl)
    }

    override fun showState(state: AuthSocialScreenState) {
        Log.d("kekeke", "show state $state")
        val anyLoading = state.isAuthProgress || state.pageState == WebPageViewState.Loading
        progressBarWv.isVisible = anyLoading
        webView.isInvisible = anyLoading
        showClearCookies(state.showClearCookies)
    }

    private var kekDialog: AlertDialog? = null

    private fun showClearCookies(show: Boolean) {
        val currentDialog = kekDialog
        if (!show) {
            currentDialog?.hide()
        } else {
            val dialog = currentDialog ?: run {
                AlertDialog.Builder(requireContext())
                    .setCancelable(false)
                    .setMessage("Обнаружен автоматический вход по старым данным авторизации. Хотите продолжить?")
                    .setPositiveButton("Продолжить") { _, _ ->
                        presenter.onContinueClick()
                    }
                    .setNegativeButton("Начать заново") { _, _ ->
                        presenter.onClearDataClick()
                    }
                    .setNeutralButton("Отмена") { _, _ ->
                        presenter.onBackPressed()
                    }
                    .create()
            }
            if (!dialog.isShowing) {
                dialog.show()
            }
        }
    }

    override fun showError() {
        AlertDialog.Builder(requireContext())
            .setMessage("Не найден связанный аккаунт.\n\nЕсли у вас уже есть аккаунт на сайте AniLibria.tv, то привяжите этот аккаунт в личном кабинете.\n\nЕсли аккаунта нет, то зарегистрируйте его на сайте.")
            .setPositiveButton("Перейти") { _, _ ->
                Utils.externalLink("${apiConfig.siteUrl}/pages/cp.php")
            }
            .setNegativeButton("Отмена", null)
            .show()
            .setOnDismissListener {
                presenter.onUserUnderstandWhatToDo()
            }
    }
}