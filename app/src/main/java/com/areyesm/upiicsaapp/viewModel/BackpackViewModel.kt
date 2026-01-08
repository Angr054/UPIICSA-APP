package com.areyesm.upiicsaapp.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.areyesm.upiicsaapp.repository.BackpackRepository
import com.areyesm.upiicsaapp.repository.UserRepository

class BackpackViewModel : ViewModel() {

    private val backpackRepository = BackpackRepository()
    private val userRepository = UserRepository()

    var images by mutableStateOf<List<String>>(emptyList())
        private set

    var pinnedImage by mutableStateOf<String?>(null)
        private set

    var isUploading by mutableStateOf(false)
        private set

    var isDeleting by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadImages()
        loadPinnedImage()
    }

    private fun loadImages() {
        backpackRepository.getImages(
            onSuccess = { images = it },
            onError = { errorMessage = it }
        )
    }

    private fun loadPinnedImage() {
        userRepository.getPinnedImage(
            onSuccess = { pinnedImage = it },
            onError = { errorMessage = it }
        )
    }

    fun pinImage(url: String) {
        userRepository.savePinnedImage(
            imageUrl = url,
            onSuccess = { pinnedImage = url },
            onError = { errorMessage = it }
        )
    }

    fun unpinImage() {
        userRepository.clearPinnedImage(
            onSuccess = { pinnedImage = null },
            onError = { errorMessage = it }
        )
    }

    fun uploadImage(uri: Uri) {
        isUploading = true
        backpackRepository.uploadImage(
            uri,
            onSuccess = {
                images = images + it
                isUploading = false
            },
            onError = {
                errorMessage = it
                isUploading = false
            }
        )
    }

    fun deleteImage(imageUrl: String) {
        isDeleting = true
        backpackRepository.deleteImage(
            imageUrl,
            onSuccess = {
                images = images.filterNot { it == imageUrl }
                if (pinnedImage == imageUrl) {
                    unpinImage()
                }
                isDeleting = false
            },
            onError = {
                errorMessage = it
                isDeleting = false
            }
        )
    }

    fun unPinImage() {
        pinnedImage = null
    }

}




