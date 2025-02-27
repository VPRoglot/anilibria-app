package ru.radiationx.anilibria.model.loading

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Deprecated("just use stateflow", level = DeprecationLevel.ERROR)
class StateController<T : Any>(defaultState: T) {

    private val stateRelay = MutableStateFlow(defaultState)

    var currentState: T
        get() = requireNotNull(stateRelay.value)
        private set(value) {
            stateRelay.value = value
        }

    fun update(block: (T) -> T) {
        stateRelay.update(block)
    }

    fun observeState(): Flow<T> {
        return stateRelay
    }
}