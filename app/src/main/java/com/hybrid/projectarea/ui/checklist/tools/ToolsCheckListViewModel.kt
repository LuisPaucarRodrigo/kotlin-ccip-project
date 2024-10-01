package com.hybrid.projectarea.ui.checklist.tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.checkListTools
import com.hybrid.projectarea.domain.model.checklistEpps
import com.hybrid.projectarea.domain.repository.checklist.EppsRepository
import com.hybrid.projectarea.domain.repository.checklist.ToolsRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToolsCheckListViewModel:ViewModel() {
    private val _postSuccess = MutableLiveData<Boolean>()
    val postSuccess: LiveData<Boolean> get() = _postSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun postTools(token:String,tools: checkListTools){
        viewModelScope.launch(Dispatchers.IO) {
            val toolsRepository = ToolsRepository(RetrofitClient.getClient())
            val result = toolsRepository.postToolsCheckList(token,tools)
            result.onSuccess {
                _postSuccess.postValue(true)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }
}