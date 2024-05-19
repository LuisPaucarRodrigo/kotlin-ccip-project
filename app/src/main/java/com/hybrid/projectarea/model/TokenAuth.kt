package com.hybrid.projectarea.model

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hybrid.projectarea.view.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object TokenAuth {
    suspend fun getToken(context: Context): String {
        val tokenFlow = context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey("token")] ?: ""
        }
        return tokenFlow.first()
    }
}