# Agrifarm Sitani 🌾

Aplikasi mobile Android untuk membantu petani dalam menghitung kebutuhan benih dan merencanakan jadwal tanam.

## 📱 Tentang Aplikasi

Agrifarm Sitani adalah aplikasi berbasis Android yang dikembangkan menggunakan Kotlin dan Jetpack Compose. Aplikasi ini menyediakan fitur-fitur untuk membantu petani dalam:

- **Kalkulator Benih**: Menghitung kebutuhan benih berdasarkan luas lahan
- **Kalender Tanam**: Merencanakan dan mengelola jadwal penanaman
- **Manajemen Data**: Menyimpan dan mengelola data penanaman menggunakan Firebase

## 🛠️ Teknologi yang Digunakan

- **Kotlin**: Bahasa pemrograman utama
- **Jetpack Compose**: UI framework modern untuk Android
- **Firebase**: Backend services
  - Firebase Firestore: Database
  - Firebase Authentication: Autentikasi pengguna
- **Material Design 3**: Komponen UI modern
- **Navigation Compose**: Navigasi antar screen
- **Gradle**: Build system

## 📋 Persyaratan

- Android Studio Hedgehog atau yang lebih baru
- JDK 11 atau yang lebih baru
- Android SDK API 24 (Android 7.0) atau lebih tinggi
- Target SDK: API 36

## 🚀 Instalasi dan Setup

1. **Clone repository**
   ```bash
   git clone https://github.com/notataxpayer/agrifarm-mobile.git
   cd agrifarm-mobile
   ```

2. **Konfigurasi Firebase**
   - Buat project baru di [Firebase Console](https://console.firebase.google.com/)
   - Download file `google-services.json`
   - Letakkan file tersebut di folder `app/`

3. **Build project**
   ```bash
   ./gradlew build
   ```

4. **Run aplikasi**
   - Buka project di Android Studio
   - Pilih emulator atau device
   - Klik Run (atau tekan Shift+F10)

## 📁 Struktur Project

```
agrifarm_sitani/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/agrifarm_sitani/
│   │   │   │   ├── calculator/          # Logic perhitungan
│   │   │   │   ├── model/               # Data models
│   │   │   │   ├── navigation/          # Navigation graph
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screen/          # UI screens
│   │   │   │   │   └── theme/           # Theme & styling
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/                     # Resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/                        # Unit tests
│   ├── build.gradle.kts
│   └── google-services.json
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## ✨ Fitur Utama

### 1. Kalkulator Benih
- Input luas lahan
- Pemilihan jenis tanaman
- Perhitungan otomatis kebutuhan benih
- Rekomendasi jarak tanam

### 2. Kalender Tanam
- Penjadwalan penanaman
- Reminder jadwal tanam
- Tracking progress penanaman
- Riwayat penanaman

### 3. Manajemen Data
- Penyimpanan data di cloud (Firebase)
- Sinkronisasi real-time
- Backup otomatis

## 🔧 Dependencies

```kotlin
// Core Android
androidx.core:core-ktx
androidx.lifecycle:lifecycle-runtime-ktx
androidx.activity:activity-compose

// Jetpack Compose
androidx.compose.ui
androidx.compose.material3
androidx.compose.ui.tooling.preview
androidx.navigation:navigation-compose
androidx.compose.material:material-icons-extended

// Firebase
firebase-bom
firebase-firestore-ktx
firebase-auth-ktx

// Testing
junit
androidx.test
androidx.compose.ui.test
```

## 🧪 Testing

Menjalankan unit tests:
```bash
./gradlew test
```

Menjalankan instrumented tests:
```bash
./gradlew connectedAndroidTest
```
