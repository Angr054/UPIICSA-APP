package com.areyesm.upiicsaapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.areyesm.upiicsaapp.repository.NewsRepository

class NewsViewModel : ViewModel() {

    private val repository = NewsRepository()

    var images by mutableStateOf<List<String>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadNews()
    }

    fun loadNews() {
        isLoading = true
        errorMessage = null

        repository.getNewsImages(
            onSuccess = {
                images = it
                isLoading = false
            },
            onError = {
                errorMessage = it
                isLoading = false
            }
        )
    }
}
