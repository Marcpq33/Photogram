package com.photogram.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.datastore.UserPreferencesRepository
import com.photogram.core.navigation.PhotogramDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal enum class ChatSnackbarKey { NewConversation }

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState(allItems = emptyList()))
    internal val uiState = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navEvent: SharedFlow<String> = _navEvent.asSharedFlow()

    private val _snackbarEvent = MutableSharedFlow<ChatSnackbarKey>(extraBufferCapacity = 1)
    internal val snackbarEvent: SharedFlow<ChatSnackbarKey> = _snackbarEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            if (userPreferences.userData.first().isDemoMode) {
                _uiState.value = ChatListUiState(allItems = ChatDefaults.items)
            }
        }
    }

    internal fun onAction(action: ChatListUiAction) {
        when (action) {
            is ChatListUiAction.FilterSelected ->
                _uiState.update { it.copy(selectedFilter = action.filter) }

            is ChatListUiAction.SearchQueryChanged ->
                _uiState.update { it.copy(searchQuery = action.query) }

            is ChatListUiAction.ChatItemTapped ->
                navigate(PhotogramDestination.ChatDetail.createRoute(action.id))

            ChatListUiAction.FabTapped ->
                snackbar(ChatSnackbarKey.NewConversation)
        }
    }

    private fun navigate(route: String) {
        viewModelScope.launch { _navEvent.emit(route) }
    }

    private fun snackbar(key: ChatSnackbarKey) {
        viewModelScope.launch { _snackbarEvent.emit(key) }
    }
}
