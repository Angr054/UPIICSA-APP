package com.areyesm.upiicsaapp.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.areyesm.upiicsaapp.model.CampusModel
import com.areyesm.upiicsaapp.repository.CampusRepository

class CampusViewModel : ViewModel() {

    private val campusRepository = CampusRepository()

    var status by mutableStateOf(CampusModel.CERRADO)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        campusRepository.observeCampusStatus(
            onChange = { status = it },
            onError = { errorMessage = it }
        )
    }

    override fun onCleared() {
        super.onCleared()
        campusRepository.removeListener()
    }
}

