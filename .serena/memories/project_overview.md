# Project Overview

## Purpose
This is an Android library project called "changelog-compose" that provides a Jetpack Compose library for displaying changelogs in Android applications. The library parses XML-formatted changelog files and presents them with a clean, Material 3 UI.

The project contains:
- **changelog**: The main library module (Android library)
- **app**: Sample application demonstrating library usage

## Library Module (changelog)

**Package**: `com.codingfeline.changelog`
**Main API**: `ChangelogContent` composable

### Key Features
- Parses XML changelog files from raw resources
- Displays changelogs with Material 3 UI
- Built-in loading and error states with retry
- Support for change types: FIX, NEW, BREAKING
- Customizable padding and labels
- Version grouping with optional dates

### Data Models
- **Changelog**: Contains list of releases
- **Release**: Version name, change date, and list of changes
- **ChangeItem**: Change text with optional type
- **ChangeType**: Enum (FIX, NEW, BREAKING)

### Components
- **ChangelogContent**: Main public API composable
- **ChangelogParser**: XML parser for changelog files
- **ChangelogViewModel**: State management
- **Internal UI**: Loading, error, and list composables

## Sample App Module (app)

**Package**: `com.codingfeline.changelog.sample`
**Application ID**: `com.codingfeline.changelog.sample`
**Version**: 1.0 (versionCode: 1)

Demonstrates how to integrate the changelog library.

## SDK Requirements
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 36
- **Compile SDK**: 36

## Technology Stack
- **Language**: Kotlin 2.0.21
- **Build System**: Gradle with Kotlin DSL (.gradle.kts)
- **UI Framework**: Jetpack Compose (BOM version: 2024.09.00)
- **Design System**: Material 3 (Material Design 3)
- **Java Version**: 11 (source & target compatibility)

## Key Dependencies

### Library Module
- androidx.core:core-ktx (1.17.0)
- androidx.lifecycle:lifecycle-viewmodel-ktx (2.9.4)
- androidx.lifecycle:lifecycle-viewmodel-compose
- androidx.compose:compose-bom (2024.09.00)
- androidx.compose.material3:material3
- kotlinx-coroutines-core (1.10.2)
- kotlinx-coroutines-android (1.10.2)

### Testing Dependencies
- JUnit (4.13.2)
- MockK (for unit testing)
- Coroutines Test
- AssertJ

## Build Configuration
- Version catalog-based dependency management (gradle/libs.versions.toml)
- Compose compiler plugin enabled via kotlin-compose plugin
- ProGuard optimization disabled in release builds (currently)
- Edge-to-edge UI implementation in sample app

## License
Apache License 2.0
