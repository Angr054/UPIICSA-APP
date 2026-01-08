package com.areyesm.upiicsaapp.model

data class UserModel(
    val userId: String,
    val email: String) {

    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "userId" to this.userId,
            "email" to this.email
        )
    }
}