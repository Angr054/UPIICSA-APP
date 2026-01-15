package com.areyesm.upiicsaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.areyesm.upiicsaapp.model.ScheduleBlockUiModel
import com.areyesm.upiicsaapp.model.SubjectModel
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.ColorGradient4
import com.areyesm.upiicsaapp.ui.theme.Red30
import com.areyesm.upiicsaapp.ui.theme.Yellow40
import com.areyesm.upiicsaapp.ui.theme.gray100
import com.areyesm.upiicsaapp.utils.normalize
import com.areyesm.upiicsaapp.utils.searchIndex
import com.areyesm.upiicsaapp.viewModel.ScheduleViewModel
import com.areyesm.upiicsaapp.utils.abbreviateSubject

private const val HOURS_COLUMN_WIDTH = 56
private const val START_HOUR = 7
private const val END_HOUR = 22
private const val HOUR_HEIGHT = 38f
private const val MINUTE_HEIGHT = HOUR_HEIGHT / 60f
const val totalMinutes = (END_HOUR - START_HOUR) * 60
const val gridHeightDp = totalMinutes * MINUTE_HEIGHT

@Composable
fun Schedule(viewModel: ScheduleViewModel = viewModel()) {
    val schedules = viewModel.scheduleBlocks
    var showScheduleDialog by remember { mutableStateOf(false) }

    val hours = (START_HOUR until END_HOUR).map { "${it}:00" }
    val days = listOf("L", "M", "X", "J", "V")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        AddScheduleButton(
            onClick = {
                showScheduleDialog = true
            },
            loading = false
        )

        Column {

            // Encabezados
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(HOURS_COLUMN_WIDTH.dp))
                days.forEach {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Grid del horario
            Row(modifier = Modifier.fillMaxSize()) {
                // Horas
                Column(
                    modifier = Modifier.width(HOURS_COLUMN_WIDTH.dp)
                ) {
                    Column(
                        modifier = Modifier.height(gridHeightDp.dp)
                    ) {
                        for (hour in START_HOUR until END_HOUR) {
                            Box(
                                modifier = Modifier.height(HOUR_HEIGHT.dp),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Text("$hour:00", fontSize = 11.sp)
                            }
                        }
                    }
                    Text(
                        "22:00",
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }


                // Días
                days.forEachIndexed { dayIndex, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(gridHeightDp.dp)
                    ) {
                        schedules
                            .filter { it.day == dayIndex }
                            .forEach {
                                ScheduleBlock(schedule = it, gridHeightDp = gridHeightDp)
                            }
                    }
                }
            }


        }
        if (showScheduleDialog) {
            AddScheduleDialog(
                viewModel = viewModel,
                onDismiss = { showScheduleDialog = false }
            )
        }
    }
}

@Composable
fun AddScheduleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    loading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
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
                        imageVector = Icons.Default.Add,
                        contentDescription = "Horario",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    viewModel: ScheduleViewModel,
    onDismiss: () -> Unit
) {
    val subjects = viewModel.subjects
    var selectedSubject by remember { mutableStateOf<SubjectModel?>(null) }
    var showSubjectDialog by remember { mutableStateOf(false) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = gray100
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Prepara Tu Horario",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                /* -------- Materia -------- */
                AddSubjectButton(
                    onClick = {
                        showSubjectDialog = true
                    },
                    loading = false
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                    items(viewModel.selectedSubjects) { subject ->
                        SubjectScheduleItem(
                            subject = subject,
                            onRemove = {
                                viewModel.removeSubject(subject)
                            }
                        )
                    }
                }


                /* -------- Guardar -------- */
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.selectedSubjects.isNotEmpty(),
                    onClick = {
                        viewModel.saveSchedule()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Yellow40,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Guardar")
                }


            }
        }
    }
    if (showSubjectDialog) {
        AddSubjectDialog(
            subjects = viewModel.subjects,
            onSelect = { subject ->
                viewModel.addSubject(subject)
            },
            onDismiss = { showSubjectDialog = false }
        )
    }
}

@Composable
fun ScheduleBlock(
    schedule: ScheduleBlockUiModel,
    gridHeightDp: Float
) {
    val totalMinutes = (END_HOUR - START_HOUR) * 60

    val startMinutes = schedule.startMinute - (START_HOUR * 60)
    val durationMinutes = schedule.endMinute - schedule.startMinute

    val offsetDp = (startMinutes.toFloat() / totalMinutes) * gridHeightDp
    val heightDp = (durationMinutes.toFloat() / totalMinutes) * gridHeightDp

    Box(
        modifier = Modifier
            .offset(y = offsetDp.dp)
            .height(heightDp.dp)
            .fillMaxWidth()
            .background(
                Color(schedule.color),
                RoundedCornerShape(6.dp)
            )
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = abbreviateSubject(schedule.subject),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(schedule.secuencia, fontSize = 8.sp, color = Color.Black)
            Text(schedule.salon, fontSize = 8.sp, color = Color.Black)
        }
    }
}

@Composable
fun AddSubjectButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    loading: Boolean
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = modifier.size(56.dp),
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
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar materia",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubjectDialog(
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
            tonalElevation = 6.dp
        )
        {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            )
            {
                DefaultSearchTopBar(
                    value = query,
                    onValueChange = { query = it }
                )

                Spacer(Modifier.height(16.dp))
                when {
                    query.isBlank() -> EmptySearchHint()
                    filtered.isEmpty() -> EmptyState()
                    else ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp), // 👈 limita el alto del dialog
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filtered) { subject ->
                                SubjectSearchScheduleItem(
                                    subject = subject,
                                    onSelect = {
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
}


@Composable
fun SubjectScheduleItem(
    subject: SubjectModel,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ColorGradient3,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        onClick = onRemove
    ) {
        Box() {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row {
                        Text(subject.secuencia, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(8.dp))
                        Text(subject.unidad, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(8.dp))
                    }
                    Row {
                        Text(
                            text = subject.docente,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectSearchScheduleItem(
    subject: SubjectModel,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ColorGradient3,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        onClick = onSelect
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
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


