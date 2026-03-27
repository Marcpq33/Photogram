package com.photogram.feature.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.photogram.core.navigation.PhotogramDestination

fun NavGraphBuilder.authNavGraph(
    onAuthSuccess: () -> Unit,
) {
    navigation(
        route = PhotogramDestination.AuthGraph.route,
        startDestination = PhotogramDestination.Login.route,
    ) {
        composable(route = PhotogramDestination.Login.route) {
            AuthScreen(onAuthSuccess = onAuthSuccess)
        }
        composable(route = PhotogramDestination.Register.route) {
            // Placeholder — register flow wired in a future milestone
        }
    }
}
