package com.jerson.soundeyes.presentation.navGraph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jerson.soundeyes.consts.Const
import com.jerson.soundeyes.presentation.AboutScreen
import com.jerson.soundeyes.presentation.bluetooth.BluetoothScreen
import com.jerson.soundeyes.presentation.bluetooth.BluetoothViewModel
import com.jerson.soundeyes.presentation.camera.LocalCameraEvent
import com.jerson.soundeyes.presentation.camera.LocalCameraScreen
import com.jerson.soundeyes.presentation.camera.LocalCameraViewModel
import com.jerson.soundeyes.presentation.home.HomeVewModel
import com.jerson.soundeyes.presentation.home.MainScreen


@Composable
fun NavGraph(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = Route.HomeScreen.route) {
        composable(Route.HomeScreen.route) {
            val viewModel: HomeVewModel = hiltViewModel()
            val state = viewModel.state.value
            val navigationEvent = viewModel.navigationEvent
            MainScreen(
                state = state,
                event = viewModel::onEvent,
                navigationEvent = navigationEvent,
                onNavigateCameraScreen = {resolution ->
                    val str:String = if(resolution == Const.DESEMPENHO )
                        "640x640"
                    else
                        "720x1280"
                    navHostController.navigate(Route.LocalCameraScreen.route.replace("resolution",str))
                },
                onNavigateBluetoothCameraScreen = {
                    navHostController.navigate(Route.BluetoothCameraScreen.route)
                },
                onNavigateAboutScreen = {
                    navHostController.navigate(Route.AboutScreen.route)
                }
            )
        }
        composable(Route.LocalCameraScreen.route,
            arguments = listOf(navArgument("resolution"){type = NavType.StringType})
        ) {
            val viewModel: LocalCameraViewModel = hiltViewModel()
            val state = viewModel.state.value
            LocalCameraScreen(
                state = state,
                event = viewModel::onEvent
            )
        }
        composable(Route.BluetoothCameraScreen.route){
            val viewModel: BluetoothViewModel = hiltViewModel()
            val state = viewModel.state.value
            BluetoothScreen(
                state = state,
                onBack = {
                    navHostController.popBackStack()
                }
            )
        }
        composable(Route.AboutScreen.route){
            AboutScreen(
                onBack = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}