package com.jerson.soundeyes.feature_app.presentation.navGraph

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jerson.soundeyes.feature_app.presentation.yoloClassifier.YoloClassifierScreen
import com.jerson.soundeyes.feature_app.presentation.main.MainScreen


@Composable
fun NavGraph(navHostController: NavHostController) {

    NavHost(navController = navHostController, startDestination = Route.MainScreen.route) {
       composable(Route.MainScreen.route) {MainScreen(navController = navHostController) }

        composable(Route.YoloClassifierScreen.route) {
            YoloClassifierScreen(navHostController)}


    }
}