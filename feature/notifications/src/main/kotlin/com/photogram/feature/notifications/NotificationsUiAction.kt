package com.photogram.feature.notifications

internal sealed interface NotificationsUiAction {
    data class FilterSelected(val filter: NotifFilter)  : NotificationsUiAction
    data class NotifTapped(val id: String)              : NotificationsUiAction
    data object FilterIconTapped                        : NotificationsUiAction
    data object FilterSheetDismissed                    : NotificationsUiAction
    data object HomeNavTapped                           : NotificationsUiAction
    data object GalleryNavTapped                        : NotificationsUiAction
    data object CreateNavTapped                         : NotificationsUiAction
    data object ProfileNavTapped                        : NotificationsUiAction
}
