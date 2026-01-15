package com.areyesm.upiicsaapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.areyesm.upiicsaapp.ui.theme.ColorShadowPrimary
import com.areyesm.upiicsaapp.ui.theme.ColorSurface
import com.areyesm.upiicsaapp.ui.theme.gray100
import com.areyesm.upiicsaapp.viewModel.BackpackViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackpackDialog(
    viewModel: BackpackViewModel = viewModel(),
    onDismiss: () -> Unit,
    select: String
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Back(onDismiss)
                when(select) {
                    "Schedule" -> Schedule()
                    "Tasks" -> Tasks()
                    "Gallery" -> Gallery()
                    "Documents" -> Documents()
                }
            }
        }
    }
}

@Composable
fun Back(
    onDismiss: () -> Unit
) {
    Surface(
        onClick = { onDismiss() },
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