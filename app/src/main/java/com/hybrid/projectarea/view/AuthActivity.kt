package com.hybrid.projectarea.view

import android.content.Context
import android.content.Intent
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.hybrid.projectarea.databinding.ActivityAuthBinding
import com.hybrid.projectarea.model.LoginRequest
import com.hybrid.projectarea.model.LoginResponse
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "datastore")

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val token = TokenAuth.getToken(this@AuthActivity,"token")
            if (token !== "") {
                navigateToBaseActivity()
            } else {
                setupLoginButton()
            }
        }
    }

    private fun setupLoginButton() {
        binding.BtnAuth.setOnClickListener {
            val formData = getFormData()
            if (areAllFieldsFilled(formData)){
                sendFormData(formData)
            }else{
                Toast.makeText(this@AuthActivity,"Complete todo los Campos",Toast.LENGTH_LONG).show()
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
                            dataStore.edit { preferences ->
                                preferences[stringPreferencesKey("token")] = response.token
                                preferences[stringPreferencesKey("userId")] = response.id
                            }
                        }
                        val baseActivity = Intent(this@AuthActivity, BaseActivity::class.java)
                        startActivity(baseActivity)
                        finish()
                    }
                    override fun onLoginFailed(errorMessage: String) {
                        Toast.makeText(
                            this@AuthActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } catch (e: Exception) {
                Toast.makeText(this@AuthActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun navigateToBaseActivity() {
        val baseActivity = Intent(this, BaseActivity::class.java)
        startActivity(baseActivity)
        finish()
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

