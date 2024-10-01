package com.hybrid.projectarea.ui.expenses.history


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.ExpenseHistory
import com.hybrid.projectarea.domain.repository.ExpenseRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseHistoryViewModel:ViewModel() {
    private val _expensesHistory = MutableLiveData<List<ExpenseHistory>>()
    val data :LiveData<List<ExpenseHistory>> get() = _expensesHistory

    private val _error = MutableLiveData<String>()
    val error :LiveData<String> get() = _error

    fun getExpenses(token:String){
        viewModelScope.launch(Dispatchers.IO) {
            val expenseRepository = ExpenseRepository(RetrofitClient.getClient())
            val result = expenseRepository.getExpenses(token)
            result.onSuccess { success ->
                _expensesHistory.postValue(success)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }


}