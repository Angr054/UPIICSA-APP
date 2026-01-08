package com.areyesm.upiicsaapp.navigation

sealed class AppScreen(val route: String) {
    object Blank: AppScreen(route = "blank")
    object Home: AppScreen(route = "home")
    object Login: AppScreen(route = "login")
    object Signup: AppScreen(route= "signup")
}