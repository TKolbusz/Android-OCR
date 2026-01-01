# OCR App - Text Extraction from Camera
A simple Android app that uses your camera to extract text from images using Google's Gemini AI.

## What Does It Do?

1. Opens your phone's camera
2. Take a photo of any text
3. Extracts the text using AI
4. Shows the result in a popup 

## Setup Instructions

### 1. Install Android Studio

### 2. Clone/Open the Project

### 3. Setup Firebase

1. Go to https://console.firebase.google.com/
2. Setup Android project with package name: `io.tkolbusz.ocr`
3. Download `google-services.json` file and place the file in: `app/google-services.json`
4. Enable Vertex AI in Firebase Console

### 4. Sync Project

1. In Android Studio, click File → Sync Project with Gradle Files
2. Connect developer phone to computer via USB
3. Run the app via Android Studio

## Running Tests

**Unit Tests:**
```bash
./gradlew test
```

**UI Tests (device/emulator required):**
```bash
./gradlew connectedAndroidTest
```

## Technologies Used

- **Kotlin** 
- **CameraX**
- **Firebase Vertex AI** - Gemini 2.5 Flash Lite for OCR
- **Material Design**
- **Coroutines** 
- **ViewBinding** 
### Tests
- **JUnit**
- **Mockito** 
- **Espresso** 
- **Robolectric**


<img width="723" height="1405" alt="Image" src="https://github.com/user-attachments/assets/6050a415-a24f-4c0d-8dc6-538d096fe1d7" />
