package com.hybrid.projectarea.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.LoginRequest
import com.hybrid.projectarea.domain.model.LoginResponse
import com.hybrid.projectarea.domain.model.UsersResponse
import com.hybrid.projectarea.domain.repository.AuthRepository
import com.hybrid.projectarea.domain.repository.UsersRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _postSuccess = MutableLiveData<LoginResponse>()
    val postSuccess:LiveData<LoginResponse> get() = _postSuccess

    private val _error = MutableLiveData<String>()
    val error:LiveData<String> get() = _error

    fun postAuth(login: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val authRepository = AuthRepository(RetrofitClient.getClient())
            val result = authRepository.postAuth(login)
            result.onSuccess { success ->
                _postSuccess.postValue(success)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }

}