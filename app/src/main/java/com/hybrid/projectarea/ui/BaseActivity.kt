package com.hybrid.projectarea.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.ActivityBaseBinding
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val Context.dataStore by preferencesDataStore(name = "datastore")

class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding:ActivityBaseBinding

    private lateinit var baseViewModel: BaseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        baseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)
        lifecycleScope.launch {
            val token = TokenAuth.getToken(this@BaseActivity,"token")
            if (token !== "") {
                openFragment(R.id.PreProjectFragment)
            }
        }

        baseViewModel.data.observe(this) { user ->
            val navheader = binding.navView.getHeaderView(0)
            navheader.findViewById<TextView>(R.id.txtnombreuser).text = "Nombre: ${user.name}"
            navheader.findViewById<TextView>(R.id.txtdniuser).text = "Dni: ${user.dni}"
            navheader.findViewById<TextView>(R.id.txtemail).text = "Email: ${user.email}"
        }
        baseViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    fun requestUser(){
        binding.mitoolbar.root.isVisible = true
        binding.navView.isVisible = true

        binding.navView.setNavigationItemSelectedListener(this)
        setSupportActionBar(binding.mitoolbar.root)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(this@BaseActivity,"token")
                val user_id = TokenAuth.getToken(this@BaseActivity,"userId")
                baseViewModel.getUser(token,user_id)
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    Toast.makeText(this@BaseActivity, "Se produjo un error inesperado: $e", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
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
            R.id.nav_preproject -> openFragment(R.id.PreProjectFragment)
            R.id.nav_expenses -> openFragment(R.id.ExpensesFragment)
            R.id.nav_checkList -> openFragment(R.id.DayCheckListFragment)
            R.id.nav_processManuals -> openFragment(R.id.ManualsFragment)
            R.id.nav_camera -> openFragment(R.id.CameraFragment)
            R.id.nav_logout -> cerrarsesion()
        }
        binding.layoutLateral.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openFragment(id: Int) {
        val navController = findNavController(R.id.container)
        navController.popBackStack()
        navController.navigate(id)
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
                val token = TokenAuth.getToken(this@BaseActivity,"token")
                val apiService = RetrofitClient.getClient(token)
                val authManager = AuthManager(apiService)
                authManager.funlogout(token, object : AuthManager.Logout {
                    override fun onLogoutSuccess() {
                        lifecycleScope.launch(Dispatchers.Main) {
                            deleteTokenFromDataStore()
                            binding.mitoolbar.root.isVisible = false
                            openFragment(R.id.AuthFragment)
                        }
                    }
                    override fun onLogoutNoAuthenticated() {
                        TODO("Not yet implemented")
                    }

                    override fun onLogoutFailed () {
                        Toast.makeText(this@BaseActivity, getString(R.string.check_connection), Toast.LENGTH_LONG).show()
                    }
                }
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BaseActivity, "Se produjo un error inesperado. Por favor inténtalo de nuevo.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun deleteTokenFromDataStore() {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey("token"))
            preferences.remove(stringPreferencesKey("user_id"))
        }
    }
}