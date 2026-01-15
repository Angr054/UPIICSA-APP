package com.areyesm.upiicsaapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.areyesm.upiicsaapp.model.SubjectModel
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.ColorGradient4
import com.areyesm.upiicsaapp.ui.theme.gray100
import com.areyesm.upiicsaapp.utils.normalize
import com.areyesm.upiicsaapp.utils.searchIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectSearchDialog(
    subjects: List<SubjectModel>,
    onSelect: (SubjectModel) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filtered = remember(query, subjects) {
        if (query.isBlank()) emptyList()
        else {
            val tokens = query.normalize().split(" ").filter { it.isNotBlank() }
            subjects.filter { subject ->
                val index = subject.searchIndex()
                tokens.all { index.contains(it) }
            }
        }
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = gray100,
            tonalElevation = 6.dp)
        {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp))
            {
                DefaultSearchTopBar(
                    value = query,
                    onValueChange = { query = it }
                )

                Spacer(Modifier.height(16.dp))

                when {
                    query.isBlank() -> EmptySearchHint()
                    filtered.isEmpty() -> EmptyState()
                    else -> filtered.forEach { subject ->
                        SubjectSearchItem(
                            subject = subject,
                            onClick = {
                                onSelect(subject)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EmptySearchHint() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text("Busca tu Unidad De Aprendizaje por Nombre, Secuencia y/o Profesor.")
    }
}

@Composable
fun EmptyState() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text("No hay entradas encontradas para tu consulta actual.")
    }
}

@Composable
fun SubjectSearchItem(
    subject: SubjectModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ColorGradient3,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        onClick = {}
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row() {
                Text(
                    text = subject.secuencia,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = subject.unidad,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = subject.docente,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}



