package com.areyesm.upiicsaapp.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomDivider() {
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(16.dp))
}