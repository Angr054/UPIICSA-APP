package com.areyesm.upiicsaapp.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StatusRepository {

    private val db = FirebaseDatabase.getInstance()
    private val statusRef = db.getReference("status")

    fun observeStatus(onChange: (Boolean) -> Unit) {
        statusRef.child("isOpen")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isOpen = snapshot.getValue(Boolean::class.java) ?: false
                    onChange(isOpen)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
