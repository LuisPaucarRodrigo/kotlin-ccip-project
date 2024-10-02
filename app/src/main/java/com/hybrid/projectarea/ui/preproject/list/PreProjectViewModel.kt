package com.hybrid.projectarea.ui.preproject.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.PreProject
import com.hybrid.projectarea.domain.repository.preproject.PreProjectRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PreProjectViewModel: ViewModel() {
    private val _preprojects =  MutableLiveData<List<PreProject>>()
    val data: LiveData<List<PreProject>> get() = _preprojects

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getPreproject(token:String,id:String){
        viewModelScope.launch(Dispatchers.IO) {
            val preprojectRepository = PreProjectRepository(RetrofitClient.getClient(token))
            val result = preprojectRepository.preproject(id)
            result.onSuccess { success ->
                _preprojects.postValue(success)
            }.onFailure { exception ->
                if (exception.message == "Token no Valido"){
                    _error.postValue("Token no valido. Cerrando sesion")
                }else{
                    _error.postValue(exception.message)
                }

            }
        }
    }
}