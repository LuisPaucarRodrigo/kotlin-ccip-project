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
import com.hybrid.projectarea.model.LoginResponse
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "datastore")

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val token = TokenAuth.getToken(this@AuthActivity)
            if (token !== "") {
                navigateToBaseActivity()
            } else {
                setupLoginButton()
            }
        }
    }

    private fun setupLoginButton() {
        binding.BtnAuth.setOnClickListener {
            val dni = binding.DniUsers.text.toString()
            val password = binding.PasswordUser.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val apiService = RetrofitClient.getClient().create(ApiService::class.java)
                    val authManager = AuthManager(apiService)
                    authManager.login(dni, password, object : AuthManager.AuthListener {
                        override fun onLoginSuccess(response: LoginResponse) {
                            lifecycleScope.launch {
                                dataStore.edit { preferences ->
                                    preferences[stringPreferencesKey("token")] = response.token
                                }
                            }
                            val baseActivity = Intent(this@AuthActivity, BaseActivity::class.java)
                            startActivity(baseActivity)
                            finish()
                        }

                        override fun onLoginFailed(errorMessage: String) {
                            println(errorMessage)
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
    }

    private fun navigateToBaseActivity() {
        val baseActivity = Intent(this, BaseActivity::class.java)
        startActivity(baseActivity)
        finish()
    }
}

