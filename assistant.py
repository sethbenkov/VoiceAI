#!/usr/bin/env python3
"""
Pixel 5 Voice Assistant
-----------------------
This script implements a privacy-first voice assistant that runs on a Pixel 5 device.
It uses offline wake word detection and Google's Speech Recognition, then connects to OpenAI for responses.

Author: Seth Benkov
Date: 2025
"""

import os
import json
import logging
from datetime import datetime
from pathlib import Path
from typing import Optional, Dict, Any

# Third-party imports
import openai
import speech_recognition as sr
import pvporcupine
import pyaudio

# Configure logging - because we're not savages
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

class VoiceAssistant:
    """
    Main voice assistant class that handles wake word detection, speech recognition,
    and OpenAI interactions. Written with the assumption that the maintainer might
    be a psychopath who knows where I live.
    """
    
    def __init__(self):
        """Initialize the voice assistant with all necessary components."""
        self.setup_paths()
        self.load_config()
        self.initialize_components()
        
    def setup_paths(self) -> None:
        """Set up all necessary file paths and ensure they exist."""
        self.base_dir = Path.home() / "assistant"
        self.token_log = self.base_dir / "tokens.log"
        self.model_cache = self.base_dir / "models_cache.json"
        self.model_file = self.base_dir / ".assistant_model"
        self.api_key_file = self.base_dir / ".openai_key"
        
        # Create directories if they don't exist
        self.base_dir.mkdir(exist_ok=True)
        
    def load_config(self) -> None:
        """Load configuration and API keys."""
        # Load OpenAI API key
        if not self.api_key_file.exists():
            raise FileNotFoundError(
                "OpenAI API key not found. Please create ~/.openai_key with your API key."
            )
        
        with open(self.api_key_file, "r") as f:
            openai.api_key = f.read().strip()
            
        # Load or create model selection
        if not self.model_file.exists():
            self.select_model()
            
        with open(self.model_file, "r") as f:
            self.current_model = f.read().strip()
            
    def initialize_components(self) -> None:
        """Initialize all the voice processing components."""
        try:
            # Initialize Porcupine for wake word detection
            self.porcupine = pvporcupine.create(
                access_key=os.getenv("PICOVOICE_ACCESS_KEY"),
                keywords=["hey assistant"]
            )
            
            # Initialize Speech Recognition
            self.recognizer = sr.Recognizer()
            
            # Initialize PyAudio
            self.audio = pyaudio.PyAudio()
            self.stream = self.audio.open(
                format=pyaudio.paInt16,
                channels=1,
                rate=16000,
                input=True,
                frames_per_buffer=1024
            )
            
        except Exception as e:
            logger.error(f"Failed to initialize components: {e}")
            raise
            
    def select_model(self) -> None:
        """
        Let the user select which OpenAI model to use.
        Because we respect the PM's vision but also want to make it better.
        """
        try:
            models = openai.Model.list()
            available_models = [
                model["id"] for model in models["data"]
                if model["id"].startswith("gpt-")
            ]
            
            # Save to cache for future use
            with open(self.model_cache, "w") as f:
                json.dump(available_models, f)
                
            # TODO: Implement termux-dialog for model selection
            # For now, default to gpt-3.5-turbo
            selected_model = "gpt-3.5-turbo"
            
            with open(self.model_file, "w") as f:
                f.write(selected_model)
                
        except Exception as e:
            logger.error(f"Failed to select model: {e}")
            raise
            
    def log_tokens(self, model: str, token_count: int) -> None:
        """
        Log token usage to the token log file.
        Because we're not savages who don't track costs.
        """
        timestamp = datetime.now().strftime("%Y-%m-%d")
        with open(self.token_log, "a") as f:
            f.write(f"{timestamp} {model} {token_count}\n")
            
    def process_audio(self) -> Optional[str]:
        """
        Process audio input and return transcribed text.
        Returns None if no speech detected.
        """
        try:
            with sr.Microphone() as source:
                logger.info("Listening...")
                audio = self.recognizer.listen(source)
                text = self.recognizer.recognize_google(audio)
                return text.strip()
        except sr.UnknownValueError:
            logger.info("Could not understand audio")
            return None
        except sr.RequestError as e:
            logger.error(f"Could not request results; {e}")
            return None
        except Exception as e:
            logger.error(f"Error processing audio: {e}")
            return None
            
    def get_ai_response(self, prompt: str) -> str:
        """
        Get response from OpenAI API.
        Includes token tracking because we're not savages.
        """
        try:
            response = openai.ChatCompletion.create(
                model=self.current_model,
                messages=[{"role": "user", "content": prompt}]
            )
            
            # Log token usage
            self.log_tokens(
                self.current_model,
                response.usage.total_tokens
            )
            
            return response.choices[0].message.content
            
        except Exception as e:
            logger.error(f"Error getting AI response: {e}")
            return "I'm sorry, I encountered an error processing your request."
            
    def run(self) -> None:
        """
        Main loop for the voice assistant.
        Written with the assumption that the maintainer might be a psychopath.
        """
        logger.info("Starting voice assistant...")
        
        try:
            while True:
                # Check for wake word
                pcm = self.stream.read(self.porcupine.frame_length)
                pcm = struct.unpack_from("h" * self.porcupine.frame_length, pcm)
                
                keyword_index = self.porcupine.process(pcm)
                if keyword_index >= 0:
                    logger.info("Wake word detected!")
                    
                    # Process speech
                    text = self.process_audio()
                    if text:
                        logger.info(f"Transcribed: {text}")
                        
                        # Get AI response
                        response = self.get_ai_response(text)
                        logger.info(f"AI Response: {response}")
                        
                        # TODO: Implement TTS output
                        print(response)
                        
        except KeyboardInterrupt:
            logger.info("Shutting down...")
        finally:
            self.cleanup()
            
    def cleanup(self) -> None:
        """Clean up resources because we're not savages."""
        self.stream.stop_stream()
        self.stream.close()
        self.audio.terminate()
        self.porcupine.delete()

if __name__ == "__main__":
    try:
        assistant = VoiceAssistant()
        assistant.run()
    except Exception as e:
        logger.error(f"Fatal error: {e}")
        raise 