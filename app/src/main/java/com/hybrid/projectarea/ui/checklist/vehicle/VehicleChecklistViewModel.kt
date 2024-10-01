package com.hybrid.projectarea.ui.checklist.vehicle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hybrid.projectarea.domain.model.checkListTools
import com.hybrid.projectarea.domain.model.checkListVehicle
import com.hybrid.projectarea.domain.repository.checklist.VehicleRepository
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VehicleChecklistViewModel:ViewModel() {
    private val _postSuccess = MutableLiveData<Boolean>()
    val postSuccess: LiveData<Boolean> get() = _postSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun postVehicle(token:String, vehicle: checkListVehicle){
        viewModelScope.launch(Dispatchers.IO) {
            val vehicleRepository = VehicleRepository(RetrofitClient.getClient())
            val result = vehicleRepository.postVehicleCheckList(token,vehicle)
            result.onSuccess {
                _postSuccess.postValue(true)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }
}