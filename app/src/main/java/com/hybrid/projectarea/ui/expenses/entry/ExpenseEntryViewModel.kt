package com.hybrid.projectarea.ui.expenses.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.ExpenseForm
import com.hybrid.projectarea.domain.repository.ExpenseRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseEntryViewModel: ViewModel() {
    private val _postSuccess = MutableLiveData<Boolean>() // Para manejar éxito del post
    val postSuccess: LiveData<Boolean> get() = _postSuccess

    private val _error = MutableLiveData<String>()
    val error : LiveData<String> get() = _error

    fun postExpenses(token: String,expenses: ExpenseForm){
        viewModelScope.launch(Dispatchers.IO) {
            val expenseRepository = ExpenseRepository(RetrofitClient.getClient(token))
            val result = expenseRepository.postExpenses(expenses)
            result.onSuccess {
                _postSuccess.postValue(true)
            }.onFailure { exception ->
                _postSuccess.postValue(false)
                _error.postValue(exception.message)
            }

        }
    }
}