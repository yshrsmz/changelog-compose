# Code Style and Conventions

## Kotlin Conventions

### Naming
- **Classes**: PascalCase (e.g., `MainActivity`, `ComponentActivity`)
- **Functions**: camelCase (e.g., `onCreate`, `enableEdgeToEdge`)
- **Composables**: PascalCase (e.g., `Greeting`, `ChangelogSampleTheme`)
- **Variables**: camelCase (e.g., `darkTheme`, `dynamicColor`, `colorScheme`)
- **Private properties**: camelCase with `val` (e.g., `val DarkColorScheme`)
- **Parameters**: camelCase (e.g., `name`, `modifier`)

### Compose Conventions

#### Composable Functions
- Use `@Composable` annotation
- PascalCase naming
- Default parameters for customization (e.g., `modifier: Modifier = Modifier`)
- Parameters order: content/data first, modifiers last

Example:
```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
```

#### Preview Functions
- Use `@Preview` annotation with parameters (e.g., `showBackground = true`)
- Named with "Preview" suffix (e.g., `GreetingPreview`)
- Wrap content in theme composable for consistency

Example:
```kotlin
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChangelogSampleTheme {
        Greeting("Android")
    }
}
```

### String Handling
- Use string interpolation with `$` for simple variables
- Example: `"Hello $name!"` instead of `"Hello " + name + "!"`

### Imports
- Organized alphabetically
- No wildcard imports
- Separate Android and Compose imports logically

### Code Formatting
- No explicit linting/formatting configuration found (no ktlint, detekt, or .editorconfig)
- Follows standard Kotlin and Android Studio conventions
- 4-space indentation (default)
- Opening braces on same line

### Theme Implementation
- Uses Material 3 design system
- Supports dark/light themes
- Implements dynamic color on Android 12+ (when `dynamicColor = true`)
- Theme composable wrapper pattern for consistent theming

### Edge-to-Edge UI
- Enable edge-to-edge with `enableEdgeToEdge()` in Activity
- Use `Scaffold` with `innerPadding` for safe content placement
- Apply padding modifiers to handle system bars

Example:
```kotlin
enableEdgeToEdge()
setContent {
    ChangelogSampleTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Content(modifier = Modifier.padding(innerPadding))
        }
    }
}
```

## Gradle Conventions
- Kotlin DSL for build files (.gradle.kts)
- Version catalog for dependency management
- Plugin aliases from version catalog (e.g., `alias(libs.plugins.android.application)`)
