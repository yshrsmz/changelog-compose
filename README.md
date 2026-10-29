# changelog-compose

A Jetpack Compose library for displaying changelogs in Android applications. This library parses XML-formatted changelog files and presents them with a clean, Material 3 UI.

<p align="center">
  <img height="600" src="./assets/screenshot.png">
</p>

## Features

- **Jetpack Compose UI**: Modern declarative UI built with Compose and Material 3
- **XML Parser**: Reads changelog from XML files in your `res/raw` resources
- **Change Types**: Support for categorized changes (FIX, NEW, BREAKING)
- **Built-in States**: Automatic handling of loading and error states with retry
- **Customizable**: Configurable padding and error-state labels
- **Version Grouping**: Organizes changes by release version with optional dates

## Requirements

- **Min SDK**: 24 (Android 7.0)
- **Kotlin**: 2.0.21+
- **Jetpack Compose**: UI 1.9.4+, Material3 1.4.0+

## Installation

Add the dependency to your app's `build.gradle.kts`:

<!-- x-release-please-start-version -->
```kotlin
dependencies {
    implementation("com.codingfeline.changelog:changelog-compose:1.0.0")
}
```

Or using version catalog in `gradle/libs.versions.toml`:

```toml
[versions]
changelog-compose = "1.0.0"

[libraries]
changelog-compose = { module = "com.codingfeline.changelog:changelog-compose", version.ref = "changelog-compose" }
```
<!-- x-release-please-end -->

Then in your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.changelog.compose)
}
```

## Usage

### 1. Create a Changelog XML File

Create an XML file in `app/src/main/res/raw/` (e.g., `changelog.xml`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<changelog>
    <changelogversion versionName="1.1.0" changeDate="2024-10-28">
        <changelogtext type="new">Added dark mode support</changelogtext>
        <changelogtext type="new">Added settings screen</changelogtext>
        <changelogtext type="fix">Fixed crash on startup</changelogtext>
    </changelogversion>

    <changelogversion versionName="1.0.0" changeDate="2024-10-01">
        <changelogtext>Initial release</changelogtext>
        <changelogtext type="new">User authentication</changelogtext>
        <changelogtext type="new">Main dashboard</changelogtext>
    </changelogversion>
</changelog>
```

### 2. Add the Composable

Use the `ChangelogContent` composable in your app:

```kotlin
import com.codingfeline.changelog.ChangelogContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChangelogScreen() {
    ChangelogContent(
        changelogResId = R.raw.changelog,
        modifier = Modifier.fillMaxSize()
    )
}
```

The composable automatically handles:
- **Loading state**: Shows a loading indicator while parsing the XML
- **Error state**: Displays error message with retry button
- **Success state**: Presents the changelog in a scrollable list

### Edge-to-Edge Support

When using `ChangelogContent` inside a `Scaffold`, pass the scaffold's `innerPadding` as
`contentPadding` (instead of `Modifier.padding(innerPadding)`) so the list content scrolls
behind the system bars:

```kotlin
Scaffold { innerPadding ->
    ChangelogContent(
        changelogResId = R.raw.changelog,
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding
    )
}
```

`contentPadding` defaults to `PaddingValues(16.dp)`. Passing a value replaces the default
padding entirely, so combine it with your own padding if needed.

### Customizing Labels

The texts shown when loading fails (the retry button and the error messages) can be replaced through `labels`, e.g. with localized strings from your resources:

```kotlin
ChangelogContent(
    changelogResId = R.raw.changelog,
    labels = ChangelogLabels(
        retry = stringResource(R.string.changelog_retry),
        errorResourceNotFound = stringResource(R.string.changelog_error_not_found),
        errorInvalidFormat = stringResource(R.string.changelog_error_invalid_format),
        errorReadFailed = stringResource(R.string.changelog_error_read_failed)
    )
)
```

The `ChangelogLabels` constructor requires every text, so a forgotten text (or one added in a later version of the library) is a compile error instead of silently showing up in English. Without `labels`, `ChangelogContent` uses `ChangelogLabels.Default`, the built-in English texts.

To replace only some of the texts and keep the English texts for the rest, use `copy` on `ChangelogLabels.Default`. Texts added in a later version of the library then also fall back to English.

```kotlin
labels = ChangelogLabels.Default.copy(
    retry = stringResource(R.string.changelog_retry)
)
```

For `errorInvalidFormat` and `errorReadFailed`, the underlying error message is appended as `"<text>: <message>"` when available. The change type badges (`NEW`, `FIX`, `BREAKING`) are not customizable.

### Testing with a Custom Dispatcher

The changelog XML is read and parsed on `ioDispatcher`, which defaults to `Dispatchers.IO`. In Compose UI tests, pass a `TestDispatcher` from `kotlinx-coroutines-test` so the test can synchronize with the load instead of waiting in real time. With an `UnconfinedTestDispatcher`, the changelog is loaded by the time `waitForIdle()` returns:

```kotlin
@Test
fun changelogIsDisplayed() {
    composeTestRule.setContent {
        ChangelogContent(
            changelogResId = R.raw.changelog,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    composeTestRule.onNodeWithText("v1.0.0").assertIsDisplayed()
}
```

This relies on the view model resuming on the real main dispatcher. If your test replaces `Dispatchers.Main` with a `StandardTestDispatcher` (e.g. through a `MainDispatcherRule`), advance that dispatcher's scheduler as well before asserting. To check the loading state first, pass a `StandardTestDispatcher` and advance its scheduler after the assertion. The dispatcher is captured when `ChangelogContent` first creates its view model, so changing it afterwards has no effect.

## XML Format

### Root Element

```xml
<changelog>
    <!-- Release versions go here -->
</changelog>
```

### Version Element

```xml
<changelogversion
    versionName="1.0.0"    <!-- Required: Version number -->
    changeDate="2024-10-28"> <!-- Optional: Release date -->
    <!-- Changes go here -->
</changelogversion>
```

### Change Text Element

```xml
<changelogtext type="new">
    Description of the change
</changelogtext>
```

#### Supported Types

- `type="new"` - New features (default styling)
- `type="fix"` - Bug fixes
- `type="breaking"` - Breaking changes
- No type attribute - General changes

## Architecture

The library consists of:

- **ChangelogContent**: Main composable API
- **ChangelogParser**: XML parser that reads from raw resources
- **ChangelogViewModel**: Manages loading state and error handling
- **Data Models**: `Changelog`, `Release`, `ChangeItem`, `ChangeType`
- **Internal UI**: Composables for loading, error, and list display

## Dependencies

The library uses:

- Jetpack Compose (Material 3)
- Lifecycle ViewModel Compose
- Kotlin Coroutines
- AndroidX Core KTX

## Sample App

The `app` module demonstrates how to integrate the changelog library in your application.

## License

```
Copyright 2025 Yasuhiro Shimizu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

