package com.areyesm.upiicsaapp.view

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.areyesm.upiicsaapp.components.DefaultTextField
import com.areyesm.upiicsaapp.model.PhoneUserModel
import com.areyesm.upiicsaapp.ui.theme.ColorGradient1
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.gray100
import com.areyesm.upiicsaapp.viewModel.LoginViewModel
import com.areyesm.upiicsaapp.viewModel.PhoneLoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneAuthDialog(
    phoneloginVM: PhoneLoginViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as Activity

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
                    text = "Acceder con telefono",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                when (phoneloginVM.phoneAuthStep) {
                    PhoneUserModel.ENTER_PHONE -> {
                        DefaultTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Número telefónico",
                            icon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone,
                            leadingText = "+52"
                        )
                    }

                    PhoneUserModel.ENTER_CODE -> {
                        DefaultTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = "Código SMS",
                            icon = Icons.Default.Lock,
                            keyboardType = KeyboardType.Number
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            when (phoneloginVM.phoneAuthStep) {

                                PhoneUserModel.ENTER_PHONE -> {
                                    phoneloginVM.sendVerificationCode(
                                        phoneNumber = phone,
                                        activity = activity,
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }

                                PhoneUserModel.ENTER_CODE -> {
                                    phoneloginVM.verifyCode(
                                        code = code,
                                        onSuccess = {
                                            onSuccess()
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorGradient1)
                    ) {
                        Text(
                            text = if (phoneloginVM.phoneAuthStep == PhoneUserModel.ENTER_PHONE)
                                "Enviar código"
                            else
                                "Verificar"
                        )
                    }

                }

            }

        }
    }
}
