# Suggested Commands

## Build Commands

### Build the project
```bash
./gradlew build
```

### Clean build artifacts
```bash
./gradlew clean
```

### Clean and rebuild
```bash
./gradlew clean build
```

## Installation Commands

### Install debug APK on connected device/emulator
```bash
./gradlew installDebug
```

### Build and install debug APK
```bash
./gradlew installDebug
```

## Testing Commands

### Run all unit tests
```bash
./gradlew test
```

### Run instrumented tests (requires device/emulator)
```bash
./gradlew connectedAndroidTest
```

### Run specific test
```bash
./gradlew test --tests com.codingfeline.changelog.sample.ExampleUnitTest
```

### Run all tests (unit + instrumented)
```bash
./gradlew test connectedAndroidTest
```

## Development Commands

### List all tasks
```bash
./gradlew tasks
```

### List all project dependencies
```bash
./gradlew dependencies
```

### Check for dependency updates
```bash
./gradlew dependencyUpdates
```
(Note: Requires dependency-updates plugin)

## Verification Commands

### Assemble debug APK (without installing)
```bash
./gradlew assembleDebug
```

### Assemble release APK
```bash
./gradlew assembleRelease
```

## System Commands (macOS/Darwin)

### Git operations
```bash
git status
git add .
git commit -m "message"
git push
git pull
```

### File operations
```bash
ls -la                  # List files with details
find . -name "*.kt"    # Find Kotlin files
grep -r "text" .       # Search for text in files
cd directory           # Change directory
```

### Process management
```bash
ps aux | grep java     # Find Java processes
kill -9 PID           # Kill process
```

## Notes
- All Gradle commands should be run from the project root
- Use `./gradlew` on macOS/Linux, `gradlew.bat` on Windows
- Instrumented tests require a connected Android device or running emulator
- No linting or formatting commands configured (no ktlint/detekt setup currently)
