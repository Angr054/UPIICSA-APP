package com.areyesm.upiicsaapp.model

data class QueryModel(
    val id: String,
    val materia: String,
    val secuencia: String,
    val profesor: String,
    val edificio: String,
    val aula: String
) {
    val title: String get() = materia
    val description: String get() = "Secuencia: $secuencia | Materia: $materia | Profesor: $profesor | Edificio: $edificio | Aula: $aula"
}
