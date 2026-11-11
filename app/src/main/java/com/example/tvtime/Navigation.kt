package com.example.tvtime

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tvtime.data.repository.TvRepository
import com.example.tvtime.ui.main.MainScreen
import com.example.tvtime.ui.main.MainScreenViewModel

@Composable
fun MainNavigation() {
    val context = LocalContext.current.applicationContext
    val repository = remember { TvRepository(context) }
    val viewModel: MainScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainScreenViewModel(repository) as T
            }
        }
    )

    MainScreen(
        viewModel = viewModel,
        modifier = Modifier.fillMaxSize()
    )
}
