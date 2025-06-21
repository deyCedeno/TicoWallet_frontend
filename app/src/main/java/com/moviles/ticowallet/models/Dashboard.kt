package com.moviles.ticowallet.models

data class DashboardResponse(
    val balancesByCurrency: List<CurrencyBalance>,
    val monthlyFlow: MonthlyFlow,
    val topExpensesByCategory: List<CategoryExpense>,
    val accountsOverview: AccountsOverview,
    val lastUpdated: String,
    val dataPeriod: String
)

data class CurrencyBalance(
    val currency: String,
    val totalBalance: Double
)

data class MonthlyFlow(
    val income: Double,
    val expenses: Double,
    val netFlow: Double,
    val month: String
)

data class CategoryExpense(
    val categoryId: Int,
    val categoryName: String,
    val totalAmount: Double,
    val currency: String,
    val transactionCount: Int,
    val lastTransactionDate: String
)

data class AccountsOverview(
    val totalAccounts: Int,
    val activeAccountsThisMonth: Int,
    val totalTransactionsThisMonth: Int,
    val averageTransactionAmount: Double
)