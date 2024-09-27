package com.hybrid.projectarea.ui.preproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.PreprojectTitle
import com.hybrid.projectarea.domain.repository.CodePhotoRepository
import kotlinx.coroutines.launch

class CodePhotoViewModel(private val codePhotoRepository: CodePhotoRepository): ViewModel() {
    private val _codePhotoPreProject = MutableLiveData<List<PreprojectTitle>>()
    val data: LiveData<List<PreprojectTitle>> get() = _codePhotoPreProject

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getCodePhoto(token:String,id:String) {
        viewModelScope.launch {
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