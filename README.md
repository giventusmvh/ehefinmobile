# EheFin Mobile - Aplikasi Pinjaman Digital

## 📋 Overview

EheFin Mobile adalah aplikasi Android modern untuk pengajuan pinjaman digital yang dibangun dengan arsitektur Clean Architecture dan teknologi terbaru. Aplikasi ini memungkinkan pengguna untuk mengajukan pinjaman, melihat status pinjaman, mengelola plafond, dan memantau profil pengguna.

## 🏗️ Teknologi Utama

### Core Technologies

- **Kotlin** - Bahasa pemrograman utama
- **Jetpack Compose** - UI toolkit modern untuk Android
- **Hilt** - Dependency Injection framework
- **Coroutines** - Asynchronous programming
- **Flow** - Reactive data streams

### Architecture & Design

- **Clean Architecture** - Pemisahan layer yang jelas (Data, Domain, Presentation)
- **MVVM** - Model-View-ViewModel pattern
- **Repository Pattern** - Abstraksi sumber data
- **Single Responsibility Principle** - Setiap class memiliki satu tanggung jawab

### Data & Storage

- **Room Database** - Local database untuk offline support
- **DataStore** - Secure storage untuk token dan preferences
- **Retrofit** - REST API client
- **OkHttp** - HTTP client dengan interceptors
- **Gson** - JSON serialization/deserialization

### Background Processing

- **WorkManager** - Background task scheduling
- **Firebase Cloud Messaging** - Push notifications

### Location & Maps

- **Google Play Services Location** - GPS dan location services

### Testing & Debugging

- **Chucker** - HTTP request/response debugging
- **JUnit** - Unit testing
- **Espresso** - UI testing

## 📁 Struktur Proyek

```
ehefin-mobile/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/ehefin_mobile/
│   │   │   ├── core/                    # Core functionality
│   │   │   │   ├── common/              # Shared utilities & constants
│   │   │   │   ├── database/            # Room database setup
│   │   │   │   ├── datastore/           # DataStore for preferences
│   │   │   │   ├── designsystem/        # UI theme & components
│   │   │   │   ├── di/                  # Dependency Injection modules
│   │   │   │   ├── network/             # Network layer (Retrofit, OkHttp)
│   │   │   │   ├── util/                # Utility classes
│   │   │   │   └── worker/              # WorkManager workers
│   │   │   ├── feature/                 # Feature modules
│   │   │   │   ├── auth/                # Authentication feature
│   │   │   │   ├── home/                # Home screen
│   │   │   │   ├── loan/                # Loan management
│   │   │   │   ├── plafond/             # Plafond management
│   │   │   │   ├── profile/             # User profile
│   │   │   │   └── notification/        # Push notifications
│   │   │   ├── navigation/              # Navigation setup
│   │   │   ├── EheFinApplication.kt     # Application class
│   │   │   └── MainActivity.kt          # Main activity
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 🚀 Fitur Utama

### 1. Authentication

- Login dengan email dan password
- Registrasi akun baru
- Google Sign-In dengan Firebase
- Lupa password
- Session management dengan DataStore

### 2. Loan Management

- Pengajuan pinjaman baru
- Melihat daftar pinjaman
- Detail pinjaman dengan history
- Status tracking (Pending, Approved, Rejected, etc.)
- Offline support untuk pengajuan pinjaman

### 3. Plafond Management

- Melihat plafond aktif
- Memilih produk plafond
- Tracking sisa plafond
- Progress visualization

### 4. Profile Management

- Lihat dan edit profil
- Upload dokumen (KTP, KK, NPWP)
- Data rekening bank
- Profile completion tracking

### 5. Background Sync

- Auto-sync data saat online
- Pending request queue
- Reference data sync (branches, products)
- Retry mechanism untuk failed requests

### 6. Push Notifications

- FCM token registration
- Notifikasi status pinjaman
- Notifikasi update aplikasi

## 🔐 Security Features

- JWT token authentication
- Secure token storage dengan DataStore
- Automatic token refresh
- User switch detection
- Data cleanup saat logout
- HTTPS communication

## 📱 UI/UX Features

- Material Design 3
- Responsive layout
- Dark/Light theme support
- Smooth animations
- Loading states
- Error handling dengan user-friendly messages
- Offline mode indicators

## 🌐 API Integration

### Base URLs

- **Development**: `http://10.0.2.2:8080/api/`
- **Staging**: `https://staging.ehefin.com/api/`
- **Production**: `https://api.ehefin.com/api/`

