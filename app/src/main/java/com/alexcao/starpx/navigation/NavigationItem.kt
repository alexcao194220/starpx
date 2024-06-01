package com.alexcao.starpx.navigation

enum class Screen {
    HOME,
    LOGIN,
    IMAGE_DETAIL,
}

sealed class NavigationItem(val route: String) {
    data object Home : NavigationItem(Screen.HOME.name)
    data object Login : NavigationItem(Screen.LOGIN.name)
    data object ImageDetail : NavigationItem(Screen.IMAGE_DETAIL.name)
}