package com.hybrid.projectarea.ui.checklist.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.checklistDiary
import com.hybrid.projectarea.domain.repository.checklist.DiaryRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryCheckListViewModel:ViewModel() {
    private val _postSuccess = MutableLiveData<Boolean>()
    val postSuccess:LiveData<Boolean> get() = _postSuccess

    private val _error = MutableLiveData<String>()
    val error:LiveData<String> get() = _error

    fun postDiary(token:String,diary: checklistDiary){
        viewModelScope.launch(Dispatchers.IO) {
            val diaryRepository = DiaryRepository(RetrofitClient.getClient(token))
            val result = diaryRepository.postDiaryCheckList(diary)
            result.onSuccess {
                _postSuccess.postValue(true)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }
}