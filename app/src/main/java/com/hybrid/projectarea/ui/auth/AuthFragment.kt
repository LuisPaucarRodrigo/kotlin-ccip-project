package com.hybrid.projectarea.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentAuthBinding
import com.hybrid.projectarea.domain.model.LoginRequest
import com.hybrid.projectarea.domain.model.LoginResponse
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.ui.BaseActivity
import com.hybrid.projectarea.ui.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthFragment : Fragment() {
    private var _binding:FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
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
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        authViewModel.postSuccess.observe(viewLifecycleOwner) { success ->
            lifecycleScope.launch(Dispatchers.IO) {
                requireContext().dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("token")] = success.token
                    preferences[stringPreferencesKey("userId")] = success.id
                }
            }
            findNavController().navigate(R.id.to_PreProjectFragment)
        }
        authViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(),error,Toast.LENGTH_LONG).show()
        }
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
               authViewModel.postAuth(formData)
            } catch (e: Exception) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
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