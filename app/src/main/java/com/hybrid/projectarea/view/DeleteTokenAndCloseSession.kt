package com.hybrid.projectarea.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.hybrid.projectarea.R
import com.hybrid.projectarea.view.auth.AuthFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun DeleteTokenAndCloseSession(fragment: Fragment) {
    fragment.lifecycleScope.launch(Dispatchers.IO) {
        deleteTokenFromDataStore(fragment.requireContext())
        withContext(Dispatchers.Main) {

            val transaction: FragmentTransaction = fragment.requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contenedor, AuthFragment())
            transaction.commit()
        }
    }
}

private suspend fun deleteTokenFromDataStore(context: Context) {
    context.dataStore.edit { preferences ->
        preferences.remove(stringPreferencesKey("token"))
        preferences.remove(stringPreferencesKey("user_id"))
    }
}
