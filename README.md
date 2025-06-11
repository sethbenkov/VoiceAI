# Pixel 5 Voice Assistant

A privacy-first voice assistant that runs on a Pixel 5 device, using offline wake word detection and speech-to-text, with OpenAI-powered responses.

## 🚀 Quick Start

1. Install Termux from F-Droid
2. Run these commands in Termux:

```bash
# Update and install dependencies
pkg update
pkg upgrade
pkg install python git termux-api

# Install Python packages
pip install -r requirements.txt

# Download Vosk model
mkdir -p ~/assistant/vosk-model
cd ~/assistant/vosk-model
wget https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip
unzip vosk-model-small-en-us-0.15.zip

# Set up your OpenAI API key
echo "your-api-key-here" > ~/.openai_key
chmod 600 ~/.openai_key
```

## 🛠️ Configuration

- OpenAI API key: `~/.openai_key`
- Model selection: `~/.assistant_model`
- Token usage logs: `~/assistant/tokens.log`

## 🎯 Features

- Offline wake word detection ("Hey Assistant")
- Offline speech-to-text
- OpenAI-powered responses
- Token usage tracking
- Model selection
- Privacy-focused (no data logging)

## 📝 Usage

1. Run the assistant:
```bash
python assistant.py
```

2. Say "Hey Assistant" to activate
3. Speak your question or command
4. Get AI-powered response

## 🔒 Security Notes

- All wake word detection and speech-to-text runs offline
- Only your prompts and responses are sent to OpenAI
- No conversation history is stored
- Token usage is logged locally only

## 🐛 Troubleshooting

If you encounter issues:

1. Check your OpenAI API key
2. Verify Vosk model is downloaded correctly
3. Ensure microphone permissions are granted
4. Check the logs in `~/assistant/tokens.log`

## 📚 Documentation

For more details, see the [PRD](pixel5_voice_assistant_prd.md).

## 🤝 Contributing

Feel free to submit issues and pull requests. Please follow the existing code style and add tests for new features.

## 📄 License

MIT License - See LICENSE file for details 