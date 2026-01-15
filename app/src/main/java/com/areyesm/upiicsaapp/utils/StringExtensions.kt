package com.areyesm.upiicsaapp.utils
import com.areyesm.upiicsaapp.model.SubjectModel
import java.text.Normalizer

fun String.normalize(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase()

fun SubjectModel.searchIndex(): String {
    val horariosText = horarios.joinToString(" ") { h ->
        val dayName = listOf("lunes", "martes", "miercoles", "jueves", "viernes")[h.day]
        "$dayName ${h.startMinute} ${h.endMinute} ${h.salon}"
    }

    return """
        $unidad
        $docente
        $secuencia
        $horariosText
    """.normalize()
}
