package com.areyesm.upiicsaapp.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.areyesm.upiicsaapp.model.ScheduleBlockUiModel
import com.areyesm.upiicsaapp.model.SubjectModel
import com.areyesm.upiicsaapp.repository.SubjectRepository
import com.areyesm.upiicsaapp.repository.UserScheduleRepository
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

class ScheduleViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val subjectRepository = SubjectRepository()

    var subjects by mutableStateOf<List<SubjectModel>>(emptyList())
        private set

    var selectedSubjects by mutableStateOf<List<SubjectModel>>(emptyList())
        private set

    var scheduleBlocks by mutableStateOf<List<ScheduleBlockUiModel>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val userScheduleRepository = UserScheduleRepository()

    private val colorPalette = listOf(
        0xFFFF9191, // Rojo
        0xFFFFBAEB, // Rosa
        0xFFF3BAFF, // Morado
        0xFFD3BAFF, // Indigo
        0xFFBACEFF, // Azul
        0xFFBAF5FF, // Light Blue
        0xFFFFF9BA, // Teal
        0xFFC0FFBA, // Verde
        0xFFFFDEBA  // Naranja
    )



    init {
        // 1. Carga inmediata si el usuario YA está logueado
        FirebaseAuth.getInstance().currentUser?.let {
            loadSubjects()
        }

        // 2. Escucha cambios de sesión (login / logout)
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                loadSubjects()
            } else {
                clearLocalState()
            }
        }
    }

    private fun clearLocalState() {
        subjects = emptyList()
        selectedSubjects = emptyList()
        scheduleBlocks = emptyList()
    }



    private fun loadSubjects() {
        subjectRepository.getSubjects(
            onSuccess = {
                subjects = it
                loadUserSelection()
            },
            onError = { errorMessage = it }
        )
    }

    private fun loadUserSelection() {
        userScheduleRepository.loadSelectedSubjects(
            onSuccess = { ids ->
                if (ids.isEmpty()) return@loadSelectedSubjects

                selectedSubjects = subjects.filter { it.id in ids }
                buildScheduleBlocks()
            },
            onError = {
                Log.w("Schedule", "No se pudo cargar selección: ${it.message}")
            }
        )
    }


    private fun generateColor(seed: String): Long {
        val index = kotlin.math.abs(seed.hashCode()) % colorPalette.size
        return colorPalette[index]
    }

    fun addSubject(subject: SubjectModel) {
        if (selectedSubjects.none { it.id == subject.id }) {
            selectedSubjects = selectedSubjects + subject
            buildScheduleBlocks()
        }
    }

    fun removeSubject(subject: SubjectModel) {
        selectedSubjects = selectedSubjects.filterNot { it.id == subject.id }
        buildScheduleBlocks()
    }

    private fun buildScheduleBlocks() {
        scheduleBlocks = selectedSubjects.flatMap { subject ->
            val color = generateColor(subject.unidad)

            subject.horarios.map { h ->
                ScheduleBlockUiModel(
                    subject = subject.unidad,
                    secuencia = subject.secuencia,
                    day = h.day,
                    startMinute = h.startMinute,
                    endMinute = h.endMinute,
                    salon = h.salon,
                    color = color
                )
            }
        }
    }

    fun saveSchedule() {
        if (selectedSubjects.isEmpty()) return

        userScheduleRepository.saveSelectedSubjects(
            subjectIds = selectedSubjects.map { it.id },
            onSuccess = { /* opcional */ },
            onError = { errorMessage = it.message }
        )
    }





}
