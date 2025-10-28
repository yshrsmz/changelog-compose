# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

<!-- OPENSPEC:START -->
## OpenSpec Instructions

These instructions are for AI assistants working in this project.

Always open `@/openspec/AGENTS.md` when the request:
- Mentions planning or proposals (words like proposal, spec, change, plan)
- Introduces new capabilities, breaking changes, architecture shifts, or big performance/security work
- Sounds ambiguous and you need the authoritative spec before coding

Use `@/openspec/AGENTS.md` to learn:
- How to create and apply change proposals
- Spec format and conventions
- Project structure and guidelines

Keep this managed block so 'openspec update' can refresh the instructions.

<!-- OPENSPEC:END -->


## Project Overview

This is an Android application project called "Changelog Sample" built with Jetpack Compose and Kotlin. The project appears to be a sample/demonstration app for a changelog-related feature or library.

- **Package**: `com.codingfeline.changelog.sample`
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Kotlin Version**: 2.0.21
- **Java Version**: 11

## Build Commands

Build and run the app:
```bash
./gradlew build
```

Install the app on a connected device/emulator:
```bash
./gradlew installDebug
```

Clean the build:
```bash
./gradlew clean
```

## Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests (requires connected device/emulator):
```bash
./gradlew connectedAndroidTest
```

Run a specific test:
```bash
./gradlew test --tests com.codingfeline.changelog.sample.ExampleUnitTest
```

## Project Structure

This is a single-module Android application:

- **app/**: Main application module
  - **src/main/java/**: Kotlin source files
    - `MainActivity.kt`: Main entry point, uses Jetpack Compose with edge-to-edge support
    - **ui/theme/**: Compose theme definitions (Color, Type, Theme)
  - **src/test/**: Unit tests
  - **src/androidTest/**: Instrumented tests
  - **build.gradle.kts**: Module-level build configuration

## Technology Stack

- **Jetpack Compose**: Modern declarative UI toolkit
- **Material 3**: Material Design 3 components
- **Compose BOM**: Bill of Materials for versioning Compose dependencies
- **Version Catalog**: Dependencies managed via `gradle/libs.versions.toml`

## Development Notes

- The project uses Kotlin's Gradle DSL (.gradle.kts files)
- Dependencies are centralized using Gradle version catalogs
- Compose compiler plugin is enabled via `kotlin-compose` plugin
- The app implements edge-to-edge UI with `enableEdgeToEdge()`
