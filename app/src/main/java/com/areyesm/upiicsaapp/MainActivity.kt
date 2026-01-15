package com.areyesm.upiicsaapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.areyesm.upiicsaapp.navigation.AppNavigation
import com.areyesm.upiicsaapp.ui.theme.UpiicsaAppTheme
import com.areyesm.upiicsaapp.viewModel.CampusViewModel
import com.areyesm.upiicsaapp.viewModel.FacebookLoginViewModel
import com.areyesm.upiicsaapp.viewModel.GithubLoginViewModel
import com.areyesm.upiicsaapp.viewModel.GoogleLoginViewModel
import com.areyesm.upiicsaapp.viewModel.LocationViewModel
import com.areyesm.upiicsaapp.viewModel.LoginViewModel
import com.areyesm.upiicsaapp.viewModel.PhoneLoginViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import kotlin.getValue

@Suppress("INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION_WARNING")
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private lateinit var callbackManager: CallbackManager

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Facebook SDK Init
        FacebookSdk.sdkInitialize(applicationContext)
        // Facebook CallbackManager
        callbackManager = CallbackManager.Factory.create()

        // ViewModels
        val loginVM: LoginViewModel by viewModels()
        val phoneLoginVM: PhoneLoginViewModel by viewModels()
        val googleLoginVM: GoogleLoginViewModel by viewModels()
        val facebookLoginVM: FacebookLoginViewModel by viewModels()
        val githubLoginVM: GithubLoginViewModel by viewModels()
        val campusVM: CampusViewModel by viewModels()
        val locationVM: LocationViewModel by viewModels()

        // Registro único Callback de Facebook
        facebookLoginVM.registerFacebookCallback(callbackManager)

        enableEdgeToEdge()

        setContent {
            UpiicsaAppTheme {
                navController = rememberNavController()
                AppNavigation(
                    navController,
                    loginVM,
                    phoneLoginVM,
                    googleLoginVM,
                    facebookLoginVM,
                    githubLoginVM,
                    campusVM,
                    locationVM
                )
            }
        }
    }

    @Deprecated("Facebook SDK aún utiliza onActivityResult")
    @Suppress("DEPRECATION")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
