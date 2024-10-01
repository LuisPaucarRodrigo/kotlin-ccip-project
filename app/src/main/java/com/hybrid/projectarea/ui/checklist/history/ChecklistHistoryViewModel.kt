package com.hybrid.projectarea.ui.checklist.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.ChecklistHistory
import com.hybrid.projectarea.domain.repository.checklist.CheckListHistoryRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChecklistHistoryViewModel:ViewModel() {
    private val _history = MutableLiveData<List<ChecklistHistory>>()
    val data :LiveData<List<ChecklistHistory>> get() = _history

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getChecklist(token:String){
        viewModelScope.launch(Dispatchers.IO) {
            val checklistRepository = CheckListHistoryRepository(RetrofitClient.getClient())
            val result = checklistRepository.getChecklist(token)
            result.onSuccess { success ->
                _history.postValue(success)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }
}