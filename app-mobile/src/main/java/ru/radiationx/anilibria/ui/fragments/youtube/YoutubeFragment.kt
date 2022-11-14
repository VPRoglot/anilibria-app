package ru.radiationx.anilibria.ui.fragments.youtube

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_refresh.*
import kotlinx.android.synthetic.main.fragment_main_base.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.databinding.FragmentListRefreshBinding
import ru.radiationx.anilibria.extension.disableItemChangeAnimation
import ru.radiationx.anilibria.model.YoutubeItemState
import ru.radiationx.anilibria.presentation.youtube.YoutubePresenter
import ru.radiationx.anilibria.presentation.youtube.YoutubeView
import ru.radiationx.anilibria.ui.adapters.PlaceholderListItem
import ru.radiationx.anilibria.ui.fragments.BaseFragment
import ru.radiationx.anilibria.ui.fragments.ToolbarShadowController
import ru.radiationx.shared_app.di.injectDependencies

class YoutubeFragment : BaseFragment<FragmentListRefreshBinding>(R.layout.fragment_list_refresh),
    YoutubeView {

    private val youtubeAdapter: YoutubeAdapter by lazy {
        YoutubeAdapter(
            loadMoreListener = { presenter.loadMore() },
            loadRetryListener = { presenter.loadMore() },
            listener = adapterListener,
            emptyPlaceHolder = PlaceholderListItem(
                R.drawable.ic_youtube,
                R.string.placeholder_title_nodata_base,
                R.string.placeholder_desc_nodata_base
            ),
            errorPlaceHolder = PlaceholderListItem(
                R.drawable.ic_youtube,
                R.string.placeholder_title_errordata_base,
                R.string.placeholder_desc_nodata_base
            )
        )
    }

    @InjectPresenter
    lateinit var presenter: YoutubePresenter

    @ProvidePresenter
    fun providePresenter(): YoutubePresenter =
        getDependency(YoutubePresenter::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(screenScope)
        super.onCreate(savedInstanceState)
    }

    override val statusBarVisible: Boolean = true

    override fun onCreateBinding(view: View): FragmentListRefreshBinding {
        return FragmentListRefreshBinding.bind(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.apply {
            title = getString(R.string.fragment_title_youtube)
        }

        refreshLayout.setOnRefreshListener { presenter.refresh() }

        recyclerView.apply {
            adapter = youtubeAdapter
            layoutManager = LinearLayoutManager(recyclerView.context)
            disableItemChangeAnimation()
        }

        ToolbarShadowController(
            recyclerView,
            appbarLayout
        ) {
            updateToolbarShadow(it)
        }
    }

    override fun onBackPressed(): Boolean {
        presenter.onBackPressed()
        return true
    }

    override fun showState(state: YoutubeScreenState) {
        progressBarList.isVisible = state.data.emptyLoading
        refreshLayout.isRefreshing = state.data.refreshLoading
        youtubeAdapter.bindState(state)
    }

    private val adapterListener = object : YoutubeAdapter.ItemListener {

        override fun onItemClick(item: YoutubeItemState, position: Int) {
            presenter.onItemClick(item)
        }

        override fun onItemLongClick(item: YoutubeItemState): Boolean {
            return false
        }
    }

}