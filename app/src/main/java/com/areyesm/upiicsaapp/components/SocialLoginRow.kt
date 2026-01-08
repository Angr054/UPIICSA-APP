package com.areyesm.upiicsaapp.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.components.GitHubSocialButton
import com.areyesm.upiicsaapp.navigation.AppScreen
import com.areyesm.upiicsaapp.ui.theme.Green40
import com.areyesm.upiicsaapp.view.PhoneAuthDialog
import com.areyesm.upiicsaapp.viewModel.FacebookLoginViewModel
import com.areyesm.upiicsaapp.viewModel.GithubLoginViewModel
import com.areyesm.upiicsaapp.viewModel.GoogleLoginViewModel
import com.areyesm.upiicsaapp.viewModel.PhoneLoginViewModel

@Composable
fun SocialLoginRow(
    navController: NavHostController,
    phoneloginVM: PhoneLoginViewModel,
    googleloginVM: GoogleLoginViewModel,
    facebookloginVM: FacebookLoginViewModel,
    githubLoginVM: GithubLoginViewModel
) {
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        var showPhoneDialog by remember { mutableStateOf(false) } //Variable para mostrar el dialogo de LoginPhone

        SocialButton(icon = R.drawable.phone, onClick = {
            showPhoneDialog = true
        })

        GoogleSocialButton(
            icon = R.drawable.google,
            onClick = GoogleSignInLauncher(context, navController, googleloginVM)
        )

        FacebookSocialButton(icon = R.drawable.facebook, navController, facebookloginVM)

        GitHubSocialButton(icon = R.drawable.github, navController, githubLoginVM)

        if (showPhoneDialog) {

            LaunchedEffect(Unit) {
                phoneloginVM.resetPhone()
            }

            PhoneAuthDialog(
                phoneloginVM = phoneloginVM,
                onDismiss = { showPhoneDialog = false; phoneloginVM.resetPhone()},
                onSuccess = {
                    showPhoneDialog = false
                    phoneloginVM.resetPhone()
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun RowScope.SocialButton(
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Green40,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}