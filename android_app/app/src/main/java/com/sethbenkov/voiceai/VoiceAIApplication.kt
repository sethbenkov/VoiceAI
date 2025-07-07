package com.sethbenkov.voiceai

import android.app.Application

class VoiceAIApplication : Application() {
    // No longer instantiates AssistantService directly. Service is started and bound from MainActivity.
} 