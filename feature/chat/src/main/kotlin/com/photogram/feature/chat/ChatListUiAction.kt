package com.photogram.feature.chat

internal sealed interface ChatListUiAction {
    data class FilterSelected(val filter: ChatFilter)   : ChatListUiAction
    data class SearchQueryChanged(val query: String)    : ChatListUiAction
    data class ChatItemTapped(val id: String)           : ChatListUiAction
    data object FabTapped                               : ChatListUiAction
}
