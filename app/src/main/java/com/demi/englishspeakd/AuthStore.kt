package com.demi.englishspeakd

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

/**
 * Google Sign-In (free, via Play Services Auth — no Firebase needed).
 *
 * SETUP (free, ~10 minutes, done once by DEMi):
 * 1. Go to Google Cloud Console → create a project → OAuth consent screen.
 * 2. Create OAuth Client ID (Android): package com.demi.englishspeakd + your SHA-1.
 * 3. Create a Web Client ID too, and put it in res/values/google_signin.xml
 *    as `google_web_client_id`. (The file is in this repo with a placeholder.)
 * 4. Rebuild. Sign-in will then work on a real phone.
 *
 * Until the Client ID is set, sign-in shows a clear setup message and
 * the app still works fully in Guest mode — nothing is blocked.
 */
object AuthStore {
    const val RC_SIGN_IN = 9001
    private const val PREFS = "englishspeakd_auth"

    fun googleClient(context: Context, webClientId: String?): GoogleSignInClient? {
        if (webClientId.isNullOrBlank() || webClientId.startsWith("PASTE_")) return null
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun signInIntent(context: Context, webClientId: String?): Intent? =
        googleClient(context, webClientId)?.signInIntent

    fun handleResult(context: Context, data: Intent?): GoogleSignInAccount? {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            saveProfile(context, account?.displayName ?: "Learner", account?.email ?: "")
            account
        } catch (_: Exception) {
            null
        }
    }

    fun saveProfile(context: Context, name: String, email: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("name", name).putString("email", email).putBoolean("signed_in", true).apply()
    }

    fun signInAsGuest(context: Context, name: String = "Guest Learner") {
        saveProfile(context, name, "")
    }

    fun signOut(context: Context, webClientId: String?) {
        googleClient(context, webClientId)?.signOut()
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun displayName(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("name", "Guest Learner") ?: "Guest Learner"

    fun isSignedIn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("signed_in", false)
}
