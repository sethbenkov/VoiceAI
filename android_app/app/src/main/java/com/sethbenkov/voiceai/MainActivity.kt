package com.sethbenkov.voiceai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sethbenkov.voiceai.databinding.ActivityMainBinding
import com.sethbenkov.voiceai.services.AssistantService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Build
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ToggleButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListening = false
    private var assistantService: AssistantService? = null
    private var isServiceBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? AssistantService.LocalBinder
            assistantService = binder?.getService()
            isServiceBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            assistantService = null
            isServiceBound = false
        }
    }
    private val silenceTimeoutMillis = 5000L // 5 seconds of silence
    private val silenceHandler = Handler(Looper.getMainLooper())
    private val silenceRunnable = Runnable {
        stopListening()
        showToast("Stopped listening due to silence.")
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpeechRecognizer()
        setupUI()
        checkPermissions()

        // Start the AssistantService as a foreground service
        val serviceIntent = Intent(this, AssistantService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent)
        } else {
            startService(serviceIntent)
        }
        // Bind to the service
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                binding.statusText.text = "Listening..."
                isListening = true
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    binding.userInputText.text = text
                    // Show loading indicator
                    binding.statusText.text = "Thinking..."
                    // Use the bound AssistantService instance
                    if (assistantService != null && isServiceBound) {
                        lifecycleScope.launch {
                            val result = assistantService!!.getResponse(text)
                            if (result.isSuccess) {
                                binding.userInputText.text = result.getOrNull()
                                binding.statusText.text = "Ready"
                                binding.assistantResponseText.text = result.getOrNull()
                            } else {
                                binding.userInputText.text = "Error: ${result.exceptionOrNull()?.localizedMessage ?: "Unknown error"}"
                                binding.statusText.text = "Error"
                                showToast("Assistant error: ${result.exceptionOrNull()?.localizedMessage ?: "Unknown error"}")
                            }
                        }
                    } else {
                        binding.userInputText.text = "Service not available"
                        binding.statusText.text = "Error"
                        showToast("Assistant service not available")
                    }
                }
                isListening = false
                // Don't set statusText here, coroutine will update it
                // Remove silence timeout
                silenceHandler.removeCallbacks(silenceRunnable)
                // Reset toggle button
                binding.listenToggleButton.isChecked = false
            }

            override fun onError(error: Int) {
                isListening = false
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error: $error"
                }
                binding.statusText.text = "Error: $errorMsg"
                showToast("Speech recognition error: $errorMsg")
                // Remove silence timeout
                silenceHandler.removeCallbacks(silenceRunnable)
                // Reset toggle button
                binding.listenToggleButton.isChecked = false
            }

            // Required override methods
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun setupUI() {
        binding.listenToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startListening()
            } else {
                stopListening()
            }
        }

        binding.settingsButton.setOnClickListener {
            // Open SettingsActivity when the settings button is clicked
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.usageButton.setOnClickListener {
            val intent = Intent(this, UsageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechRecognizer.startListening(intent)
        // Start silence timeout
        silenceHandler.postDelayed(silenceRunnable, silenceTimeoutMillis)
    }

    private fun stopListening() {
        speechRecognizer.stopListening()
        isListening = false
        binding.statusText.text = "Ready"
        // Reset toggle button
        binding.listenToggleButton.isChecked = false
        // Remove silence timeout
        silenceHandler.removeCallbacks(silenceRunnable)
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions required for app functionality", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        // Unbind from the service
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
} 