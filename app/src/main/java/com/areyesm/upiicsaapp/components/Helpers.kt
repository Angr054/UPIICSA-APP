package com.areyesm.upiicsaapp.components

import android.app.DatePickerDialog
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

//Extensión de Modifier, sombra personalizada
//Función estable (Sin cambios en su comportamiento)
@Stable
fun Modifier.customShadow(
    color: Color = Color.Black, //Color base
    alpha: Float = 0.5f,   //Opacidad 50%
    borderRadius: Dp = 0.dp,   //Radio de esquinas
    shadowRadius: Dp = 0.dp, //Difuminado de sombra
    offsetY: Dp = 0.dp,   //Desplazamiento vertical
    offsetX: Dp = 0.dp     //Desplazamiento vertical
) = drawBehind {  //Contenido a partir de aquí estará detrás del componente

    val shadowColor = color.copy(alpha = alpha).toArgb() //Color de la sombra
    val transparent = color.copy(alpha = 0f).toArgb() //Color transparente

    this.drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparent

        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}


@Composable
fun RowScope.FooterItem(
    @DrawableRes icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    //Resaltar icono seleccionado
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.10f else 0.8f,
        label = "IconScale"
    )

    val iconAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.35f,
        label = "IconAlpha"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .noRippleClickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .scale(iconScale)
                .alpha(iconAlpha),
            contentScale = ContentScale.Fit
        )
    }
}

//Función para quitar ripple del clicable
@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }

// Obtener Proveedor Utilizado
fun getLoginProviderLabel(): String {
    val user = FirebaseAuth.getInstance().currentUser

    val providerId = user
        ?.providerData
        ?.firstOrNull { it.providerId != "firebase" }
        ?.providerId

    return when (providerId) {
        "google.com", "facebook.com", "github.com", "phone" -> "Visitante"
        "password" -> "Bienvenido"
        else -> "Desconocido"
    }
}

@Composable
fun rememberDatePicker(
    initialDate: Long,
    onDateSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    calendar.timeInMillis = initialDate

    val dialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selected = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                onDateSelected(selected.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    LaunchedEffect(Unit) {
        dialog.show()
    }
}

fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "Sin fecha"

    val locale = Locale.getDefault()
    val date = Date(timestamp)

    val dayFormat = SimpleDateFormat("EEEE", locale)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", locale)

    val dayName = dayFormat.format(date)
    val fullDate = dateFormat.format(date)

    return "$dayName $fullDate"
}

