package com.areyesm.upiicsaapp.viewModel

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.areyesm.upiicsaapp.model.PhoneUserModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit

class PhoneLoginViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private var verificationId: String? = null

    var phoneAuthStep by mutableStateOf(PhoneUserModel.ENTER_PHONE)
        private set

    fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        onError: (String) -> Unit
    ) {
        val normalizedPhone = normalizePhone(phoneNumber)

        if (normalizedPhone == null) {
            onError("Número telefónico inválido. Usa 10 dígitos.")
            return
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(normalizedPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    auth.signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onError(e.localizedMessage ?: "Error al enviar código")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@PhoneLoginViewModel.verificationId = verificationId
                    phoneAuthStep = PhoneUserModel.ENTER_CODE
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    fun verifyCode(
        code: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = PhoneAuthProvider.getCredential(
            verificationId ?: return,
            code
        )

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError("El código no es correcto")
                }
            }
    }

    //Cierra sesión en UI
    fun resetPhone() {
        verificationId = null
        phoneAuthStep = PhoneUserModel.ENTER_PHONE
    }


    //Norma E.164 para Google
    private fun normalizePhone(phone: String): String? {
        val digits = phone.filter { it.isDigit() }

        return when {
            digits.length == 10 -> "+52$digits"

            digits.startsWith("52") && digits.length == 12 ->
                "+52${digits.substring(2)}"

            else -> null
        }
    }

}