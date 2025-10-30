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

This is a multi-module Android project that provides a Jetpack Compose library for displaying changelogs in Android applications.

**Modules:**
- `:changelog` - The library module (publishable to Maven Central)
- `:app` - Sample application demonstrating the library usage

**Key Info:**
- **Group ID**: `com.codingfeline.changelog`
- **Artifact ID**: `changelog-compose`
- **Min SDK**: 24 (Android 7.0) for library, 26 (Android 8.0) for sample app
- **Target/Compile SDK**: 36
- **Kotlin**: 2.0.21
- **Java**: 11
- **Compose UI**: 1.9.4
- **Material3**: 1.4.0

## Build Commands

### Basic Build Tasks

```bash
# Build the entire project
./gradlew build

# Build only the library module
./gradlew :changelog:build

# Clean build
./gradlew clean
```

### Sample App

```bash
# Install the sample app on a connected device/emulator
./gradlew :app:installDebug

# Run the sample app
./gradlew :app:run
```

### Testing

```bash
# Run all unit tests
./gradlew test

# Run tests for the library module only
./gradlew :changelog:test

# Run instrumented tests (requires connected device)
./gradlew connectedAndroidTest

# Run a specific test
./gradlew test --tests com.codingfeline.changelog.ChangelogParserTest
```

### Publishing

```bash
# Publish to local Maven repository (for testing)
./gradlew :changelog:publishToMavenLocal

# Verify artifacts in ~/.m2/repository/com/codingfeline/changelog/changelog-compose/

# Generate POM file for inspection
./gradlew :changelog:generatePomFileForMavenPublication

# Publish to Maven Central (requires signing credentials)
./gradlew :changelog:publish
```

See `PUBLISHING.md` for complete publishing instructions.

## Project Structure

```
changelog-compose/
├── app/                          # Sample application
│   ├── src/main/java/
│   │   └── com/codingfeline/changelog/sample/
│   │       ├── MainActivity.kt   # Entry point with ChangelogContent demo
│   │       └── ui/theme/         # App theme
│   └── src/main/res/raw/
│       └── changelog.xml         # Sample changelog data
│
├── changelog/                    # Library module (published artifact)
│   ├── src/main/java/
│   │   └── com/codingfeline/changelog/
│   │       ├── ChangelogContent.kt       # Main public API
│   │       ├── ChangelogViewModel.kt     # State management
│   │       ├── ChangelogParser.kt        # XML parser
│   │       ├── models/
│   │       │   ├── Changelog.kt
│   │       │   ├── Release.kt
│   │       │   ├── ChangeItem.kt
│   │       │   └── ChangeType.kt
│   │       └── internal/         # Internal UI components
│   └── build.gradle.kts          # Library build config + Maven publishing
│
├── gradle/
│   └── libs.versions.toml        # Version catalog (centralized dependencies)
├── gradle.properties             # Maven publishing metadata + POM configuration
├── PUBLISHING.md                 # Complete Maven Central publishing guide
└── README.md                     # Library usage documentation
```

## Architecture

### Library Architecture

The `:changelog` module follows a clean architecture pattern:

1. **Public API Layer**
   - `ChangelogContent`: Main composable - the only public API users interact with
   - Provides loading/error/content states out-of-the-box

2. **ViewModel Layer**
   - `ChangelogViewModel`: Manages changelog loading state using Kotlin Coroutines
   - Handles error states and provides retry functionality

3. **Data Layer**
   - `ChangelogParser`: Parses XML from raw resources into domain models
   - Domain models: `Changelog`, `Release`, `ChangeItem`, `ChangeType`

4. **UI Layer (Internal)**
   - Internal composables for loading, error, and list display
   - Not exposed to library consumers

### XML Format

The library parses changelog XML files with this structure:

```xml
<changelog>
    <changelogversion versionName="1.0.0" changeDate="2024-10-28">
        <changelogtext type="new">New feature description</changelogtext>
        <changelogtext type="fix">Bug fix description</changelogtext>
        <changelogtext type="breaking">Breaking change</changelogtext>
        <changelogtext>General change (no type)</changelogtext>
    </changelogversion>
</changelog>
```

## Maven Publishing Configuration

The `:changelog` module uses `com.vanniktech.maven.publish` plugin (v0.30.0) for publishing.

**Key Configuration Files:**
- `changelog/build.gradle.kts` - Defines publishing setup with `AndroidSingleVariantLibrary`
- `gradle.properties` - Contains all POM metadata (GROUP, VERSION_NAME, POM_*, etc.)
- `local.properties` (gitignored) - Where signing credentials should be stored

**Publishing Flow:**
1. Artifacts are built from the `release` variant
2. Sources JAR and Javadoc JAR (via Dokka) are automatically generated
3. All publications are signed via GPG (configured with `signAllPublications()`)
4. Published to Sonatype Central Portal (new Maven Central endpoint)

**Important Notes:**
- All Compose dependencies use `implementation` scope (not `api`) - this is correct and follows industry standard
- POM metadata (licenses, developers, SCM) is automatically loaded from `gradle.properties`
- Do not manually specify licenses/developers in `build.gradle.kts` to avoid duplication

## Dependency Management

Dependencies are managed via Gradle version catalog in `gradle/libs.versions.toml`.

**When adding Compose dependencies to the library:**
- Use `implementation` for Compose UI libraries (e.g., `androidx.compose.ui`, `material3`)
- Only use `api` if you're exposing a type from that dependency in your public API
- The library already has proper Compose dependencies configured

**Compose Compiler:**
- Managed by `kotlin-compose` plugin
- Automatically kept in sync with Kotlin version

## Development Notes

### Compose Library Development

- The library module publishes only the `release` variant
- All Compose dependencies use `implementation` scope - consumers must declare their own Compose dependencies
- Runtime scope in Maven POM is correct for Compose libraries

### Version Catalog Usage

When adding dependencies, use the version catalog:

```kotlin
// In build.gradle.kts
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material3)
}
```

### ProGuard/R8

The library includes consumer ProGuard rules via:
```kotlin
consumerProguardFiles("consumer-rules.pro")
```

## Git Commit Convention

This project follows [Conventional Commits](https://www.conventionalcommits.org/) specification.

**Commit Message Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation only changes
- `style`: Code style changes (formatting, missing semi colons, etc)
- `refactor`: Code refactoring without changing functionality
- `perf`: Performance improvements
- `test`: Adding or updating tests
- `chore`: Changes to build process or auxiliary tools
- `ci`: Changes to CI configuration files and scripts

**Scope:**
- `changelog`: Changes to the `:changelog` library module
- `app`: Changes to the `:app` sample application
- Omit scope for project-wide changes

**Examples:**
```
feat(changelog): add dark mode support
fix(changelog): resolve XML parsing error for empty tags
docs: update README with installation instructions
chore: update dependencies to latest versions
```

**Pull Request Titles:**
- PR titles must also follow the conventional commit format
- Use the same format as commit messages: `type(scope): description`
- Example: `feat(changelog): lower minSdk to 24`

## Security Notes

- **Never commit** `local.properties` - it's in `.gitignore` for a reason
- Signing keys and Maven Central credentials belong in `local.properties` or environment variables
- For CI/CD, use in-memory signing with environment variables (see `PUBLISHING.md`)
