package com.areyesm.upiicsaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.model.BackpackMenuItem
import com.areyesm.upiicsaapp.ui.theme.ColorShadowSecondary
import com.areyesm.upiicsaapp.viewModel.BackpackViewModel

@Composable
fun Backpack(
    viewModel: BackpackViewModel = viewModel()
) {

    var selectedItem by remember {
        mutableStateOf<BackpackMenuItem?>(null)
    }

    var selectedImage by remember { mutableStateOf<String?>(null) }

    val backpackMenuItems = listOf(
        BackpackMenuItem("Horario", R.drawable.calendar, "Schedule"),
        BackpackMenuItem("Tareas", R.drawable.tasklist, "Tasks"),
        BackpackMenuItem("Galería", R.drawable.picture, "Gallery"),
        BackpackMenuItem("Documentos", R.drawable.folders, "Documents")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BackpackImagePin(
            pinnedImage = viewModel.pinnedImage,
            onClick = {
                selectedImage = viewModel.pinnedImage
            }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalArrangement = Arrangement.Center
        )
        {
            items(backpackMenuItems) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                selectedItem = item
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = item.title,
                            tint = Color.Unspecified,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    selectedItem?.let { item ->
        BackpackDialog(
            select = item.select,
            onDismiss = { selectedItem = null }
        )
    }

    selectedImage?.let { url ->
        FullScreenImage(
            imageUrl = url,
            isDeleting = viewModel.isDeleting,
            isPinned = viewModel.pinnedImage,
            onDismiss = { selectedImage = null },
            onDelete = {},
            onPin = {},
            onUnpin = {
                viewModel.unpinImage()
                selectedImage = null
            }
        )
    }
}


@Composable
fun FullScreenImage(
    imageUrl: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onPin: () -> Unit,
    onUnpin: () -> Unit,
    isDeleting: Boolean,
    isPinned: String?
) {

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val minScale = 1f
    val maxScale = 5f

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(minScale, maxScale)

        if (scale > 1f) {
            offset += panChange
        } else {
            offset = Offset.Zero
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val isCurrentImagePinned = imageUrl == isPinned


    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { onDismiss() }
                .transformable(transformState)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale > 1f) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scale = 2.5f
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentScale = ContentScale.Fit,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
            )
            // Botón cerrar
            Icon(
                painter = painterResource(R.drawable.baseline_close_24),
                contentDescription = "Cerrar",
                tint = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onDismiss() }
            )
            // Acciones
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isCurrentImagePinned) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = ColorShadowSecondary,
                        onClick = onPin
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_push_pin_24),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fijar", color = Color.White)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Red,
                        onClick = { showDeleteDialog = true },
                        enabled = !isDeleting
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isDeleting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_delete_24),
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Eliminar", color = Color.White)
                            }
                            if (showDeleteDialog) {
                                DeleteDialog(
                                    onDismiss = { showDeleteDialog = false },
                                    onDelete = {
                                        showDeleteDialog = false
                                        onDelete()
                                    }
                                )
                            }

                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Gray,
                        onClick = onUnpin
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_push_pin_24),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Desfijar", color = Color.White)
                        }
                    }
                }

            }
        }
    }
}





