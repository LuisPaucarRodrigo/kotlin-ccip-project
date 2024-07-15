package com.hybrid.projectarea.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.ActivityBaseBinding
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.model.UsersResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding:ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setNavigationItemSelectedListener(this)
        setSupportActionBar(binding.mitoolbar.root)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navheader = binding.navView.getHeaderView(0)
        val nameheader = navheader.findViewById<TextView>(R.id.txtnombreuser)
        val nameheaderdni = navheader.findViewById<TextView>(R.id.txtdniuser)
        val nameheaderemail = navheader.findViewById<TextView>(R.id.txtemail)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(this@BaseActivity)
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.user(token, object : AuthManager.Users {
                    override fun onUserSuccess(response: UsersResponse) {
                        nameheader.text = "Nombre: ${ response.name }"
                        nameheaderdni.text = "Dni: ${ response.dni }"
                        nameheaderemail.text = "Email: ${ response.email }"
                    }

                    override fun onUserFailed() {
                        Toast.makeText(this@BaseActivity, getString(R.string.check_connection), Toast.LENGTH_LONG)
                            .show()
                    }
                })
            } catch (e: Exception) {
                Toast.makeText(this@BaseActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
        preproject()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            binding.layoutLateral.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_preproject -> preproject()
            R.id.nav_project -> project()
            R.id.nav_huawei -> huawei()
            R.id.nav_logout -> cerrarsesion()
        }
        binding.layoutLateral.closeDrawer(GravityCompat.START)
        return true
    }

    private fun project() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor, ProjectFragment())
        transaction.commit()
    }

    private fun preproject() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor, PreProjectFragment())
        transaction.commit()
    }

    private fun huawei() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor, HuaweiFragment())
        transaction.commit()
    }

    private fun cerrarsesion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cerrar Sesion")
        builder.setMessage("Esta seguro de Cerrar Sesion?")

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            logout()
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
        }
        builder.show()

    }

    private fun logout() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(this@BaseActivity)
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.logout(token, object : AuthManager.Logout {
                    override fun onLogoutSuccess() {
                        lifecycleScope.launch {
                            deleteTokenFromDataStore()
                        }
                        val authActivity = Intent(this@BaseActivity, AuthActivity::class.java)
                        startActivity(authActivity)
                        finish()
                    }

                    override fun onLogoutFailed () {
                        Toast.makeText(this@BaseActivity, getString(R.string.check_connection), Toast.LENGTH_LONG).show()
                    }
                }
                )
            } catch (e: Exception) {
                // Manejar errores
            }
        }
    }

    private suspend fun deleteTokenFromDataStore() {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey("token"))
        }
    }

}