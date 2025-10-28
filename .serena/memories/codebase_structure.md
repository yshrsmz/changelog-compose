# Codebase Structure

## Project Layout
This is a multi-module Android library project with the following structure:

```
changelog-compose/
├── changelog/                                    # Library module (Android library)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/codingfeline/changelog/
│   │   │   │   ├── ChangelogContent.kt          # Main public API
│   │   │   │   └── internal/
│   │   │   │       ├── parser/
│   │   │   │       │   └── ChangelogParser.kt   # XML parser
│   │   │   │       ├── ui/
│   │   │   │       │   ├── ChangelogModels.kt   # Data models
│   │   │   │       │   ├── ChangelogList.kt     # List UI
│   │   │   │       │   ├── ChangelogSupportingScreens.kt # Loading/Error UI
│   │   │   │       │   └── ChangeTypeStyle.kt   # Styling
│   │   │   │       └── viewmodel/
│   │   │   │           └── ChangelogViewModel.kt # State management
│   │   │   └── res/                             # Android resources
│   │   └── test/                                # Unit tests
│   │       └── java/com/codingfeline/changelog/
│   │           └── ChangelogViewModelTest.kt
│   ├── build.gradle.kts                         # Library build config
│   └── consumer-rules.pro                       # Consumer ProGuard rules
├── app/                                         # Sample application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/codingfeline/changelog/sample/
│   │   │   │   ├── MainActivity.kt              # Main entry point
│   │   │   │   └── ui/theme/                    # UI theme definitions
│   │   │   │       ├── Color.kt                 # Color definitions
│   │   │   │       ├── Theme.kt                 # Theme composable
│   │   │   │       └── Type.kt                  # Typography definitions
│   │   │   ├── res/                             # Android resources
│   │   │   │   └── raw/                         # (Place changelog.xml here)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                                # Unit tests
│   │   │   └── java/com/codingfeline/changelog/sample/
│   │   │       └── ExampleUnitTest.kt
│   │   └── androidTest/                         # Instrumented tests
│   │       └── java/com/codingfeline/changelog/sample/
│   │           └── ExampleInstrumentedTest.kt
│   ├── build.gradle.kts                         # App module build config
│   └── proguard-rules.pro                       # ProGuard rules
├── gradle/
│   ├── libs.versions.toml                       # Version catalog
│   └── wrapper/
├── build.gradle.kts                              # Root build config
├── settings.gradle.kts                           # Project settings
├── gradle.properties                             # Gradle properties
├── README.md                                     # Project documentation
├── LICENSE                                       # Apache 2.0 License
├── CLAUDE.md                                     # Claude Code instructions
└── AGENTS.md                                     # OpenSpec instructions

```

## Key Components

### Library Module (changelog)

#### Public API
- **ChangelogContent.kt** (changelog/src/main/java/com/codingfeline/changelog/ChangelogContent.kt:20)
  - Main composable function for displaying changelog
  - Parameters: changelogResId, onRetry, modifier, contentPadding, retryLabel

#### Internal Components (not part of public API)

**Parser**:
- **ChangelogParser.kt**: Parses XML changelog files using XmlPullParser

**Data Models**:
- **Changelog**: Root data class containing list of releases
- **Release**: Version info (versionName, changeDate) and list of changes
- **ChangeItem**: Individual change with text and optional type
- **ChangeType**: Enum (FIX, NEW, BREAKING)

**UI Components**:
- **ChangelogList.kt**: Displays list of releases and changes
- **ChangelogSupportingScreens.kt**: Loading and error screens
- **ChangeTypeStyle.kt**: Styling for different change types

**State Management**:
- **ChangelogViewModel.kt**: Manages loading, error, and data state

### Sample App Module (app)

#### MainActivity.kt
- Main activity extending ComponentActivity
- Implements edge-to-edge UI with `enableEdgeToEdge()`
- Uses Compose with `setContent { }`
- Contains `Greeting` composable and preview

#### Theme Package (ui/theme/)
- **Theme.kt**: Main theme composable with dark/light/dynamic color support
- **Color.kt**: Color palette definitions
- **Type.kt**: Typography system definitions

## Package Organization

### Library Module
- Main package: `com.codingfeline.changelog`
- Internal packages (not exposed):
  - `com.codingfeline.changelog.internal.parser`
  - `com.codingfeline.changelog.internal.ui`
  - `com.codingfeline.changelog.internal.viewmodel`

### Sample App
- Main package: `com.codingfeline.changelog.sample`
- Theme package: `com.codingfeline.changelog.sample.ui.theme`

## XML Format for Changelogs

Changelog XML files should be placed in `app/src/main/res/raw/`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<changelog>
    <changelogversion versionName="1.1.0" changeDate="2024-10-28">
        <changelogtext type="new">New feature</changelogtext>
        <changelogtext type="fix">Bug fix</changelogtext>
        <changelogtext type="breaking">Breaking change</changelogtext>
        <changelogtext>General change</changelogtext>
    </changelogversion>
</changelog>
```
