package com.areyesm.upiicsaapp.model

data class SubjectModel(
    val id: String = "",
    val unidad: String = "",
    val docente: String = "",
    val secuencia: String = "",
    val horarios: List<SubjectScheduleModel> = emptyList()
)




