
# 🗣️ Pixel 5 Offline + Online Voice Assistant PRD & Setup Guide

## Overview

This project turns a spare Pixel 5 into a **privacy-first, voice-activated AI assistant**. It runs fully offline for wake word detection and transcription, then sends prompts to the OpenAI API for intelligent responses.

Built entirely using **free and open source tools**, the assistant is modular, extendable, and tracks token usage per model for cost awareness.

---

## 🎯 Goals

- Run a voice assistant on an old Android device (Pixel 5).
- Use open-source offline tools for privacy and performance.
- Support model selection and usage tracking.
- Show token usage notifications.
- Use only free / open-source tools, and sideload if needed.

---

## 🧩 Components

| Component         | Tool/Library     | Description |
|------------------|------------------|-------------|
| Shell            | Termux           | Linux CLI on Android |
| Wake Word        | Porcupine (Picovoice) | Local hotword engine (e.g. "Hey Assistant") |
| Speech-to-Text   | Vosk             | Offline STT |
| Assistant Engine | OpenAI API       | GPT-3.5 / GPT-4 /
| UI               | termux-dialog    | Android-native popups |
| Notifications    | termux-notification | Token summaries |
| Model Management | Custom Python    | Model dropdown GUI |
| Token Tracking   | Flat file logs   | CSV-like logs for usage |

---

## ✅ Functional Requirements

### FR1: Wake Word
- Wake word detected offline using Porcupine.
- Initiates STT recording.

### FR2: Offline Transcription
- Use Vosk to convert speech to text.
- Fast and accurate enough for assistant prompts.

### FR3: GPT-based Assistant
- Text is sent to OpenAI with user-selected model.
- Receives and speaks back the response (via TTS or print).

### FR4: Inline Model Selector
- User is prompted via `termux-dialog` to choose model (`gpt-*`).
- Options populated from OpenAI API.
- Saves model in `~/.assistant_model`.

### FR5: Token Usage Tracker
- Tracks `total_tokens` returned from API calls.
- Logs to `tokens.log` as: `YYYY-MM-DD MODEL_NAME TOKEN_COUNT`

### FR6: Token Summary Notification
- Shows total tokens used for the current model **today**.

### FR7: Offline-Only Option
- Wake word and STT can run without OpenAI key for offline testing.

---

## 🔒 Security Requirements

- No prompt or response content is logged.
- API key stored in `~/.openai_key` (user created manually).
- Model names and tokens sanitized before logging.
- No internet usage outside OpenAI API calls.

---

## 🛠️ File Structure

```
~/assistant/
├── assistant.py              # Main script
├── model_menu.py             # Standalone selector (optional)
├── tokens.log                # Token log
├── .assistant_model          # Saved model (e.g. gpt-3.5-turbo)
├── .openai_key               # Your OpenAI API key
├── models_cache.json         # Cached OpenAI model list
```

---

## 🧪 Installation Guide

### 1. Set Up Termux

```bash
pkg update
pkg upgrade
pkg install python git termux-api
pip install openai vosk
```

### 2. Download Vosk Models

```bash
mkdir -p ~/assistant/vosk-model
cd ~/assistant/vosk-model
wget https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip
unzip vosk-model-small-en-us-0.15.zip
```

### 3. Save Your OpenAI API Key

```bash
echo "sk-..." > ~/.openai_key
chmod 600 ~/.openai_key
```

### 4. Clone and Set Up the Project

```bash
mkdir -p ~/assistant
cd ~/assistant
# Place scripts here (see below)
```

---

## 💻 Main Script (`assistant.py`)

Handles model selection, token tracking, and OpenAI API use.
_(Code previously provided in conversation)_

---

## 🖼️ GUI Model Selector (`termux-dialog`)

```python
# Embedded inline in assistant.py
# Launches termux-dialog dropdown
```

---

## 📢 Token Notification

Uses:
```bash
termux-notification --title "Model: gpt-4" --content "Tokens used today: 2312"
```

---

## ✅ Optional Enhancements

- Add TTS output using `termux-tts-speak`.
- Use `termux-wake-lock` to keep assistant always-on.
- Add a Termux widget shortcut to launch the assistant with a tap.

---

## 🔐 Security Recap

- Only home WiFi.
- No sensitive data logged.
- Local-only except OpenAI call.
- Minimal storage of data: model name and token count only.

---

## 📌 Final Thoughts

This assistant makes your old phone smart again — and **fully yours**. You're not tied to Big Tech silos or locked platforms. You can tweak, swap models, or run fully offline.

Enjoy building it — and let me know when you want to turn this into a GUI app or extend it to home automation!

