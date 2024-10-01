package com.hybrid.projectarea.ui.preproject.stages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.PreprojectTitle
import com.hybrid.projectarea.domain.repository.CodePhotoRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.launch

class CodePhotoViewModel: ViewModel() {
    private val _codePhotoPreProject = MutableLiveData<List<PreprojectTitle>>()
    val data: LiveData<List<PreprojectTitle>> get() = _codePhotoPreProject

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getCodePhoto(token:String,id:String) {
        viewModelScope.launch {
            val codePhotoRepository = CodePhotoRepository(RetrofitClient.getClient(token))
            val result = codePhotoRepository.codePhotoPreProject(token, id)
            result.onSuccess { success ->
                _codePhotoPreProject.value = success
            }.onFailure { exception ->
                if (exception.message == "Token no válido") {
                    _error.value = "Token no válido. Cerrando sesión."
                } else {
                    _error.value = exception.message
                }
            }
        }
    }
}