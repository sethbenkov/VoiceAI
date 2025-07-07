# VoiceAI - Privacy-First Voice Assistant

A privacy-focused voice assistant Android app that uses local speech recognition and OpenAI's API for natural language processing. Built specifically for Pixel 5 devices.

## Features

- 🎤 Local speech recognition using Android's native SpeechRecognizer
- 🤖 OpenAI GPT-3.5 integration for natural language understanding
- 🔒 Secure API key storage using Android's EncryptedSharedPreferences
- 📱 Modern Material Design UI
- 📊 Token usage tracking with Room database (see below)
- 🔄 Persistent background/foreground service for voice commands
- 🟢 Improved listening UI: toggle button, auto-stop on silence
- ⚠️ Robust error handling with user-friendly messages
- 🚧 (Planned) Wake word detection for hands-free activation

## Prerequisites

- Android Studio (latest version)
- JDK 11 or higher
- OpenAI API key
- Pixel 5 device (or compatible Android device)

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/VoiceAI.git
   cd VoiceAI
   ```

2. Open the project in Android Studio:
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `android_app` directory and open it
   - Wait for the project to sync and download dependencies

3. Connect your device:
   - Enable USB debugging on your Pixel 5
   - Connect your device via USB
   - Allow USB debugging when prompted

4. Run the app:
   - Click the "Run" button (green play icon) in Android Studio
   - Select your device from the list
   - Wait for the app to install and launch

## First-time Setup

1. Launch the app
2. Click the "Settings" button
3. Enter your OpenAI API key
4. Click "Save"
5. Return to the main screen

## Usage

1. Use the **toggle button** to start/stop voice recognition. The button clearly shows whether the app is listening.
2. Speak your query clearly. The app will auto-stop listening after 5 seconds of silence.
3. The app will:
   - Transcribe your speech
   - Send the text to OpenAI
   - Display the response in the main text area
   - Show a persistent notification while running in the background
4. Errors (network, API, recognition, etc.) are shown as Toasts and in the UI with clear messages.

## Token Usage Tracking

- The app tracks the number of tokens used in each OpenAI API call (prompt, completion, total).
- Token usage is stored locally using Room database for later analysis.
- (Developers) See the `Usage` entity, `UsageDao`, and `AppDatabase` in the `services` package for details.

## Security Features

- API key is stored using Android's EncryptedSharedPreferences
- No sensitive data is stored locally except for the encrypted API key and token usage stats
- All API calls are made over HTTPS
- Minimal permissions required (Internet and Microphone)

## Development

The project is structured as follows:

```
android_app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sethbenkov/voiceai/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── SettingsActivity.kt
│   │   │   │   ├── VoiceAIApplication.kt
│   │   │   │   └── services/
│   │   │   │       ├── AssistantService.kt
│   │   │   │       ├── Usage.kt
│   │   │   │       ├── UsageDao.kt
│   │   │   │       └── AppDatabase.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   └── drawable/
│   │   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## Planned Features

- **Wake word detection**: Hands-free activation using Android's HotwordDetector API (in progress)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- OpenAI for providing the GPT API
- Android team for the SpeechRecognizer API
- Material Design team for the UI components 