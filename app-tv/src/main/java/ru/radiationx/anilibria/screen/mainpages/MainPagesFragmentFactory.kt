package ru.radiationx.anilibria.screen.mainpages

import androidx.fragment.app.Fragment
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.Row
import ru.radiationx.anilibria.screen.main.MainFragment
import ru.radiationx.shared_app.di.ScopeProvider
import ru.radiationx.shared_app.di.putScopeArgument

class MainPagesFragmentFactory(
    private val scopeProvider: ScopeProvider
) : BrowseSupportFragment.FragmentFactory<Fragment>() {

    companion object {
        const val ID_MAIN = 1L
        const val ID_MY = 2L
        const val ID_SERIES = 3L
        const val ID_MOVIES = 4L
        const val ID_SEARCH = 5L
        const val ID_YOUTUBE = 6L
        const val ID_PROFILE = 7L

        val ids = listOf(ID_MAIN, ID_MY, ID_SERIES, ID_MOVIES, ID_SEARCH, ID_YOUTUBE, ID_PROFILE)

        val variant1 = mapOf(
            ID_MAIN to "Главная",
            ID_MY to "Я смотрю",
            ID_SERIES to "Сериалы",
            ID_MOVIES to "Фильмы",
            ID_SEARCH to "Поиск",
            ID_YOUTUBE to "YouTube",
            ID_PROFILE to "Профиль"
        )
    }

    private val fragments = mutableMapOf<Any, Fragment>()

    override fun createFragment(rowObj: Any): Fragment {
        val row = rowObj as Row
        val fragment = fragments[row]
        if (fragment == null) {
            fragments[row] = getFragmentByRow(row).putScopeArgument(scopeProvider.screenScopeTag)
        }
        return fragments.getValue(row)
    }

    private fun getFragmentByRow(row: Row): Fragment = when (row.id) {
        ID_MAIN -> MainFragment()
        ID_MY -> MainFragment()
        else -> EmptyFragment()
    }
}