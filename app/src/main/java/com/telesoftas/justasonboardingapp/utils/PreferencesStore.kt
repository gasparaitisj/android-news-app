package com.telesoftas.justasonboardingapp.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class PreferencesStore @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val Context.dataStore by preferencesDataStore(PreferencesKeys.SETTINGS_PREFERENCE_NAME)

    fun isFirstLaunch(): Flow<Boolean> = context.dataStore.data.catch { exception ->
        // dataStore.data throws an IOException if it can't read the data
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[PreferencesKeys.FIRST_LAUNCH] ?: true
    }

    suspend fun updateIsFirstLaunch(isFirstLaunch: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_LAUNCH] = isFirstLaunch
        }
    }
}

object PreferencesKeys {
    const val SETTINGS_PREFERENCE_NAME = "settings"
    val FIRST_LAUNCH = booleanPreferencesKey("FIRST_LAUNCH")
}
