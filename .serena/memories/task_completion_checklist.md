# Task Completion Checklist

When completing a task in this project, follow these steps:

## 1. Build Verification
After making code changes, ensure the project builds successfully:
```bash
./gradlew build
```

If the build fails, fix any compilation errors before proceeding.

## 2. Testing
Run the test suite to ensure no regressions:

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests (if applicable)
```bash
./gradlew connectedAndroidTest
```
(Requires connected device or emulator)

## 3. Code Quality (Optional)
Currently, there are no automated linting or formatting tools configured in this project (no ktlint, detekt, or similar).

If implementing code quality tools in the future:
- Run linting: `./gradlew lint`
- Run static analysis: `./gradlew detekt`
- Format code: `./gradlew ktlintFormat`

## 4. Manual Verification
For UI changes:
1. Install the app on a device/emulator:
   ```bash
   ./gradlew installDebug
   ```
2. Manually test the affected functionality
3. Test on both light and dark themes (if UI-related)
4. Test on different Android versions if SDK requirements changed

## 5. Documentation
- Update relevant comments if API signatures changed
- Update CLAUDE.md if project structure or commands changed
- Update README.md if user-facing features were added

## 6. Git Operations
After verification:
```bash
git status
git add .
git commit -m "Descriptive commit message"
git push
```

## Summary Checklist
- [ ] Code builds without errors (`./gradlew build`)
- [ ] All tests pass (`./gradlew test`)
- [ ] Changes manually tested (if applicable)
- [ ] Documentation updated (if applicable)
- [ ] Changes committed to git

## Notes
- Since there's no automated formatting, ensure code follows the conventions in `code_style_and_conventions.md`
- For Compose UI changes, verify previews render correctly in Android Studio
- Consider edge-to-edge behavior when modifying layouts
