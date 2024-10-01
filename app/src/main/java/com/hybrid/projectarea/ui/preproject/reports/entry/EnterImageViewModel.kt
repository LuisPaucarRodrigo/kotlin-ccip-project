package com.hybrid.projectarea.ui.preproject.reports.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.CodePhotoDescription
import com.hybrid.projectarea.domain.model.ImageReport
import com.hybrid.projectarea.domain.repository.preproject.EnterImageRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EnterImageViewModel:ViewModel() {
    private val _codeData = MutableLiveData<CodePhotoDescription>()
    val data:LiveData<CodePhotoDescription> get() = _codeData

    private val _postSuccess = MutableLiveData<Boolean>()
    val postSuccess: LiveData<Boolean> get() = _postSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getData(token:String, code_id: String){
        viewModelScope.launch(Dispatchers.IO) {
            val enterImageRepository = EnterImageRepository(RetrofitClient.getClient())
            val result = enterImageRepository.getData(token,code_id)
            result.onSuccess { success ->
                _codeData.postValue(success)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }

    fun postImageReport(token:String, image: ImageReport){
        viewModelScope.launch(Dispatchers.IO) {
            val enterImageRepository = EnterImageRepository(RetrofitClient.getClient())
            val result = enterImageRepository.postImage(token,image)
            result.onSuccess {
                _postSuccess.postValue(true)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }
}