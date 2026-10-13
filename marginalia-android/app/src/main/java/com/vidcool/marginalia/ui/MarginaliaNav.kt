package com.vidcool.marginalia.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vidcool.marginalia.ui.search.SearchScreen
import com.vidcool.marginalia.ui.search.SearchViewModel
import com.vidcool.marginalia.ui.settings.SettingsScreen
import com.vidcool.marginalia.ui.web.WebScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val WEB = "web/{encodedUrl}"

    fun webRoute(url: String): String {
        val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8)
        return "web/$encoded"
    }
}

@Composable
fun MarginaliaNavHost(viewModel: SearchViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SEARCH) {
        composable(Routes.SEARCH) {
            SearchScreen(
                viewModel = viewModel,
                onOpenWebsiteTab = { url ->
                    navController.navigate(Routes.webRoute(url))
                },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.WEB,
            arguments = listOf(navArgument("encodedUrl") { type = NavType.StringType }),
        ) { entry ->
            val encoded = entry.arguments?.getString("encodedUrl").orEmpty()
            val url = URLDecoder.decode(encoded, StandardCharsets.UTF_8)
            WebScreen(
                initialUrl = url.ifBlank { "https://marginalia-search.com/" },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
