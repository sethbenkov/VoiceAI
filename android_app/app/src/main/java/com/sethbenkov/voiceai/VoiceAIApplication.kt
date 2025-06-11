package com.sethbenkov.voiceai

import android.app.Application
import com.sethbenkov.voiceai.services.AssistantService

class VoiceAIApplication : Application() {
    lateinit var assistantService: AssistantService
        private set

    override fun onCreate() {
        super.onCreate()
        assistantService = AssistantService(this)
    }
} 