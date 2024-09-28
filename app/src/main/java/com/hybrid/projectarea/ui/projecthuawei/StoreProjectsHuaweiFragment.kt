package com.hybrid.projectarea.ui.projecthuawei

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentStoreProjectsHuaweiBinding
import com.hybrid.projectarea.domain.model.FormStoreProjectHuawei
import com.hybrid.projectarea.utils.Alert
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.ui.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoreProjectsHuaweiFragment : Fragment() {

    private var _binding:FragmentStoreProjectsHuaweiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreProjectsHuaweiBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.send.buttonSend.setOnClickListener {
            val formData = getFormData()
            if (areAllFieldsFilled(formData)) {
                sendFormData(formData)
            } else {
                Toast.makeText(requireContext(),"Complete todo los Campos",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendFormData(formData: FormStoreProjectHuawei) {
        lifecycleScope.launch {
            val token = TokenAuth.getToken(requireContext(),"token")
            val apiService = withContext(Dispatchers.IO){
                RetrofitClient.getClient(token)
            }
            val authManager = AuthManager(apiService)
            authManager.funStorePtojectHuawei(token,formData,object: AuthManager.inStoreProjectHuawei{
                override fun onStoreProjectHuaweiSuccess() {
                    dataCleaning()
                    Alert.alertSuccess(requireContext(),layoutInflater)
                }

                override fun onStoreProjectHuaweiNoAuthenticated() {
                    DeleteTokenAndCloseSession(this@StoreProjectsHuaweiFragment)
                }

                override fun onStoreProjectHuaweiFailed(errorMessage: String) {
                    Toast.makeText(requireContext(),errorMessage,Toast.LENGTH_LONG).show()
                }

            })
        }

    }

    private fun getFormData(): FormStoreProjectHuawei {
        return FormStoreProjectHuawei(
            site = binding.addSite.text.toString(),
            diu = binding.addDiu.text.toString(),
        )
    }

    private fun areAllFieldsFilled(formData: FormStoreProjectHuawei): Boolean {
        return with(formData) {
            site.isNotEmpty() && diu.isNotEmpty()
        }
    }

    private fun dataCleaning() {
        binding.addSite.text.clear()
        binding.addDiu.text.clear()
    }
}