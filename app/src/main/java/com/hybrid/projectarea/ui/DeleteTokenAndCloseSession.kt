package com.hybrid.projectarea.ui

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hybrid.projectarea.R
import com.hybrid.projectarea.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun DeleteTokenAndCloseSession(fragment: Fragment) {
    fragment.lifecycleScope.launch(Dispatchers.IO) {
        deleteTokenFromDataStore(fragment.requireContext())
        withContext(Dispatchers.Main) {
            fragment.findNavController().navigate(R.id.AuthFragment)
            RetrofitClient.deleteHttpClient()
        }
    }
}

private suspend fun deleteTokenFromDataStore(context: Context) {
    context.dataStore.edit { preferences ->
        preferences.remove(stringPreferencesKey("token"))
        preferences.remove(stringPreferencesKey("user_id"))
    }
}
