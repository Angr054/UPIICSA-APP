package com.areyesm.upiicsaapp.repository

import android.util.Log
import com.areyesm.upiicsaapp.model.CampusModel
import com.google.firebase.database.*

class CampusRepository {

    private val dbRef =
        FirebaseDatabase.getInstance("https://upiicsa-app-default-rtdb.firebaseio.com").getReference("campusStatus")


    private var listener: ValueEventListener? = null

    fun observeCampusStatus(
        onChange: (CampusModel) -> Unit,
        onError: (String) -> Unit
    ) {
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val value = snapshot.getValue(String::class.java)

                val status = runCatching {
                    CampusModel.valueOf(value ?: "CARGANDO")
                }.getOrElse {
                    CampusModel.CARGANDO
                }

                onChange(status)


            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        }

        dbRef.addValueEventListener(listener!!)
    }

    fun removeListener() {
        listener?.let { dbRef.removeEventListener(it) }
    }
}

