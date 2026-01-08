package com.areyesm.upiicsaapp.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.model.QueryModel
import com.areyesm.upiicsaapp.navigation.AppScreen
import com.areyesm.upiicsaapp.ui.auth.getGoogleSignInClient
import com.areyesm.upiicsaapp.ui.theme.ColorGradient1
import com.areyesm.upiicsaapp.ui.theme.ColorGradient2
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.ColorShadowPrimary
import com.areyesm.upiicsaapp.ui.theme.ColorSurface
import com.areyesm.upiicsaapp.ui.theme.ColorTextSecondary
import com.areyesm.upiicsaapp.ui.theme.gray100
import com.areyesm.upiicsaapp.viewModel.LoginViewModel
import com.areyesm.upiicsaapp.viewModel.QueryViewModel
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

//Columna que almacena todos los elementos de la barra de acciones
@Composable
fun ActionBar(navController: NavController, loginVM: LoginViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SearchButton()
        TitleText()
        UserButton(navController, loginVM)
    }
}

//Botón de busqueda
@Composable
private fun SearchButton(
    viewModel: QueryViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    Surface(
        onClick = {
            showDialog = true
        },
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
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_manage_search_24),
                contentDescription = "Search",
                modifier = Modifier.size(20.dp)
            )
        }
    }
    if (showDialog) {
        val results by viewModel.results.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        QueryDialog(
            onDismiss = { showDialog = false },
            query = query,
            results = results,
            isLoading = isLoading,
            onQueryChange = {
                query = it
                viewModel.onQueryChange(it)
            }
        )
    }
}

//Botón de perfil
@Composable
private fun UserButton(
    navController: NavController,
    loginVM: LoginViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    Surface(
        onClick = { showDialog = true },
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
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_person_raised_hand_24),
                contentDescription = "User Button",
                modifier = Modifier.size(20.dp)
            )
        }
    }

    //PROCEDIMIENTO DE PRUEBA GOOGLE SIGN OUT
    val context = LocalContext.current
    val googleClient = getGoogleSignInClient(context)
    fun signOutGoogle(context: Context, onComplete: () -> Unit) {
        val googleClient = getGoogleSignInClient(context)

        // Firebase
        FirebaseAuth.getInstance().signOut()

        // Google
        googleClient.signOut().addOnCompleteListener{}

        // Facebook
        LoginManager.getInstance().logOut()

        onComplete()
    }

    if (showDialog) {
        UserDialog(
            onDismiss = { showDialog = false },
            onLogout = {
                showDialog = false
                loginVM.signOut()

                signOutGoogle(context) { //REMOVER POR PRUEBA DE GOOGLE
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }

            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDialog(
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
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
                    text = "Sesión Activa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text("¿Deseas cerrar la sesión? ${(FirebaseAuth.getInstance().currentUser?.email)?:"Visitante"}") //Operador Elvis, si es nulo entonces muestra la cadena visitante

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ColorGradient1)) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    TextButton(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = ColorGradient3)) {
                        Text("Cerrar sesión")
                    }
                }
            }
        }
    }
}



//Texto Superior (Título)
@Composable
private fun TitleText(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.escudoupiicsa),
                contentDescription = null,
                modifier = Modifier.height(30.dp),
                contentScale = ContentScale.FillHeight
            )
            Image(
                painter = painterResource(id = R.drawable.upiicsaapp),
                contentDescription = null,
                modifier = Modifier.height(20.dp),
                contentScale = ContentScale.FillHeight
            )
        }

        Bar(getLoginProviderLabel())
    }
}


//Barra de información
@Composable
private fun Bar(
    label: String
) {
    Box(
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    0f to ColorGradient1,
                    0.25f to ColorGradient2,
                    1f to ColorGradient3
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                vertical = 2.dp,
                horizontal = 10.dp
            )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ColorTextSecondary.copy(alpha = 0.7f)
        )
    }
}
