package com.hybrid.projectarea.ui.preproject.reports.imageHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.Photo
import com.hybrid.projectarea.domain.repository.ImageHistoryRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.launch

class ImageHistoryViewModel :ViewModel(){
    private val _imageHistory = MutableLiveData<List<Photo>>()
    val data:LiveData<List<Photo>> get() = _imageHistory

    private val _error = MutableLiveData<String>()
    val error:LiveData<String> get() = _error


    fun getImagesHistory(token:String,code_id:String){
        viewModelScope.launch {
            val imageHistoryRepository = ImageHistoryRepository(RetrofitClient.getClient("das"))
            val result = imageHistoryRepository.getPhotos(token, code_id)
            result.onSuccess { success ->
                _imageHistory.value = success
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }
}