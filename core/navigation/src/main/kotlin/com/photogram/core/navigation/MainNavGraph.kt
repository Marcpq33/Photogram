package com.photogram.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation

// Main graph — feature destinations injected via content lambdas to avoid core→feature dependency
fun NavGraphBuilder.mainNavGraph(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
    homeContent: @Composable () -> Unit = {},
    cameraContent: @Composable () -> Unit = {},
    profileContent: @Composable () -> Unit = {},
    settingsContent: @Composable () -> Unit = {},
    privacyContent: @Composable () -> Unit = {},
    editProfileContent: @Composable () -> Unit = {},
    changePasswordContent: @Composable () -> Unit = {},
    notificationsContent: @Composable () -> Unit = {},
    chatListContent: @Composable () -> Unit = {},
    chatDetailContent: @Composable () -> Unit = {},
    notificationsSettingsContent: @Composable () -> Unit = {},
    languagePickerContent: @Composable () -> Unit = {},
    storageDetailContent: @Composable () -> Unit = {},
    storyViewerContent: @Composable () -> Unit = {},
    galleryContent: @Composable () -> Unit = {},
    albumDetailContent: @Composable () -> Unit = {},
    eventsContent: @Composable () -> Unit = {},
    recapsContent: @Composable () -> Unit = {},
    mediaViewerContent: @Composable () -> Unit = {},
) {
    navigation(
        route = PhotogramDestination.MainGraph.route,
        startDestination = PhotogramDestination.Home.route,
    ) {
        composable(route = PhotogramDestination.Home.route) {
            homeContent()
        }
        composable(route = PhotogramDestination.Camera.route) {
            cameraContent()
        }
        composable(route = PhotogramDestination.Gallery.route) {
            galleryContent()
        }
        composable(route = PhotogramDestination.ChatList.route) {
            chatListContent()
        }
        composable(
            route = PhotogramDestination.ChatDetail.route,
            arguments = listOf(
                navArgument(PhotogramDestination.ChatDetail.ARG_ALBUM_ID) {
                    type = NavType.StringType
                },
            ),
        ) {
            chatDetailContent()
        }
        composable(route = PhotogramDestination.Notifications.route) {
            notificationsContent()
        }
        composable(route = PhotogramDestination.Profile.route) {
            profileContent()
        }
        composable(route = PhotogramDestination.Settings.route) {
            settingsContent()
        }
        composable(route = PhotogramDestination.Privacy.route) {
            privacyContent()
        }
        composable(route = PhotogramDestination.EditProfile.route) {
            editProfileContent()
        }
        composable(route = PhotogramDestination.ChangePassword.route) {
            changePasswordContent()
        }
        composable(route = PhotogramDestination.NotificationsSettings.route) {
            notificationsSettingsContent()
        }
        composable(route = PhotogramDestination.LanguagePicker.route) {
            languagePickerContent()
        }
        composable(route = PhotogramDestination.StorageDetail.route) {
            storageDetailContent()
        }
        composable(
            route = PhotogramDestination.StoryViewer.route,
            arguments = listOf(
                navArgument(PhotogramDestination.StoryViewer.ARG_ALBUM_ID) {
                    type = NavType.StringType
                },
            ),
        ) {
            storyViewerContent()
        }
        composable(
            route = PhotogramDestination.AlbumDetail.route,
            arguments = listOf(
                navArgument(PhotogramDestination.AlbumDetail.ARG_ALBUM_ID) {
                    type = NavType.StringType
                },
            ),
        ) {
            albumDetailContent()
        }
        composable(
            route = PhotogramDestination.EventList.route,
            arguments = listOf(
                navArgument(PhotogramDestination.EventList.ARG_ALBUM_ID) {
                    type = NavType.StringType
                },
            ),
        ) {
            eventsContent()
        }
        composable(
            route = PhotogramDestination.RecapList.route,
            arguments = listOf(
                navArgument(PhotogramDestination.RecapList.ARG_ALBUM_ID) {
                    type = NavType.StringType
                },
            ),
        ) {
            recapsContent()
        }
        composable(
            route = PhotogramDestination.MediaViewer.route,
            arguments = listOf(
                navArgument(PhotogramDestination.MediaViewer.ARG_MEDIA_ID) {
                    type = NavType.StringType
                },
            ),
        ) {
            mediaViewerContent()
        }
    }
}
