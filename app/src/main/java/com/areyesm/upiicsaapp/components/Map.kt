package com.areyesm.upiicsaapp.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.model.CampusBounds
import com.areyesm.upiicsaapp.model.MapPoint
import com.areyesm.upiicsaapp.model.UPIICSA_BOUNDS
import com.areyesm.upiicsaapp.ui.theme.ColorGradient1
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.Green40
import com.areyesm.upiicsaapp.ui.theme.gray100
import com.areyesm.upiicsaapp.viewModel.LocationViewModel
import com.google.firebase.auth.FirebaseAuth

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun Map(
    modifier: Modifier = Modifier,
    locationVM: LocationViewModel
) {

    val context = LocalContext.current

    var selectedPoint by remember { mutableStateOf<MapPoint?>(null) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                locationVM.startLocationUpdates(
                    context = context,
                    campusBounds = UPIICSA_BOUNDS
                )
            }
        }

    LaunchedEffect(Unit) {
        val granted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            locationVM.startLocationUpdates(
                context = context,
                campusBounds = UPIICSA_BOUNDS
            )
        } else {
            permissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }


    val minScale = 1f
    val maxScale = 4f

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val userPoint = locationVM.userLocation


    val MAP_ASPECT_RATIO = 4f / 6f


    val staticPoints = listOf(
        MapPoint(0.15f, -0.52f, R.drawable.pesados, "Edificio de Competencias Integrales e Institucionales (Pesados)", "Jefatura\n" +
                "Academias de Procesos de Manufactura\n" +
                "Academia de Control de Calidad\n" +
                "Academia de Ingeniería de Métodos\n" +
                "Academia de Electricidad y Control\n" +
                "Academia de Sistemas Automotrices\n" +
                "Academia de Automatización y Robótica\n" +
                "Academia de Tecnologías Ferroviariarias"),
        MapPoint(-0.15f, 0.20f, R.drawable.basicas, "Edificio de Formación Básica", "Jefatura de Básicas\n" +
                "Academia de Física\n" +
                "Academia de Química\n" +
                "Academia de Matemáticas\n" +
                "Academia de Humanidades y Ciencias Sociales\n" +
                "Tutorías"),
        MapPoint(-0.2f, 0.45f, R.drawable.graduados,"Edificio de Post Grado (Graduados)","Inhabilitado"),
        MapPoint(0.12f, -0.3f, R.drawable.ingenierias,"Edificio de Desarrollo Profesional Específico (Ingeniería)","Jefatura\n" +
                "Academias de Ciencias Básicas de la Ingeniería\n" +
                "Academias de Producción\n" +
                "Academias de Informática\n" +
                "Academias de Computación\n" +
                "Academias de Ingeniería Industrial\n" +
                "Academias de Investigación de Operaciones\n" +
                "Academias de Sistemas de Transporte\n" +
                "Fomento a la Cultura"),
        MapPoint(-0.33f, -0.05f, R.drawable.sociales,"Edificio de Estudios Profesionales Genéricos (Sociales)","Jefatura\n" +
                "Academia de Administración\n" +
                "Academia de Derecho\n" +
                "Academia de Finanzas\n" +
                "Academia de Economía\n" +
                "Academia de Tecnología Informática\n" +
                "Academia de Mercadotecnia\n" +
                "Academia de Recursos Humanos\n" +
                "Coordinación de la Red de Género de la UPIICSA"),
        MapPoint(-0.33f, 0.22f, R.drawable.ligeros,"Laboratorios ligeros","Laboratorio de Física\n" +
                "Laboratorio de Química"),
        MapPoint(-0.33f, -0.44f, R.drawable.gym,"Gimnasio","Actividades Deportivas"),
        MapPoint(-0.30f, -0.38f, R.drawable.folklor,"Salón de Espejos","Actividades Culturales"),
        MapPoint(-0.12f, 0.03f, R.drawable.gobierno,"Edificio de Gobierno","Trámites"),
        MapPoint(-0.06f, -0.15f, R.drawable.culturales,"Edificio de Actividades Culturales","Auditorio A\n"+
        "Auditorio B"),
        MapPoint(-0.19f, -0.18f, R.drawable.biblioteca,"Biblioteca","Sala de Lectura\n" +
                "Computadores"),
        MapPoint(-0.09f, -0.2f, R.drawable.cafeteria,"Cafeteria","Olga's Food"),
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

        val containerWidth = constraints.maxWidth.toFloat()
        val containerHeight = constraints.maxHeight.toFloat()

        val mapWidth: Float
        val mapHeight: Float

        if (containerWidth / containerHeight > MAP_ASPECT_RATIO) {
            mapHeight = containerHeight
            mapWidth = mapHeight * MAP_ASPECT_RATIO
        } else {
            mapWidth = containerWidth
            mapHeight = mapWidth / MAP_ASPECT_RATIO
        }

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

        @Composable
        fun drawPoint(
            point: MapPoint,
            sizeDp: Float,
            onClick: () -> Unit
        ) {
            val centerX = containerWidth / 2f
            val centerY = containerHeight / 2f

            // bordes
            val px = point.x.coerceIn(-0.5f, 0.5f)
            val py = point.y.coerceIn(-0.5f, 0.5f)

            val x =
                centerX + (px * mapWidth) * scale + offset.x
            val y =
                centerY + (py * mapHeight) * scale + offset.y

            Icon(
                painter = painterResource(point.icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .offset { IntOffset(x.toInt(), y.toInt()) }
                    .size(sizeDp.dp)
                    .shadow(6.dp, CircleShape)
                    .clickable(
                        enabled = selectedPoint == null
                    ) {
                        selectedPoint = point
                    }
            )



        }


        staticPoints.forEach { point ->
            drawPoint(
                point = point,
                sizeDp = 40f,
                onClick = { }
            )
        }

        userPoint?.let { point ->
            drawPoint(
                point = point,
                sizeDp = 28f,
                onClick = {  }
            )
        }
    }
    selectedPoint?.let { point ->
        DetailIconDialog(
            onDismiss = { selectedPoint = null },
            title = point.title,
            icon = point.icon,
            detail = point.description
        )
    }
}


fun gpsToMapPoint(
    latitude: Double,
    longitude: Double,
    bounds: CampusBounds
): MapPoint {

    // Normalización
    val xNorm =
        (longitude - bounds.lngWest) /
                (bounds.lngEast - bounds.lngWest)

    val yNorm =
        (latitude - bounds.latSouth) /
                (bounds.latNorth - bounds.latSouth)

    // Sistema centrado
    val x = xNorm - 0.5
    val y = yNorm - 0.5

    // Rotación 90° antihoraria
    val rotatedX = -y
    val rotatedY = x

    // Corrección eje Y (pantalla)
    val uiY = -rotatedY

    return MapPoint(
        x = rotatedX.toFloat(),
        y = uiY.toFloat(),
        icon = R.drawable.ic_user_location,
        "Ubicación Actual",
        ""
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailIconDialog(
    onDismiss: () -> Unit,
    title: String,
    icon: Int,
    detail: String
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
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp)
                    )

                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Text(
                    text = detail,
                    fontSize = 11.sp
                )
            }
        }
    }
}

