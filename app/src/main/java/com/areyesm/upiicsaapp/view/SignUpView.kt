package com.areyesm.upiicsaapp.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.components.CustomDivider
import com.areyesm.upiicsaapp.components.DefaultButton
import com.areyesm.upiicsaapp.components.DefaultTextField
import com.areyesm.upiicsaapp.components.customShadow
import com.areyesm.upiicsaapp.navigation.AppScreen
import com.areyesm.upiicsaapp.ui.theme.ColorBackground
import com.areyesm.upiicsaapp.ui.theme.ColorShadowPrimary
import com.areyesm.upiicsaapp.ui.theme.ColorSurface
import com.areyesm.upiicsaapp.viewModel.LoginViewModel

@Composable
fun SignUpView(
    navController: NavHostController,
    loginVM: LoginViewModel
)
     {
         var email by remember { mutableStateOf("") }
         var password by remember { mutableStateOf("") }
         var confirmpassword by remember { mutableStateOf("") }
         var context = LocalContext.current
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

                Back(navController)

                SignUpHeader()

                Spacer(modifier = Modifier.height(10.dp))

                DefaultTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo Institucional",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                DefaultTextField(
                    modifier = Modifier.padding(top = 5.dp).fillMaxWidth(),
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    icon = Icons.Default.Lock,
                    hideText = true
                )
                DefaultTextField(
                    modifier = Modifier.padding(top = 5.dp).fillMaxWidth(),
                    value = confirmpassword,
                    onValueChange = { confirmpassword = it },
                    label = "Confirmar Contraseña",
                    icon = Icons.Default.CheckCircle,
                    hideText = true
                )
                DefaultButton(
                    text = "Registrate",
                    onClick = {
                        loginVM.registerUser(
                            email = email,
                            password = password,
                            confirmPassword = confirmpassword,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "¡Registro Exitoso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate(AppScreen.Login.route)
                            },
                            onError = { errorMessage ->
                                Toast.makeText(
                                    context,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                )

            }
        }
    }

@Composable
fun SignUpHeader() {
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
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Crea Tu Cuenta",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun Back(
    navController: NavHostController
) {
    Surface(
        onClick = { navController.popBackStack() },
        color = ColorSurface,
        shape = CircleShape,
        modifier = Modifier
            .size(48.dp)
            .customShadow(
                color = ColorShadowPrimary,
                alpha = 0.20f,
                shadowRadius = 16.dp,
                borderRadius = 48.dp,
                offsetY = 4.dp
            ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector =   Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = ""
            )
        }
    }
}