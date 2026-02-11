package com.example.ehefin_mobile.feature.auth.util

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

/**
 * Helper class for Google Sign-In configuration
 */
object GoogleSignInHelper {

    /**
     * Web client ID from google-services.json
     * This is the OAuth 2.0 client ID of type 3 (Web client)
     * found in the oauth_client array with client_type: 3
     */
    const val DEFAULT_WEB_CLIENT_ID = com.example.ehefin_mobile.BuildConfig.GOOGLE_CLIENT_ID

    /**
     * Create a Google Sign-In client with the standard configuration
     *
     * @param context The application context
     * @param webClientId The web client ID from Firebase Console (optional, uses default if not provided)
     * @return Configured GoogleSignInClient
     */
    fun getGoogleSignInClient(
        context: Context,
        webClientId: String = DEFAULT_WEB_CLIENT_ID
    ): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * Get the last signed-in account (if any)
     * Useful for checking if user is already signed in
     */
    fun getLastSignedInAccount(context: Context) = GoogleSignIn.getLastSignedInAccount(context)

    /**
     * Check if a user is already signed in with Google
     */
    fun isUserSignedIn(context: Context): Boolean {
        return getLastSignedInAccount(context) != null
    }
}