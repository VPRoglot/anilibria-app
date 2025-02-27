package ru.radiationx.anilibria.screen.details

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.radiationx.anilibria.common.DetailDataConverter
import ru.radiationx.anilibria.common.DetailsState
import ru.radiationx.anilibria.common.LibriaDetails
import ru.radiationx.anilibria.common.fragment.GuidedRouter
import ru.radiationx.anilibria.screen.AuthGuidedScreen
import ru.radiationx.anilibria.screen.LifecycleViewModel
import ru.radiationx.anilibria.screen.PlayerEpisodesGuidedScreen
import ru.radiationx.anilibria.screen.PlayerScreen
import ru.radiationx.anilibria.screen.player.PlayerController
import ru.radiationx.data.entity.common.AuthState
import ru.radiationx.data.entity.domain.release.Release
import ru.radiationx.data.interactors.ReleaseInteractor
import ru.radiationx.data.repository.AuthRepository
import ru.radiationx.data.repository.FavoriteRepository
import ru.radiationx.shared.ktx.coRunCatching
import ru.terrakok.cicerone.Router
import timber.log.Timber
import toothpick.InjectConstructor

@InjectConstructor
class DetailHeaderViewModel(
    private val argExtra: DetailExtra,
    private val releaseInteractor: ReleaseInteractor,
    private val favoriteRepository: FavoriteRepository,
    private val authRepository: AuthRepository,
    private val converter: DetailDataConverter,
    private val router: Router,
    private val guidedRouter: GuidedRouter,
    private val playerController: PlayerController
) : LifecycleViewModel() {

    private val releaseId = argExtra.id

    val releaseData = MutableStateFlow<LibriaDetails?>(null)
    val progressState = MutableStateFlow<DetailsState>(DetailsState())

    private var currentRelease: Release? = null

    private var selectEpisodeJob: Job? = null
    private var favoriteDisposable: Job? = null

    init {
        updateProgress()
        releaseInteractor.getItem(releaseId)?.also {
            updateRelease(it)
        }
        releaseInteractor
            .observeFull(releaseId)
            .onEach {
                updateRelease(it)
            }
            .launchIn(viewModelScope)
    }

    override fun onResume() {
        super.onResume()

        selectEpisodeJob?.cancel()
        selectEpisodeJob = playerController
            .selectEpisodeRelay
            .onEach { episodeId ->
                router.navigateTo(PlayerScreen(releaseId, episodeId))
            }
            .launchIn(viewModelScope)
    }

    override fun onPause() {
        super.onPause()
        selectEpisodeJob?.cancel()
    }

    fun onContinueClick() {
        viewModelScope.launch {
            releaseInteractor.getEpisodes(releaseId).maxByOrNull { it.lastAccess }?.also {
                router.navigateTo(PlayerScreen(releaseId, it.id))
            }
        }
    }

    fun onPlayClick() {
        val release = currentRelease ?: return
        if (release.episodes.isEmpty()) return
        if (release.episodes.size == 1) {
            router.navigateTo(PlayerScreen(releaseId, null))
        } else {
            viewModelScope.launch {
                val episodeId =
                    releaseInteractor.getEpisodes(releaseId).maxByOrNull { it.lastAccess }?.id
                guidedRouter.open(PlayerEpisodesGuidedScreen(releaseId, episodeId))
            }
        }
    }

    fun onPlayWebClick() {

    }

    fun onFavoriteClick() {
        val release = currentRelease ?: return

        favoriteDisposable?.cancel()
        favoriteDisposable = viewModelScope.launch {
            if (authRepository.getAuthState() != AuthState.AUTH) {
                guidedRouter.open(AuthGuidedScreen())
                return@launch
            }
            coRunCatching {
                if (release.favoriteInfo.isAdded) {
                    favoriteRepository.deleteFavorite(releaseId)
                } else {
                    favoriteRepository.addFavorite(releaseId)
                }
            }.onSuccess { releaseItem ->
                currentRelease?.also { data ->
                    val newData = data.copy(
                        favoriteInfo = releaseItem.favoriteInfo
                    )
                    releaseInteractor.updateFullCache(newData)
                }
            }.onFailure {
                Timber.e(it)
            }
        }.apply {
            invokeOnCompletion { updateProgress() }
        }

        updateProgress()
    }

    fun onDescriptionClick() {

    }

    private fun updateRelease(release: Release) {
        currentRelease = release
        releaseData.value = converter.toDetail(release)
        updateProgress()
    }

    private fun updateProgress() {
        progressState.value = DetailsState(
            currentRelease == null,
            currentRelease == null || favoriteDisposable?.isActive ?: false
        )
    }
}