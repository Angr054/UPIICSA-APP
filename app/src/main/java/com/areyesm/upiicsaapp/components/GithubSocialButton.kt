package com.areyesm.upiicsaapp.components

import android.app.Activity
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.areyesm.upiicsaapp.navigation.AppScreen
import com.areyesm.upiicsaapp.ui.theme.Green40
import com.areyesm.upiicsaapp.viewModel.GithubLoginViewModel

@Composable
fun RowScope.GitHubSocialButton(
    @DrawableRes icon: Int,
    navController: NavHostController,
    githubLoginVM: GithubLoginViewModel
) {
    val context = LocalContext.current
    val activity = context as Activity

    Surface(
        onClick = {
            githubLoginVM.signInWithGitHub(
                activity = activity,
                onSuccess = {
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            )
        },
        color = Green40,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(icon),
                contentDescription = "GitHub Login",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
