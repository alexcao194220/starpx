package com.alexcao.starpx.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alexcao.starpx.screen.home.HomeScreen
import com.alexcao.starpx.screen.image_detail.ImageDetailScreen
import com.alexcao.starpx.screen.login.LoginScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    context: Context
) {
    val navController = rememberNavController()
    NavHost(navController = navController, NavigationItem.Login.route, modifier = modifier) {
        composable(NavigationItem.Home.route) {
            HomeScreen(
                navController = navController,
            )
        }
        composable(NavigationItem.Login.route) {
            LoginScreen(
                navController = navController
            )
        }
        composable(
            "${NavigationItem.ImageDetail.route}/{url}",
            arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                }
            )
        ) {
            val url = it.arguments?.getString("url")
            ImageDetailScreen(
                url = url!!,
                context = context
            )
        }
    }
}