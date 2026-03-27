package com.photogram.feature.settings

sealed interface NotificationsSettingsUiAction {
    data class NewPhotosToggled(val enabled: Boolean)     : NotificationsSettingsUiAction
    data class ReactionsToggled(val enabled: Boolean)     : NotificationsSettingsUiAction
    data class MessagesToggled(val enabled: Boolean)      : NotificationsSettingsUiAction
    data class AlbumInvitesToggled(val enabled: Boolean)  : NotificationsSettingsUiAction
    data class AlbumUpdatesToggled(val enabled: Boolean)  : NotificationsSettingsUiAction
    data class EventRemindersToggled(val enabled: Boolean): NotificationsSettingsUiAction
}
