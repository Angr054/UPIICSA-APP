package com.areyesm.upiicsaapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.areyesm.upiicsaapp.model.QueryModel
import com.areyesm.upiicsaapp.ui.theme.ColorBackground
import com.areyesm.upiicsaapp.ui.theme.gray100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryDialog(
    onDismiss: () -> Unit,
    query: String,
    results: List<QueryModel>,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit
) {
    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            onQueryChange(query)
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DefaultSearchTopBar(
                    value = query,
                    onValueChange = onQueryChange
                )
                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        LoadingState()
                    }
                    results.isEmpty() -> {
                        EmptyState()
                    }
                    else -> {
                        ResultsList(
                            results = results
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No hay resultados")
    }
}


