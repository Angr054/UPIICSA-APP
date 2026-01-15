package com.areyesm.upiicsaapp.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.model.DocumentModel
import com.areyesm.upiicsaapp.model.DocumentType
import com.areyesm.upiicsaapp.ui.theme.ColorGradient1
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.Yellow40
import com.areyesm.upiicsaapp.ui.theme.gray100
import com.areyesm.upiicsaapp.viewModel.DocumentViewModel

@Composable
fun Documents(
    viewModel: DocumentViewModel = viewModel()
) {
    val context = LocalContext.current

    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    var showNameDialog by remember { mutableStateOf(false) }

    var documentToOpen by remember { mutableStateOf<DocumentModel?>(null) }
    var documentToDelete by remember { mutableStateOf<DocumentModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val documentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            pendingUri = it
            showNameDialog = true
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        UploadDocumentButton(
            onClick = { documentPicker.launch("*/*") },
            loading = viewModel.isUploading
        )

        DocumentList(
            documents = viewModel.documents,
            onItemClick = { documentToOpen = it },
            onDelete = {
                documentToDelete = it
                showDeleteDialog = true
            }
        )

        viewModel.errorMessage?.let {
            Text(text = it, color = Color.Red)
        }
    }

    documentToOpen?.let { document ->
        openDocument(context, document.fileUrl)
        documentToOpen = null
    }

    if (showNameDialog && pendingUri != null) {
        AddDocumentDialog(
            onConfirm = { name ->
                viewModel.uploadDocument(pendingUri!!, name)
                pendingUri = null
                showNameDialog = false
            },
            onDismiss = {
                pendingUri = null
                showNameDialog = false
            }
        )
    }

    if (showDeleteDialog && documentToDelete != null) {
        DocDeleteDialog(
            onDismiss = {
                showDeleteDialog = false
                documentToDelete = null
            },
            onDelete = {
                viewModel.deleteDocument(documentToDelete!!)
                showDeleteDialog = false
                documentToDelete = null
            }
        )
    }
}

@Composable
fun DocumentList(
    documents: List<DocumentModel>,
    onItemClick: (DocumentModel) -> Unit,
    onDelete: (DocumentModel) -> Unit
) {
    if (documents.isEmpty()) {
        DocumentsPlaceHolder()
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
        ) {
            items(documents) { document ->
                DocumentGridItem(
                    document = document,
                    onClick = { onItemClick(document) },
                    onDelete = { onDelete(document) }
                )
            }
        }
    }
}

@Composable
fun DocumentGridItem(
    document: DocumentModel,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val iconRes = documentTypeToIcon(document.type)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Unspecified,
        tonalElevation = 2.dp,
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Unspecified
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = document.name,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )


            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.TopEnd)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar documento",
                    tint = Color.Red
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = gray100) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Título de Documento",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                DefaultTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    label = "Título",
                    icon = Icons.Default.Star,
                    keyboardType = KeyboardType.Text
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorGradient3)
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(
                        enabled = name.isNotBlank(),
                        onClick = { onConfirm(name) },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorGradient1)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocDeleteDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = gray100) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Eliminar Documento",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text("¿Deseas eliminar este documento?")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}

@DrawableRes
fun documentTypeToIcon(type: String): Int =
    when (DocumentType.valueOf(type)) {
        DocumentType.PDF -> R.drawable.pdf
        DocumentType.WORD -> R.drawable.doc
        DocumentType.EXCEL -> R.drawable.xls
        DocumentType.IMAGE -> R.drawable.img
        DocumentType.OTHER -> R.drawable.file
    }

fun openDocument(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "No hay una aplicación para abrir este documento",
            Toast.LENGTH_LONG
        ).show()
    }
}


@Composable
fun UploadDocumentButton(
    onClick: () -> Unit,
    loading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .size(56.dp),
            shape = CircleShape,
            color = Yellow40,
            onClick = onClick,
            enabled = !loading,
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.outline_attach_file_add_24),
                        contentDescription = "Seleccionar documento",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}


@Composable
fun DocumentsPlaceHolder() {
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
            items(6) {
                PlaceholderItem("Document")
            }
        }
    }
}