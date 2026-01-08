package com.areyesm.upiicsaapp.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class FacebookLoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginSuccess = mutableStateOf(false)
    val loginSuccess = _loginSuccess

    fun registerFacebookCallback(callbackManager: CallbackManager) {
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(
                        result.accessToken.token
                    )

                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                _loginSuccess.value = true
                            }
                        }
                }

                override fun onCancel() {}

                override fun onError(error: FacebookException) {}
            }
        )
    }

    fun consumeLoginSuccess() {
        _loginSuccess.value = false
    }

    fun signOut() {
        auth.signOut()
        LoginManager.getInstance().logOut()
    }
}




