# 🗣️ Pixel 5 Voice Assistant - Android App PRD

## Overview

This project creates a **privacy-first, voice-activated AI assistant** as a native Android app. It uses Android's native speech recognition and wake word detection, then sends prompts to the OpenAI API for intelligent responses.

Built as a proper Android app, it provides a better user experience than the Termux version, with proper background operation, battery management, and native UI components.

---

## 🎯 Goals

- Create a native Android app for the voice assistant
- Use Android's native speech recognition
- Support model selection and usage tracking
- Show token usage in a proper UI
- Run efficiently in the background
- Provide a polished user experience

---

## 🧩 Components

| Component         | Tool/Library     | Description |
|------------------|------------------|-------------|
| Wake Word        | Android's Voice Recognition API | Native hotword detection |
| Speech-to-Text   | Android's Speech Recognition API | Native STT |
| Assistant Engine | OpenAI API       | GPT-3.5 / GPT-4 |
| UI               | Material Design  | Native Android UI |
| Notifications    | Android Notifications | Native notifications |
| Model Management | Settings Screen  | Model selection UI |
| Token Tracking   | Room Database    | Local storage for usage |

---

## ✅ Functional Requirements

### FR1: Wake Word
- Use Android's native wake word detection
- Support custom wake word training
- Run efficiently in background

### FR2: Speech Recognition
- Use Android's native speech recognition
- Support multiple languages
- Handle network connectivity gracefully

### FR3: GPT-based Assistant
- Text is sent to OpenAI with user-selected model
- Responses shown in UI and read aloud
- Handle API errors gracefully

### FR4: Model Selection
- Settings screen for model selection
- Cache available models locally
- Show model capabilities and costs

### FR5: Token Usage Tracking
- Track tokens per model
- Show daily/weekly/monthly usage
- Export usage data

### FR6: UI Components
- Main screen with status and history
- Settings screen
- Token usage dashboard
- Response history

### FR7: Background Operation
- Run as a foreground service
- Handle battery optimization
- Resume after device restart

---

## 🔒 Security Requirements

- No prompt or response content is logged
- API key stored in Android Keystore
- Model names and tokens sanitized before storage
- No internet usage outside OpenAI API calls
- Proper permission handling

---

## 🛠️ Technical Architecture

### App Structure
```
app/
├── java/
│   ├── activities/
│   │   ├── MainActivity.kt
│   │   ├── SettingsActivity.kt
│   │   └── UsageActivity.kt
│   ├── services/
│   │   ├── AssistantService.kt
│   │   └── WakeWordService.kt
│   ├── database/
│   │   ├── AppDatabase.kt
│   │   └── entities/
│   ├── network/
│   │   └── OpenAIClient.kt
│   └── utils/
│       ├── SpeechRecognizer.kt
│       └── NotificationManager.kt
└── res/
    ├── layout/
    ├── values/
    └── drawable/
```

### Dependencies
```gradle
dependencies {
    // AndroidX
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    
    // Room for database
    implementation 'androidx.room:room-runtime:2.6.1'
    implementation 'androidx.room:room-ktx:2.6.1'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    
    // Retrofit for API calls
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Lifecycle components
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-service:2.7.0'
}
```

---

## 📱 UI/UX Design

### Main Screen
- Status indicator
- Recent conversations
- Quick settings
- Token usage summary

### Settings Screen
- Model selection
- Wake word configuration
- API key management
- Notification preferences

### Usage Dashboard
- Daily/weekly/monthly views
- Cost estimates
- Export options

---

## 🔄 Development Phases

### Phase 1: Core Functionality
- Basic app structure
- Speech recognition
- OpenAI integration
- Simple UI

### Phase 2: Enhanced Features
- Background service
- Token tracking
- Model selection
- Settings screen

### Phase 3: Polish
- UI improvements
- Performance optimization
- Battery optimization
- Testing and bug fixes

---

## 📊 Success Metrics

- Battery impact < 5% per hour
- Wake word detection accuracy > 95%
- Speech recognition accuracy > 90%
- App size < 50MB
- Cold start time < 2 seconds

---

## 🚀 Future Enhancements

- Custom wake word training
- Offline mode with local models
- Home screen widget
- Wear OS support
- Task automation integration

---

## 📌 Final Thoughts

This Android app version provides a much better user experience than the Termux version, with proper Android integration, better battery life, and a polished UI. It maintains the privacy-first approach while being more user-friendly and reliable. 