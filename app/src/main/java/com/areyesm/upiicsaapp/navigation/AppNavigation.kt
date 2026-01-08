package com.areyesm.upiicsaapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.areyesm.upiicsaapp.view.BlankView
import com.areyesm.upiicsaapp.view.HomeView
import com.areyesm.upiicsaapp.view.LoginView
import com.areyesm.upiicsaapp.view.SignUpView
import com.areyesm.upiicsaapp.viewModel.FacebookLoginViewModel
import com.areyesm.upiicsaapp.viewModel.GithubLoginViewModel
import com.areyesm.upiicsaapp.viewModel.GoogleLoginViewModel
import com.areyesm.upiicsaapp.viewModel.LoginViewModel
import com.areyesm.upiicsaapp.viewModel.PhoneLoginViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    loginVM: LoginViewModel,
    phoneLoginVM: PhoneLoginViewModel,
    googleLoginVM: GoogleLoginViewModel,
    facebookLoginVM: FacebookLoginViewModel,
    githubLoginVM: GithubLoginViewModel
) {
    NavHost(navController = navController, startDestination = AppScreen.Blank.route){

        composable(route = AppScreen.Blank.route) {
            BlankView(navController = navController)
        }

        composable(AppScreen.Home.route){
            HomeView(navController = navController, loginVM = loginVM)
        }

        composable(route = AppScreen.Login.route){
            LoginView(navController, loginVM, phoneLoginVM, googleLoginVM, facebookLoginVM, githubLoginVM)
        }

        composable(route = AppScreen.Signup.route) {
            SignUpView(navController, loginVM)
        }
    }
}