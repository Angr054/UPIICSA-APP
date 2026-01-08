package com.areyesm.upiicsaapp.components

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.areyesm.upiicsaapp.navigation.AppScreen
import com.areyesm.upiicsaapp.ui.auth.getGoogleSignInClient
import com.areyesm.upiicsaapp.ui.theme.Green40
import com.areyesm.upiicsaapp.viewModel.GoogleLoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun RowScope.GoogleSocialButton(
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
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(icon),
                contentDescription = "Google Login",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}


@Composable
fun GoogleSignInLauncher(
    context: Context,
    navController: NavHostController,
    googleLoginVM: GoogleLoginViewModel
): () -> Unit {

    val googleClient = remember { getGoogleSignInClient(context) }

    @Suppress("DEPRECATION") val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken!!

            googleLoginVM.firebaseAuthWithGoogle(
                idToken = idToken,
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
        } catch (_: ApiException) {}
    }

    return {
        launcher.launch(googleClient.signInIntent)
    }
}