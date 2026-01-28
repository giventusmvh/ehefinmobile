package com.example.ehefin_mobile.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ehefin_mobile.core.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.AUTH_PREFERENCES
)

/**
 * Secure token and user session management using DataStore
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey(Constants.KEY_ACCESS_TOKEN)
        private val USER_ID = longPreferencesKey(Constants.KEY_USER_ID)
        private val USER_EMAIL = stringPreferencesKey(Constants.KEY_USER_EMAIL)
        private val USER_NAME = stringPreferencesKey(Constants.KEY_USER_NAME)
        private val IS_LOGGED_IN = booleanPreferencesKey(Constants.KEY_IS_LOGGED_IN)
    }
    
    // Access Token
    fun getAccessToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }
    
    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
    }
    
    // User ID
    fun getUserId(): Flow<Long?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }
    
    suspend fun saveUserId(userId: Long) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }
    
    // User Email
    fun getUserEmail(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }
    
    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
        }
    }
    
    // User Name
    fun getUserName(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }
    
    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }
    
    // Login Status
    fun isLoggedIn(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }
    
    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }
    
    /**
     * Save all user session data at once (after login/register)
     */
    suspend fun saveUserSession(
        token: String,
        userId: Long,
        email: String,
        name: String
    ) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = name
            preferences[IS_LOGGED_IN] = true
        }
    }
    
    /**
     * Clear all session data (logout)
     */
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}