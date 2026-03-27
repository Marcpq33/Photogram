package com.photogram

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.navigation.PhotogramDestination
import com.photogram.core.navigation.mainNavGraph
import com.photogram.feature.auth.authNavGraph
import com.photogram.feature.chat.ChatDetailScreen
import com.photogram.feature.chat.ChatListScreen
import com.photogram.feature.home.HomeScreen
import com.photogram.feature.notifications.NotificationsScreen
import com.photogram.feature.privacy.PrivacyScreen
import com.photogram.feature.profile.ProfileScreen
import com.photogram.feature.settings.ChangePasswordScreen
import com.photogram.feature.settings.EditProfileScreen
import com.photogram.feature.settings.LanguagePickerScreen
import com.photogram.feature.settings.NotificationsSettingsScreen
import com.photogram.feature.settings.SettingsScreen
import com.photogram.feature.settings.StorageDetailScreen
import com.photogram.feature.story.StoryViewerScreen
import com.photogram.feature.gallery.GalleryScreen
import com.photogram.feature.gallery.MediaViewerScreen
import com.photogram.feature.albums.AlbumDetailScreen
import com.photogram.feature.events.EventScreen
import com.photogram.feature.recaps.RecapsScreen
import com.photogram.feature.upload.CameraScreen
import kotlinx.coroutines.flow.SharedFlow

/**
 * Root navigation host for the app.
 *
 * [startDestination] governs the initial graph on composition:
 * - userId == null in DataStore → AuthGraph  (signed out)
 * - userId != null in DataStore → MainGraph  (already signed in)
 *
 * [authEvent] drives runtime graph transition after magic-link completion.
 * When the deep-link lands and Supabase establishes a new session, AppViewModel
 * emits a single Unit on [authEvent]. The LaunchedEffect here receives it and
 * navigates imperatively to MainGraph, clearing the auth back stack.
 *
 * Note: startDestination on NavHost is read only once at initial composition.
 * Changing it after the fact has no effect, which is why authEvent is needed
 * for mid-session navigation.
 */
@Composable
fun PhotogramApp(
    startDestination: String,
    authEvent: SharedFlow<Unit>,
    languageCode: String = "EN",
) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        authEvent.collect {
            navController.navigate(PhotogramDestination.MainGraph.route) {
                popUpTo(PhotogramDestination.AuthGraph.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    CompositionLocalProvider(LocalLanguageCode provides languageCode) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        authNavGraph(
            onAuthSuccess = {
                // Kept for future milestones that trigger success from within the auth graph
                // (e.g. OTP code entry screen). Deep-link completion is handled by authEvent above.
                navController.navigate(PhotogramDestination.MainGraph.route) {
                    popUpTo(PhotogramDestination.AuthGraph.route) { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
        mainNavGraph(
            navController   = navController,
            homeContent          = { HomeScreen(onNavigate = { route -> navController.navigate(route) }) },
            cameraContent        = {
                CameraScreen(
                    onClose              = { navController.popBackStack() },
                    onUploadPlaceholder  = { /* upload wired in next milestone */ },
                )
            },
            notificationsContent = { NotificationsScreen(onNavigate = { route -> navController.navigate(route) }) },
            chatListContent      = { ChatListScreen(onNavigate = { route -> navController.navigate(route) }, onBack = { navController.popBackStack() }) },
            chatDetailContent    = { ChatDetailScreen(onBack = { navController.popBackStack() }) },
            profileContent  = { ProfileScreen(onNavigate = { route -> navController.navigate(route) }) },
            settingsContent = {
                SettingsScreen(
                    onBack                    = { navController.popBackStack() },
                    onNavigateToPrivacy       = { navController.navigate(PhotogramDestination.Privacy.route) },
                    onNavigateToEditProfile   = { navController.navigate(PhotogramDestination.EditProfile.route) },
                    onNavigateToPassword      = { navController.navigate(PhotogramDestination.ChangePassword.route) },
                    onNavigateToNotifications = { navController.navigate(PhotogramDestination.NotificationsSettings.route) },
                    onNavigateToLanguage      = { navController.navigate(PhotogramDestination.LanguagePicker.route) },
                    onNavigateToStorage       = { navController.navigate(PhotogramDestination.StorageDetail.route) },
                    onLogOut                  = {
                        navController.navigate(PhotogramDestination.AuthGraph.route) {
                            popUpTo(PhotogramDestination.MainGraph.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            },
            privacyContent               = { PrivacyScreen(onBack = { navController.popBackStack() }) },
            editProfileContent           = { EditProfileScreen(onBack = { navController.popBackStack() }) },
            changePasswordContent        = { ChangePasswordScreen(onBack = { navController.popBackStack() }) },
            notificationsSettingsContent = { NotificationsSettingsScreen(onBack = { navController.popBackStack() }) },
            languagePickerContent        = { LanguagePickerScreen(onBack = { navController.popBackStack() }) },
            storageDetailContent         = { StorageDetailScreen(onBack = { navController.popBackStack() }) },
            storyViewerContent           = { StoryViewerScreen(onClose = { navController.popBackStack() }) },
            galleryContent               = { GalleryScreen(onNavigate = { route -> navController.navigate(route) }, onBack = { navController.popBackStack() }) },
            albumDetailContent           = { AlbumDetailScreen(onBack = { navController.popBackStack() }, onNavigate = { route -> navController.navigate(route) }) },
            eventsContent                = { EventScreen(onNavigate = { route -> navController.navigate(route) }) },
            recapsContent                = { RecapsScreen(onNavigate = { route -> navController.navigate(route) }) },
            mediaViewerContent           = { MediaViewerScreen(onBack = { navController.popBackStack() }) },
        )
    }
    } // CompositionLocalProvider
}
