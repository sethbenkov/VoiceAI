package com.sethbenkov.voiceai

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.sethbenkov.voiceai.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var encryptedPrefs: EncryptedSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEncryptedPreferences()
        setupUI()
        loadSettings()
    }

    private fun setupEncryptedPreferences() {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        encryptedPrefs = EncryptedSharedPreferences.create(
            this,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    private fun setupUI() {
        binding.saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        binding.apiKeyInput.setText(encryptedPrefs.getString("api_key", ""))
    }

    private fun saveSettings() {
        val apiKey = binding.apiKeyInput.text.toString().trim()
        
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "API key cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        encryptedPrefs.edit().apply {
            putString("api_key", apiKey)
            apply()
        }

        // Update the service with new API key
        (application as? VoiceAIApplication)?.assistantService?.setApiKey(apiKey)

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
        finish()
    }
} 