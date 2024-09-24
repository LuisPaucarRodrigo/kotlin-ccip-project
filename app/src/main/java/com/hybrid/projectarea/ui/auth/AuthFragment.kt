package com.hybrid.projectarea.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentAuthBinding
import com.hybrid.projectarea.model.LoginRequest
import com.hybrid.projectarea.model.LoginResponse
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.ui.BaseActivity
import com.hybrid.projectarea.ui.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthFragment : Fragment() {
    private var _binding:FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).supportActionBar?.hide()
        binding.BtnAuth.setOnClickListener {
            val formData = getFormData()
            if (areAllFieldsFilled(formData)){
                sendFormData(formData)
            }else{
                Toast.makeText(requireContext(),"Complete todo los Campos", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendFormData(formData: LoginRequest) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = RetrofitClient.getClient().create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.login(formData, object : AuthManager.AuthListener {
                    override fun onLoginSuccess(response: LoginResponse) {
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                requireContext().dataStore.edit { preferences ->
                                    preferences[stringPreferencesKey("token")] = response.token
                                    preferences[stringPreferencesKey("userId")] = response.id
                                }
                            }
                        }
                        findNavController().navigate(R.id.PreProjectFragment)
                    }
                    override fun onLoginFailed(errorMessage: String) {
                        Toast.makeText(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun getFormData(): LoginRequest {
        return LoginRequest(
            dni = binding.dniUsers.text.toString(),
            password = binding.passwordUser.text.toString(),
        )
    }

    private fun areAllFieldsFilled(formData: LoginRequest): Boolean {
        return with(formData) {
            dni.isNotEmpty() && password.isNotEmpty()
        }
    }
}