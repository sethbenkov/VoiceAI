# VoiceAI - Privacy-First Voice Assistant

A privacy-focused voice assistant Android app that uses local speech recognition and OpenAI's API for natural language processing. Built specifically for Pixel 5 devices.

## Features

- 🎤 Local speech recognition using Android's native SpeechRecognizer
- 🤖 OpenAI GPT-3.5 integration for natural language understanding
- 🔒 Secure API key storage using Android's EncryptedSharedPreferences
- 📱 Modern Material Design UI
- 📊 Token usage tracking
- 🔄 Background processing for API calls

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

1. Click the "Start" button to begin voice recognition
2. Speak your query clearly
3. The app will:
   - Transcribe your speech
   - Send the text to OpenAI
   - Display the response in the main text area
4. Click "Start" again to stop listening

## Security Features

- API key is stored using Android's EncryptedSharedPreferences
- No data is stored locally except for the encrypted API key
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
│   │   │   │       └── AssistantService.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   └── drawable/
│   │   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

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