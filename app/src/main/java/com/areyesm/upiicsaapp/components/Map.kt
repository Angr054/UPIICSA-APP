package com.areyesm.upiicsaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.model.MapPoint

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun Map(
    modifier: Modifier = Modifier
) {

    val minScale = 1f
    val maxScale = 4f
    val initialScale = 2.5f

    var scale by remember { mutableStateOf(initialScale ) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val points = listOf(
        //Desarrollo Profesional
        MapPoint(0.72f, -0.25f, R.drawable.pesados),
        MapPoint(0.22f, 0.84f, R.drawable.basicas),
        MapPoint(0.19f, 1.17f, R.drawable.graduados),
        MapPoint(0.68f, 0f, R.drawable.ingenierias),
        MapPoint(-0.13f, 0.43f, R.drawable.sociales),
        MapPoint(-0.13f, 0.84f, R.drawable.ligeros),

        //Deportes
        MapPoint(-0.13f, -0.2f, R.drawable.gym),

        //Cultura
        MapPoint(-0.13f, -0.12f, R.drawable.folklor),

        //Administración
        MapPoint(0.34f, 0.55f, R.drawable.gobierno),

        //Puntos de interés
        MapPoint(0.34f, 0.22f, R.drawable.culturales),
        MapPoint(0.22f, 0.15f, R.drawable.biblioteca),
        MapPoint(0.32f, 0.12f, R.drawable.cafeteria),
    )

    BoxWithConstraints(
        modifier = modifier
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(minScale, maxScale)

                    val w = size.width
                    val h = size.height

                    val maxX = ((w * newScale - w) / 2f).coerceAtLeast(0f)
                    val maxY = ((h * newScale - h) / 2f).coerceAtLeast(0f)

                    offset = Offset(
                        (offset.x + pan.x).coerceIn(-maxX, maxX),
                        (offset.y + pan.y).coerceIn(-maxY, maxY)
                    )

                    scale = newScale
                }
            }
    ) {
        Image(
            painter = painterResource(R.drawable.mapa_upiicsa_app_beta),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
        )
        points.forEach { point ->
            //Centro del contenedor
            val centerX = constraints.maxWidth / 2f
            val centerY = constraints.maxHeight / 2f

            //Conversión a coordenadas relativas
            val baseX = point.x * constraints.maxWidth
            val baseY = point.y * constraints.maxHeight

            val relativeX = baseX - centerX
            val relativeY = baseY - centerY

            //Corrección de movimiento
            val damping = 0.45f
            val effectiveScale = 1f + (scale - 2f) * damping

            val iconX = centerX + relativeX * effectiveScale + offset.x
            val iconY = centerY + relativeY * effectiveScale + offset.y

            //Modificador de tamaño
            val iconDamping = 1f
            val baseIconSize = 40.dp

            val zoomFactor = scale / initialScale
            val effectiveIconScale =
                (1f + (zoomFactor - 0.6f) * iconDamping)
                    .coerceIn(0.8f, 3.0f)


            IconButton(
                onClick = { },
                modifier = Modifier
                    .offset {
                        IntOffset(
                            iconX.toInt(),
                            iconY.toInt()
                        )
                    }
                    .size((baseIconSize * effectiveIconScale)+3.dp)
            ) {
                Icon(
                    painter = painterResource(point.icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(baseIconSize * effectiveIconScale).shadow(6.dp, CircleShape)
                )
            }
        }

    }
}