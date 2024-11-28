package id.oversteken.ui

import HomeScreen
import PermissionsScreen
import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import id.oversteken.ui.screens.HomeScreen
import id.oversteken.ui.screens.PermissionsScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val permissionsState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
    )

    // Automatically navigate whenever permissions are not met.
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        navController.currentDestination?.route?.let {
            navController.popBackStack(
                route = it,
                inclusive = true,
                saveState = false
            )
        }
        navController.navigate(if (permissionsState.allPermissionsGranted) HomeScreen.route else PermissionsScreen.route)
    }

    NavHost(
        navController = navController,
        startDestination = HomeScreen.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(PermissionsScreen.route) {
            PermissionsScreen(navController, permissionsState = permissionsState)
        }
    }
}