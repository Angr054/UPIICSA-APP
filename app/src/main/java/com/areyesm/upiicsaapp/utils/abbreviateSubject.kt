package com.areyesm.upiicsaapp.utils

private val STOP_WORDS = setOf(
    "de", "la", "el", "y", "en", "para", "con", "del", "los", "las", "a"
)

fun abbreviateSubject(subject: String): String {
    return subject
        .trim()
        .split("\\s+".toRegex())
        .filter { it.isNotBlank() }
        .filterNot { it.lowercase() in STOP_WORDS }
        .joinToString("") { it.first().uppercaseChar().toString() }
}
