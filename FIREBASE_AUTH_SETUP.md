# Firebase Authentication Setup Guide

This guide explains how to complete the Firebase Authentication integration for the EheFin Android application.

## Overview

The app now supports Firebase Authentication with Google Sign-In. The implementation follows the backend's `POST /api/auth/google-login` endpoint specification.

## What Was Implemented

### 1. Dependencies Added

- [`firebase-auth-ktx`](gradle/libs.versions.toml:99) - Firebase Authentication
- [`play-services-auth`](gradle/libs.versions.toml:102) - Google Sign-In SDK

### 2. API Layer

- New endpoint constant [`AUTH_GOOGLE_LOGIN`](app/src/main/java/com/example/ehefin_mobile/core/common/Constants.kt:23) = `"auth/google-login"`
- New DTO [`FirebaseLoginRequest`](app/src/main/java/com/example/ehefin_mobile/feature/auth/data/source/remote/dto/AuthRequest.kt:54) with `idToken` and optional `fcmToken`
- New API method [`firebaseLogin()`](app/src/main/java/com/example/ehefin_mobile/feature/auth/data/source/remote/AuthApi.kt:44) in [`AuthApi`](app/src/main/java/com/example/ehefin_mobile/feature/auth/data/source/remote/AuthApi.kt:18)

### 3. Repository Layer

- Added [`loginWithFirebase()`](app/src/main/java/com/example/ehefin_mobile/feature/auth/data/repository/AuthRepositoryImpl.kt:112) method to [`AuthRepository`](app/src/main/java/com/example/ehefin_mobile/feature/auth/domain/repository/AuthRepository.kt:10) interface and implementation

### 4. Use Case

- Created [`FirebaseLoginUseCase`](app/src/main/java/com/example/ehefin_mobile/feature/auth/domain/usecase/FirebaseLoginUseCase.kt:12) for clean architecture

### 5. ViewModel

- Added [`loginWithFirebase()`](app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/viewmodel/AuthViewModel.kt:224) method to [`AuthViewModel`](app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/viewmodel/AuthViewModel.kt:56)

### 6. UI Components

- Created [`GoogleSignInButton`](app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/components/GoogleSignInButton.kt:26) component
- Created [`OrDivider`](app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/components/GoogleSignInButton.kt:73) component
- Updated [`LoginScreen`](app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/screen/LoginScreen.kt:43) with Google Sign-In functionality

### 7. Helper Utilities

- Created [`GoogleSignInHelper`](app/src/main/java/com/example/ehefin_mobile/feature/auth/util/GoogleSignInHelper.kt:10) for configuration management

## Required Configuration

### Step 1: Get Your Web Client ID

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to Project Settings → General
4. Scroll down to "Your apps" section
5. Find your Android app and click on it
6. Look for "Web API Key" and "Web Client ID" in the configuration

Alternatively, check your `app/google-services.json` file:

```json
{
  "client": [
    {
      "oauth_client": [
        {
          "client_id": "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com",
          "client_type": 3
        }
      ]
    }
  ]
}
```

### Step 2: Web Client ID (Already Configured)

The Web Client ID has been automatically extracted from your `google-services.json` file:

- **Client ID**: `173366112230-sciav113b6rvhqc9vbde15nbv2c76kl7.apps.googleusercontent.com`

This is configured in [`GoogleSignInHelper.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/util/GoogleSignInHelper.kt:16) and used by [`LoginScreen.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/screen/LoginScreen.kt:137). No manual changes needed.

### Step 3: Enable Google Sign-In in Firebase

1. In Firebase Console, go to **Authentication** → **Sign-in method**
2. Enable **Google** provider
3. Configure the OAuth consent screen if prompted
4. Save changes

### Step 4: Configure OAuth Consent Screen (if not done)

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your project
3. Go to **APIs & Services** → **OAuth consent screen**
4. Configure the consent screen (External or Internal)
5. Add required scopes: `email`, `profile`
6. Add test users if using External mode

## How It Works

1. User taps "Masuk dengan Google" button
2. Google Sign-In dialog appears
3. User selects Google account
4. App receives Google ID Token
5. App authenticates with Firebase using the token
6. Firebase returns Firebase ID Token
7. App sends Firebase ID Token to backend via `POST /api/auth/google-login`
8. Backend verifies token and returns JWT token
9. App saves JWT token and navigates to main screen

## Testing

1. Build and run the app
2. Tap "Masuk dengan Google"
3. Select a Google account
4. Verify successful login

## Troubleshooting

### "Sign-in failed" error

- Check that Web Client ID is correct
- Verify Google Sign-In is enabled in Firebase Console
- Check that SHA-1 fingerprint is added to Firebase project

### "Backend login failed" error

- Verify backend is running and accessible
- Check that `/api/auth/google-login` endpoint is implemented
- Review backend logs for token verification errors

### Firebase Auth errors

- Ensure `google-services.json` is in `app/` directory
- Verify Firebase project is properly configured
- Check internet connection

## Security Notes

- Never commit your actual Web Client ID to public repositories
- Consider using BuildConfig fields or environment variables
- The Firebase ID Token is short-lived (1 hour) and verified by the backend
- The backend JWT token should be stored securely

## Files Modified/Created

### Modified Files:

- [`gradle/libs.versions.toml`](gradle/libs.versions.toml)
- [`app/build.gradle.kts`](app/build.gradle.kts)
- [`app/src/main/java/com/example/ehefin_mobile/core/common/Constants.kt`](app/src/main/java/com/example/ehefin_mobile/core/common/Constants.kt)
- [`app/src/main/java/com/example/ehefin_mobile/feature/auth/data/source/remote/AuthApi.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/data/source/remote/AuthApi.kt)
- [`app/src/main/java/com/example/ehefin_mobile/feature/auth/data/source/remote/dto/AuthRequest.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/data/source/remote/dto/AuthRequest.kt)
- [`app/src/main/java/com/example/ehefin_mobile/feature/auth/data/repository/AuthRepositoryImpl.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/data/repository/AuthRepositoryImpl.kt)
- [`app/src/main/java/com/example/ehefin_mobile/feature/auth/domain/repository/AuthRepository.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/domain/repository/AuthRepository.kt)
- [`app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/viewmodel/AuthViewModel.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/viewmodel/AuthViewModel.kt)
- [`app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/screen/LoginScreen.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/screen/LoginScreen.kt)

### New Files:

- [`app/src/main/java/com/example/ehefin_mobile/feature/auth/domain/usecase/FirebaseLoginUseCase.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/domain/usecase/FirebaseLoginUseCase.kt)
- [`app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/components/GoogleSignInButton.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/presentation/components/GoogleSignInButton.kt)
- [`app/src/main/java/com/example/ehefin_mobile/feature/auth/util/GoogleSignInHelper.kt`](app/src/main/java/com/example/ehefin_mobile/feature/auth/util/GoogleSignInHelper.kt)
- [`app/src/main/res/drawable/ic_google_logo.xml`](app/src/main/res/drawable/ic_google_logo.xml)