### Key Endpoints

- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/firebase-login` - Firebase login
- `GET /profile` - Get user profile
- `PUT /profile` - Update profile
- `POST /loans` - Submit loan application
- `GET /loans` - Get user loans
- `GET /plafond` - Get user plafond
- `POST /plafond/select` - Select plafond product
- `GET /branches` - Get branches list
- `GET /products` - Get products list

## 🔄 Data Flow

### Authentication Flow

1. User enters credentials
2. ViewModel validates input
3. Use case calls repository
4. Repository makes API call
5. Response handled and token saved
6. User data synced to local DB
7. FCM token registered
8. Navigation to home screen

### Loan Submission Flow

1. User fills loan form
2. Location captured (GPS)
3. Validation performed
4. If online: Submit to API
5. If offline: Save to pending requests
6. SyncWorker processes pending requests
7. User notified of result

## 📦 Dependencies

### AndroidX

- `androidx.core.ktx`
- `androidx.lifecycle.*`
- `androidx.activity.compose`
- `androidx.navigation.compose`
- `androidx.work.runtime.ktx`

### Compose

- `androidx.compose.ui`
- `androidx.compose.material3`
- `androidx.compose.material.icons.extended`

### DI

- `com.google.dagger:hilt-android`
- `androidx.hilt:hilt-navigation-compose`
- `androidx.hilt:hilt-work`

### Database

- `androidx.room.*`

### Networking

- `com.squareup.retrofit2:*`
- `com.squareup.okhttp3:*`
- `com.google.code.gson:gson`

### Firebase

- `com.google.firebase:*`

### Location

- `com.google.android.gms:play-services-location`
- `com.google.android.gms:play-services-auth`

### Image Loading

- `io.coil-kt:coil-compose`

### Debugging

- `com.github.chuckerteam.chucker:library`

## 🛠️ Development Setup

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 35
- Gradle 8.x

### Build Variants

- **debug** - Development build dengan debugging tools
- **release** - Production build dengan ProGuard/R8

### Environment Configuration

Base URL diatur di `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "ACTIVE_BASE_URL", "\"http://10.0.2.2:8080/api/\"")
```

## 📝 Coding Standards

### Kotlin Style

- Menggunakan ktlint untuk code formatting
- Spotless plugin untuk otomatisasi formatting
- Follow Android Kotlin style guide

### Architecture Guidelines

- Clean Architecture dengan layer terpisah
- Dependency Injection dengan Hilt
- Single Responsibility Principle
- Repository pattern untuk data access
- Use cases untuk business logic

### Naming Conventions

- Classes: PascalCase
- Functions: camelCase
- Constants: UPPER_SNAKE_CASE
- Private properties: camelCase dengan underscore prefix jika diperlukan

## 🧪 Testing

### Unit Tests

- Use cases
- ViewModels
- Repository implementations
- Utility functions

### Instrumented Tests

- Database operations
- WorkManager workers
- UI interactions

## 📄 License

Proprietary - All rights reserved

## 👥 Team

EheFin Development Team

---

Untuk dokumentasi lebih detail, lihat:

- [ARCHITECTURE.md](ARCHITECTURE.md) - Arsitektur aplikasi
- [SETUP.md](SETUP.md) - Panduan setup dan instalasi
- [FEATURES.md](FEATURES.md) - Detail fitur
- [API.md](API.md) - Dokumentasi API
- [WORKFLOW.md](WORKFLOW.md) - Alur kerja aplikasi
