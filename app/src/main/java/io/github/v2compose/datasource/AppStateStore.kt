package io.github.v2compose.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStateStore @Inject constructor(){

    private val _hasCheckingInTips = MutableStateFlow(false)
    val hasCheckingInTips: Flow<Boolean> = flow {
        emitAll(_hasCheckingInTips)
    }

    suspend fun updateHasCheckingInTips(value: Boolean) {
        _hasCheckingInTips.emit(value)
    }

}