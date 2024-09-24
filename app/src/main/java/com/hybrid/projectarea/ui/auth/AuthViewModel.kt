package com.hybrid.projectarea.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.UsersResponse
import com.hybrid.projectarea.domain.repository.UsersRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UsersRepository) : ViewModel() {
    val user = MutableLiveData<UsersResponse>()
    val error = MutableLiveData<String>()

//    fun getUser(token:String,id:String) {
//        viewModelScope.launch {
//            try {
//                val userData = userRepository.fetchUsers(token,id)
//                user.postValue(userData)
//            } catch (e: Exception) {
//                error.postValue(e.message)
//            }
//        }
//    }

}