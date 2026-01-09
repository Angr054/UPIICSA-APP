package com.areyesm.upiicsaapp.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.model.TaskModel
import com.areyesm.upiicsaapp.ui.imageuri.createImageUri
import com.areyesm.upiicsaapp.ui.theme.BackpackItemColor
import com.areyesm.upiicsaapp.ui.theme.ColorGradient1
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.ColorShadowSecondary
import com.areyesm.upiicsaapp.ui.theme.Green40
import com.areyesm.upiicsaapp.ui.theme.Yellow40
import com.areyesm.upiicsaapp.ui.theme.gray100
import com.areyesm.upiicsaapp.viewModel.BackpackViewModel
import com.areyesm.upiicsaapp.viewModel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Backpack(
    viewModel: BackpackViewModel = viewModel()
) {

    var selectedImage by remember { mutableStateOf<String?>(null) }

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadImage(it) }
    }

    // Cámara
    val context = LocalContext.current
    val photoUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri.value?.let { viewModel.uploadImage(it) }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            photoUri.value = uri
            cameraLauncher.launch(uri)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        BackpackImagePin(
            pinnedImage = viewModel.pinnedImage,
            onClick = {
                selectedImage = viewModel.pinnedImage
            }
        )

        UploadImages(
            onPickFromGallery = {
                galleryLauncher.launch("image/*")
            },
            onTakePhoto = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    val uri = createImageUri(context)
                    photoUri.value = uri
                    cameraLauncher.launch(uri)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
            ,
            loading = viewModel.isUploading
        )

        BackpackGallery(
            urls = viewModel.images,
            onImageClick = { url ->
                selectedImage = url
            }
        )

        selectedImage?.let { url ->
            FullScreenImage(
                imageUrl = url,
                isDeleting = viewModel.isDeleting,
                isPinned = viewModel.pinnedImage,
                onDismiss = { selectedImage = null },
                onDelete = {
                    viewModel.deleteImage(url)
                    selectedImage = null
                },
                onPin = {
                    viewModel.pinImage(url)
                    selectedImage = null
                },
                onUnpin = {
                    viewModel.unpinImage()
                    selectedImage = null
                }
            )
        }

        viewModel.errorMessage?.let {
            Text(text = it, color = Color.Red)
        }

    }
}

@Composable
fun BackpackImagePin(
    pinnedImage: String?,
    onClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(
                enabled = pinnedImage != null
            ) {
                pinnedImage?.let { onClick(it) }
            },
        shape = RoundedCornerShape(16.dp),
        color = Green40,
        tonalElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (pinnedImage == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.pin_file),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Documento Anclado",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                } else {
                    AsyncImage(
                        model = pinnedImage,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onClick(pinnedImage) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
fun UploadImageButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    loading: Boolean
) {
    Surface(
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        color = Yellow40,
        onClick = onClick,
        enabled = !loading
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.outline_image_search_24),
                    contentDescription = "Seleccionar imagen",
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Composable
fun UploadTaskButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    loading: Boolean
) {
    Surface(
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        color = Yellow40,
        onClick = onClick,
        enabled = !loading
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.outline_checklist_rtl_24),
                    contentDescription = "Seleccionar imagen",
                    tint = Color.Unspecified
                )
            }
        }
    }
}


@Composable
fun UploadImages(
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    loading: Boolean,
    taskViewModel: TaskViewModel = viewModel()
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var showTaskDialog by remember { mutableStateOf(false) }


        UploadImageButton(
            onClick = onPickFromGallery,
            loading = loading
        )

        Spacer(modifier = Modifier.width(16.dp))

        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = Yellow40,
            onClick = onTakePhoto,
            enabled = !loading
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.outline_photo_camera_24),
                        contentDescription = "Tomar foto",
                        tint = Color.Unspecified
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        UploadTaskButton(
            onClick = { showTaskDialog = true },
            loading = loading
        )

        if (showTaskDialog) {
            TaskDialog(
                onDismiss = { showTaskDialog = false },
                viewModel = taskViewModel
            )
        }
    }
}


@Composable
fun BackpackGallery(
    urls: List<String>,
    onImageClick: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (urls.isEmpty()) {
            BackpackImagePlaceHolder()
        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            ) {
                items(urls) { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onImageClick(url)
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
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


@Composable
fun BackpackImagePlaceHolder() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
        ) {
            items(9) {
                PlaceholderItem()
            }
        }
    }
}

@Composable
fun PlaceholderItem() {
    Surface(
        modifier = Modifier
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        color = BackpackItemColor
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_image_24),
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit
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
                Text(
                    text = "Eliminar Imagen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text("¿Deseas eliminar esta imagen? ${(FirebaseAuth.getInstance().currentUser?.email) ?: "Visitante"}") //Operador Elvis, si es nulo entonces muestra la cadena visitante

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorGradient1)
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    onDismiss: () -> Unit,
    viewModel: TaskViewModel
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = gray100,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Mis Tareas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                DefaultTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = { title = it },
                    label = "Título",
                    icon = Icons.Default.Create
                )

                DefaultTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = description,
                    onValueChange = { description = it },
                    label = "Descripción",
                    icon = Icons.Default.Info
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorGradient1)
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    TextButton(
                        onClick = {
                            viewModel.saveTask(title, description)
                            title = ""
                            description = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Green40)
                    ) {
                        Text("Guardar")
                    }
                }

                // Lista de tareas
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.tasks) { task ->
                        TaskItemDialog(
                            task = task,
                            onEdit = {
                                title = task.title
                                description = task.description
                                viewModel.selectTask(task)
                            },
                            onDelete = {
                                viewModel.deleteTask(task.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItemDialog(
    task: TaskModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(task.title, fontWeight = FontWeight.Bold)
            Text(task.description, fontSize = 12.sp)

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onEdit) {
                    Text("Editar")
                }
                TextButton(onClick = onDelete) {
                    Text("Eliminar", color = Color.Red)
                }
            }
        }
    }
}





