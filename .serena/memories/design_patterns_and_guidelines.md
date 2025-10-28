# Design Patterns and Guidelines

## Jetpack Compose Patterns

### Component Structure
This project follows standard Jetpack Compose patterns:

1. **Stateless Composables**: Composables receive data through parameters and events through callbacks
2. **Theming**: Wrap content in `ChangelogSampleTheme` for consistent styling
3. **Preview Pattern**: Every composable should have a corresponding `@Preview` function

### Edge-to-Edge UI Pattern
The app implements edge-to-edge UI:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Enable edge-to-edge
        setContent {
            ChangelogSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Use innerPadding to handle system bars
                    Content(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
```

**Key Points**:
- Call `enableEdgeToEdge()` before `setContent`
- Use `Scaffold` to manage system bar insets
- Apply `innerPadding` to content to avoid overlap with system UI

### Material 3 Theming

The project uses Material 3 with the following approach:

1. **Color Schemes**: Separate dark and light color schemes
2. **Dynamic Color**: Supports Android 12+ dynamic theming
3. **Theme Composable**: Centralized `ChangelogSampleTheme` for consistency

```kotlin
@Composable
fun ChangelogSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## Architecture Guidelines

### Single Activity Pattern
- Uses single `MainActivity` as entry point
- UI built entirely with Compose
- No fragments used

### Dependency Management
- **Version Catalog**: All dependencies managed in `gradle/libs.versions.toml`
- **BOM (Bill of Materials)**: Compose versions managed via BOM
- **Plugin Aliases**: Use `alias(libs.plugins.*)` for plugin configuration

### Testing Strategy
- **Unit Tests**: In `src/test/` for business logic
- **Instrumented Tests**: In `src/androidTest/` for UI and integration tests
- **Compose UI Tests**: Use `androidx.compose.ui.test.junit4` for Compose testing

## Best Practices

### Modifier Usage
- Always provide `Modifier` parameter with default value
- Place modifier parameter last in parameter list
- Chain modifiers in logical order (size → padding → styling → interactions)

Example:
```kotlin
@Composable
fun MyComponent(
    data: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = data,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
```

### Compose Performance
- Use `remember` for expensive computations
- Use `derivedStateOf` for derived state
- Avoid creating new objects in composition
- Use stable data classes for parameters

### ProGuard/R8
- ProGuard rules defined in `app/proguard-rules.pro`
- Currently, minification is disabled (`isMinifyEnabled = false`)
- When enabling, ensure Compose rules are included

## Project-Specific Conventions

### Package Organization
- Main code: `com.codingfeline.changelog.sample`
- UI components: `com.codingfeline.changelog.sample.ui.theme`
- Keep UI-related code in `ui/` subdirectories

### Resource Naming
- Follow Android conventions for resource naming
- Use snake_case for resource files
- Prefix resource IDs with component type (e.g., `ic_launcher`)

## Future Considerations

When extending this project:
1. Consider adding navigation (Compose Navigation) for multi-screen apps
2. Implement state management (ViewModel) for complex state
3. Add dependency injection (Hilt/Koin) for larger codebases
4. Set up code quality tools (ktlint, detekt) for team consistency
5. Enable ProGuard/R8 for release builds
