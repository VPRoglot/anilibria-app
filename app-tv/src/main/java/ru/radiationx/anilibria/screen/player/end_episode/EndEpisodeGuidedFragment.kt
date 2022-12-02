package ru.radiationx.anilibria.screen.player.end_episode

import android.os.Bundle
import androidx.leanback.widget.GuidedAction
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.screen.player.BasePlayerGuidedFragment
import ru.radiationx.quill.quillViewModel

class EndEpisodeGuidedFragment : BasePlayerGuidedFragment() {

    companion object {
        private const val REPLAY_ACTION_ID = 0L
        private const val NEXT_ACTION_ID = 1L
    }

    private val viewModel by quillViewModel<EndEpisodeViewModel>()

    override fun onProvideTheme(): Int = R.style.AppTheme_Player_LeanbackWizard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
        viewModel.argReleaseId = releaseId
        viewModel.argEpisodeId = episodeId
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(REPLAY_ACTION_ID)
                .title("Начать серию заново")
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(NEXT_ACTION_ID)
                .title("Включить следующую серию")
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        super.onGuidedActionClicked(action)
        when (action.id) {
            REPLAY_ACTION_ID -> viewModel.onReplayClick()
            NEXT_ACTION_ID -> viewModel.onNextClick()
        }
    }
}