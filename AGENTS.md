# Repository Guidelines

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

## Project Structure & Module Organization
- `:changelog` lives in `changelog/src/main/java/com/codingfeline/changelog` and hosts the Compose UI, parser, and view-model layers; keep non-API code marked `internal`.
- `:app` demonstrates integration in `app/src/main/java/com/codingfeline/changelog/sample`, with resources under `app/src/main/res` for manual UX checks.
- Unit tests reside in `changelog/src/test/java`; sample unit and instrumentation tests sit in `app/src/test` and `app/src/androidTest`.
- Shared Gradle configuration and publishing metadata are in `gradle.properties`; process docs for larger proposals live in `openspec/`.

## Build, Test, and Development Commands
- `./gradlew :changelog:assembleRelease` – build the publishable AAR.
- `./gradlew :app:installDebug` – install the sample app on a running emulator or device.
- `./gradlew :changelog:testDebugUnitTest` – execute JVM unit tests for parser and view-model logic.
- `./gradlew :app:connectedDebugAndroidTest` – run Compose UI instrumentation tests; start an emulator first.
- `./gradlew lint` – aggregate Android Lint across modules; resolve findings before review.
- `./gradlew :changelog:publishToMavenLocal` – stage a local Maven artifact for downstream verification.

## Coding Style & Naming Conventions
- Kotlin style is `official`: 4-space indent, expressive trailing commas, and idiomatic null-safety.
- Public composables and types use `PascalCase`, functions and parameters `camelCase`, and constants `UPPER_SNAKE_CASE`.
- Organize code by feature packages, keep helper composables and parsers `internal`, and rely on the Gradle build to surface warnings prior to commit.

## Testing Guidelines
- Use JUnit4 with `kotlinx-coroutines-test`; inject dispatchers to avoid leaking `Dispatchers.Main`.
- Place Compose instrumentation tests in `app/src/androidTest` with the `AndroidJUnit4` runner.
- Name tests `function_underCondition_expectedResult` and cover parser edge cases plus `ChangelogViewModel` state transitions before shipping.

## Commit & Pull Request Guidelines
- Follow short, imperative subjects (optionally `module: action`) capped at 72 characters.
- Include bodies that describe user impact and call out new or changed public APIs.
- PRs must add a summary, validation evidence (command output or screenshots), linked issues, and highlight any breaking changes.

## Publishing & Configuration Notes
- Maven coordinates and license metadata come from `gradle.properties`; keep signing credentials in `local.properties` or environment variables.
- When running publish tasks, document required secrets or manual follow-up steps in the PR so reviewers can reproduce the release flow.
