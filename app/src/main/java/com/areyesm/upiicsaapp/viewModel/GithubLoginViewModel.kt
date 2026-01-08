package com.areyesm.upiicsaapp.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class GithubLoginViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signInWithGitHub(
        activity: Activity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val provider = OAuthProvider.newBuilder("github.com")
            // Permisos adicionales
            .setScopes(listOf("user:email"))
            .build()

        auth
            .startActivityForSignInWithProvider(activity, provider)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Error al iniciar sesión con GitHub")
            }
    }

    fun signOut() {
        auth.signOut()
    }
}