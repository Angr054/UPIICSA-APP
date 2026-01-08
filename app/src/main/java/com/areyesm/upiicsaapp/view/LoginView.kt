package com.areyesm.upiicsaapp.view

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.components.CustomDivider
import com.areyesm.upiicsaapp.components.DefaultButton
import com.areyesm.upiicsaapp.components.DefaultTextField
import com.areyesm.upiicsaapp.components.SocialLoginRow
import com.areyesm.upiicsaapp.navigation.AppScreen
import com.areyesm.upiicsaapp.ui.auth.getGoogleSignInClient
import com.areyesm.upiicsaapp.ui.theme.ColorBackground
import com.areyesm.upiicsaapp.ui.theme.ColorGradient1
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.Green40
import com.areyesm.upiicsaapp.ui.theme.gray100
import com.areyesm.upiicsaapp.viewModel.FacebookLoginViewModel
import com.areyesm.upiicsaapp.viewModel.GithubLoginViewModel
import com.areyesm.upiicsaapp.viewModel.GoogleLoginViewModel
import com.areyesm.upiicsaapp.viewModel.LoginViewModel
import com.areyesm.upiicsaapp.viewModel.PhoneLoginViewModel

@Composable
fun LoginView(
    navController: NavHostController,
    loginVM: LoginViewModel,
    phoneLoginVM: PhoneLoginViewModel,
    googleloginVM: GoogleLoginViewModel,
    facebookLoginVM: FacebookLoginViewModel,
    githubLoginVM: GithubLoginViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorBackground
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            LoginHeader()

            Spacer(modifier = Modifier.height(10.dp))

            CustomDivider()

            //Email
            DefaultTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = {
                    email = it
                    loginVM.loginErrorMessage = null //Limpia el mensaje
                },
                label = "Correo Institucional",
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            //Contraseña
            DefaultTextField(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth(),
                value = password,
                onValueChange = {
                    password = it
                    loginVM.loginErrorMessage = null //Limpia el mensaje
                },
                label = "Contraseña",
                icon = Icons.Default.Lock,
                hideText = true
            )

            //Error de login
            loginVM.loginErrorMessage?.let { message ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            ForgotPasswordText(loginVM)

            Spacer(modifier = Modifier.height(15.dp))

            DefaultButton(text = "Iniciar Sesión", onClick = {
                loginVM.login(email, password){
                    navController.navigate(AppScreen.Home.route) {
                        //Limpiar BackStack (Para controlar el flujo de sesión)
                        popUpTo(AppScreen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            })

            RegisterText(navController)

            Spacer(modifier = Modifier.height(8.dp))

            CustomDivider()

            Spacer(modifier = Modifier.height(8.dp))

            Visitant()

            SocialLoginRow(navController, phoneLoginVM, googleloginVM, facebookLoginVM, githubLoginVM)

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}

@Composable
fun LoginHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Image(
            modifier = Modifier.size(150.dp),
            painter = painterResource(R.drawable.escudoupiicsa),
            contentDescription = null
        )
        Image(
            painter = painterResource(R.drawable.upiicsaapp),
            contentDescription = null
        )
    }
}

@Composable
fun ForgotPasswordText(
    loginVM: LoginViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Text(
        text = "¿Olvidaste Tu Contraseña?",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        textDecoration = TextDecoration.Underline,
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF449A8B)
    )

    if (showDialog) {
        ResetPasswordDialog(
            onDismiss = { showDialog = false },
            onSend = { email ->
                loginVM.resetPassword(
                    email = email,
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Correo De Recuperación Enviado.",
                            Toast.LENGTH_LONG
                        ).show()
                        showDialog = false
                    },
                    onError = {
                        Toast.makeText(
                            context,
                            "Ocurrió un error. Intenta más tarde",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )

            }
        )
    }
}


@Composable
fun Visitant() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Acceso Como Visitante",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun RegisterText(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¿No Tienes Cuenta UPIICSA APP? ",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            modifier = Modifier.clickable {
                navController.navigate(route = AppScreen.Signup.route) {
                    launchSingleTop = true //Evita crear distintas instancias de SignUpView
                }
            },
            text = "Registrate",
            color = Color(0xFF449A8B),
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordDialog(
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ){
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = gray100,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Restablece tu contraseña",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                DefaultTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo Institucional",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ColorGradient3)) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    TextButton(onClick = { onSend(email) }, colors = ButtonDefaults.buttonColors(containerColor = ColorGradient1)) {
                        Text("Enviar")
                    }
                }

            }

        }
    }
}




