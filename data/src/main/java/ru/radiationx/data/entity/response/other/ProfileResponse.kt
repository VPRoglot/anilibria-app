package ru.radiationx.data.entity.response.other

import ru.radiationx.data.entity.common.AuthState

data class ProfileResponse(
    val id: Int = NO_ID,
    val nick: String = NO_VALUE,
    val avatarUrl: String? = NO_VALUE,
    val authState: AuthState = AuthState.NO_AUTH
) {

    companion object {
        const val NO_ID = -1
        const val NO_VALUE = ""
    }
}
