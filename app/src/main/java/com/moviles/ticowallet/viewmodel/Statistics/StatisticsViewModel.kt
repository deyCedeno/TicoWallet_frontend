package com.moviles.ticowallet.viewmodel.Statistics

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.models.DashboardResponse
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    fun getDashboardStats(
        onSuccess: (DashboardResponse) -> Unit,
        onError: (String) -> Unit,
        onLoading: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                onLoading(true)
                val response = RetrofitInstance.api.getDashboardStats()
                Log.i("StatisticsViewModel", "Real dashboard data received: ${response.balancesByCurrency.size} currencies, ${response.topExpensesByCategory.size} categories")
                Log.i("StatisticsViewModel", "Data period: ${response.dataPeriod}")
                Log.i("StatisticsViewModel", "Last updated: ${response.lastUpdated}")
                onSuccess(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("StatisticsViewModel", "HTTP Error: ${e.message()}, Body: $errorBody")
                when (e.code()) {
                    401 -> onError("Authentication required")
                    404 -> onError("No data found")
                    500 -> onError("Server error")
                    else -> onError("Error loading statistics: ${e.code()}")
                }
            } catch (e: Exception) {
                Log.e("StatisticsViewModel", "Network/Parsing Error: ${e.message}", e)
                onError("Connection error. Please check your internet.")
            } finally {
                onLoading(false)
            }
        }
    }

    fun refreshDashboardStats(
        onSuccess: (DashboardResponse) -> Unit,
        onError: (String) -> Unit,
        onLoading: (Boolean) -> Unit = {}
    ) {
        // Force refresh by calling the same method
        getDashboardStats(onSuccess, onError, onLoading)
    }
}