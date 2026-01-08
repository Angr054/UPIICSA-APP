package com.areyesm.upiicsaapp.viewModel

import androidx.lifecycle.ViewModel
import com.areyesm.upiicsaapp.model.QueryModel
import com.areyesm.upiicsaapp.repository.QueryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QueryViewModel(
    private val repository: QueryRepository = QueryRepository()
) : ViewModel() {

    private val _results = MutableStateFlow<List<QueryModel>>(emptyList())
    val results: StateFlow<List<QueryModel>> = _results

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onQueryChange(query: String) {
        /*if (query.isBlank()) {
            _results.value = emptyList()
            return
        }*/
        _isLoading.value = true
        repository.searchQuery(query,
            onResult = { list ->
                _results.value = list
                _isLoading.value = false
            },
            onError = {
                _results.value = emptyList()
                _isLoading.value = false
            }
        )
    }
}
