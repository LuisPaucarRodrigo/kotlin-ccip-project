package com.hybrid.projectarea.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.UsersResponse
import com.hybrid.projectarea.domain.repository.UsersRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BaseViewModel: ViewModel() {
    private var _users = MutableLiveData<UsersResponse>()
    val data: LiveData<UsersResponse> get() = _users

    private var _error = MutableLiveData<String>()
    val error:LiveData<String> get() = _error

    fun getUser(token:String,user_id:String){
        viewModelScope.launch(Dispatchers.IO) {
            val usersRepository = UsersRepository(RetrofitClient.getClient())
            val result = usersRepository.fetchUsers(token, user_id)
            result.onSuccess { success ->
                _users.postValue(success)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }
}