package com.areyesm.upiicsaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.areyesm.upiicsaapp.model.CampusModel
import com.areyesm.upiicsaapp.model.CampusUiState
import com.areyesm.upiicsaapp.ui.theme.ColorGradient1
import com.areyesm.upiicsaapp.ui.theme.ColorGradient2
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.ColorTextSecondary
import com.areyesm.upiicsaapp.viewModel.CampusViewModel

@Composable
fun CampusHeader(viewModel: CampusViewModel) {

    val status = viewModel.status

    CampusStatusSurface(status)
}

@Composable
fun CampusStatusSurface(status: CampusModel) {

    val (gradient1, gradient2, gradient3, text) = when (status) {
        CampusModel.ABIERTO -> CampusUiState(
            gradient1 = Color(0xFF78D37C),
            gradient2 = Color(0xFF7BB77D),
            gradient3 = Color(0xFFB8D7B9),
            text = "Unidad Abierta"
        )

        CampusModel.CERRADO -> CampusUiState(
            gradient1 = Color(0xFFFFBDBD),
            gradient2 = Color(0xFFEC847B),
            gradient3 = Color(0xFFEE6E69),
            text = "Unidad Cerrada"
        )

        CampusModel.SUSPENSION -> CampusUiState(
            gradient1 = Color(0xFFD9A160),
            gradient2 = Color(0xFFF1AA5C),
            gradient3 = Color(0xFFB79264),
            text = "Unidad Suspendida"
        )

        CampusModel.CARGANDO -> CampusUiState(
            gradient1 = Color(0xFF9F9F9F),
            gradient2 = Color(0xFF9B9793),
            gradient3 = Color(0xFF5B5B5B),
            text = "Cargando..."
        )
    }


    Box(
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    0f to gradient1,
                    0.25f to gradient2,
                    1f to gradient3
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                vertical = 2.dp,
                horizontal = 10.dp
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = ColorTextSecondary.copy(alpha = 0.7f)
        )
    }
}
