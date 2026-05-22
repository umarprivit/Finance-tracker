package com.fintrack.app.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.app.domain.usecase.ExportReportUseCase
import com.fintrack.app.domain.usecase.GetCategoriesUseCase
import com.fintrack.app.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val exportReportUseCase: ExportReportUseCase
) : ViewModel() {

    fun exportCsv(context: Context) {
        viewModelScope.launch {
            val transactions = getTransactionsUseCase().first()
            val categories = getCategoriesUseCase().first().associateBy { it.id }
            val txWithCat = transactions.map { it to categories[it.categoryId] }
            exportReportUseCase.exportToCsv(context, txWithCat)
        }
    }

    fun exportPdf(context: Context) {
        viewModelScope.launch {
            val transactions = getTransactionsUseCase().first()
            val categories = getCategoriesUseCase().first().associateBy { it.id }
            val txWithCat = transactions.map { it to categories[it.categoryId] }
            exportReportUseCase.exportToPdf(context, txWithCat)
        }
    }
}
