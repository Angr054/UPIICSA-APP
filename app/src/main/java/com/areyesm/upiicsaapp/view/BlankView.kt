package com.areyesm.upiicsaapp.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.areyesm.upiicsaapp.navigation.AppScreen
import com.google.firebase.auth.FirebaseAuth

//Verifica si existe una sesión activa
@Composable
fun BlankView(navController: NavController) {

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            navController.navigate(AppScreen.Home.route) {
                popUpTo(AppScreen.Blank.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            navController.navigate(AppScreen.Login.route) {
                popUpTo(AppScreen.Blank.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
}
