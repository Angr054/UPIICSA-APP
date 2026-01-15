package com.areyesm.upiicsaapp.model

data class SubjectScheduleModel(
    val day: Int = 0,
    val startMinute: Int = 0,
    val endMinute: Int = 0,
    val salon: String = ""
)