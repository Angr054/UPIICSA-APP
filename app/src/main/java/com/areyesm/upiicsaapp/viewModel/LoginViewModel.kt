package com.areyesm.upiicsaapp.viewModel

import android.util.Log
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.areyesm.upiicsaapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    var showAlert by mutableStateOf(false)

    //variable para controlar errores
    var loginErrorMessage by mutableStateOf<String?>(null)


    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        //Normalizar correo para comprobación
        val normalizedEmail = email.trim().lowercase()

        if (normalizedEmail.isBlank() || password.isBlank()) {
            loginErrorMessage = "Por favor, complete los campos"
            return
        }

        auth.signInWithEmailAndPassword(normalizedEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginErrorMessage = null
                    onSuccess()
                } else {
                    val exception = task.exception

                    loginErrorMessage = when (exception) {
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
                            "Este correo no está registrado, ¡Crea una cuenta!"

                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                            "Correo y/o contraseña incorrecto"

                        else ->
                            "Error al iniciar sesión"
                    }
                }
            }
    }


    //Validcaciones
    fun registerUser(
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (password != confirmPassword) {
            onError("Las contraseñas no coinciden")
            return
        }

        if (!isInstitutionalEmail(email)) {
            onError("Por favor, ingrese un correo institucional válido")
            return
        }

        createUser(email, password, onSuccess, onError)
    }

    private fun isInstitutionalEmail(email: String): Boolean {
        val allowedDomains = listOf(
            "@ipn.mx",
            "@alumno.ipn.mx"
        )

        return allowedDomains.any { domain ->
            email.lowercase().endsWith(domain)
        }
    }


    private fun createUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        saveUser(email)
                        onSuccess()
                    } else {
                        onError(task.exception?.localizedMessage ?: "Error al crear usuario")
                    }
                }
        }
    }


    private fun saveUser(username: String) {
        val id = auth.currentUser?.uid
        val email = auth.currentUser?.email

        viewModelScope.launch(Dispatchers.IO) {
            val user = UserModel(
                userId = id.toString(),
                email = email.toString()
            )

            FirebaseFirestore.getInstance().collection("Users")
                .add(user)
                .addOnFailureListener {
                    Log.d("GUARDADO CORRECTAMENTE", "Guardo correctamente")
                }.addOnFailureListener {
                    Log.d("ERROR AL GUARDAR", "Error al guardar")
                }
        }
    }

    fun closeAlert() {
        showAlert = false
    }

    //Cerrar Sesión
    fun signOut() {
        auth.signOut()
    }

    //Recuperar Contraseña
    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        if (email.isBlank()) {
            onError()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError()
                }
            }
    }
}