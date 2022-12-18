package ru.radiationx.anilibria.screen.player.end_episode

import android.os.Bundle
import android.view.View
import androidx.leanback.widget.GuidedAction
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.screen.player.BasePlayerGuidedFragment
import ru.radiationx.quill.viewModel

class EndEpisodeGuidedFragment : BasePlayerGuidedFragment() {

    companion object {
        private const val REPLAY_ACTION_ID = 0L
        private const val NEXT_ACTION_ID = 1L
    }

    private val viewModel by viewModel<EndEpisodeViewModel> { argExtra }

    override fun onProvideTheme(): Int = R.style.AppTheme_Player_LeanbackWizard

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
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