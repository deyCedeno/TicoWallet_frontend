package com.moviles.ticowallet.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.common.Constants
import com.moviles.ticowallet.ui.navigation.MenuItem
import com.moviles.ticowallet.ui.navigation.defaultMenuItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val menuItems: List<MenuItem> = defaultMenuItems,
    val selectedItemRoute: String = menuItems.firstOrNull()?.route ?: "",
    val userName: String = Constants.USERNAME
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onMenuItemSelect(selectedItem: MenuItem) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(selectedItemRoute = selectedItem.route)
            }
            println("ViewModel: Item seleccionado -> ${selectedItem.route}")
        }
    }
}