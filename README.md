# Witcher Quest Wise — Desktop ("The Wild Hunt")

A native desktop companion app for *The Witcher 3: Wild Hunt*, built with
[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) for the JVM.
It bundles a quest guide, a bestiary, and a Gwent card gallery — with original hand-made
artwork — packaged as a standalone Windows app (also buildable for macOS and Linux).

## What it is

- **Quest guide** for tracking Witcher 3 progress.
- **Bestiary** with illustrated monster entries (griffin, leshen, noonwraith, …).
- **Gwent card gallery** with custom card designs.
- Custom-made artwork throughout (`composeApp/src/commonMain/composeResources/drawable`).

## Tech stack

| Layer | Tooling |
|-------|---------|
| UI | Compose Multiplatform (Material 3), JVM desktop target |
| Local data | Room + bundled SQLite |
| Images / networking | Coil 3, OkHttp, Moshi |
| Build | Gradle (Kotlin DSL), KSP |
| Packaging | Compose Desktop native distributions (MSI / DMG / DEB) |

Entry point: `MainKt` · Package name: **The Wild Hunt** · Version: 1.0.0

## Run it (development)

Requires JDK 17+.

```bash
# launch the desktop app
./gradlew :composeApp:run
```

## Build an installer

```bash
# build for the current OS (output under composeApp/build/compose/binaries)
./gradlew :composeApp:packageDistributionForCurrentOS

# or a specific format
./gradlew :composeApp:packageMsi      # Windows installer
./gradlew :composeApp:packageDmg      # macOS
./gradlew :composeApp:packageDeb      # Linux
```

On Windows the installer registers a "The Wild Hunt" Start-menu shortcut (icon: `composeApp/wolf.ico`).

## Project layout

```
composeApp/
  src/commonMain/kotlin/         App UI and logic (Compose)
  src/commonMain/composeResources/drawable/   Bestiary + Gwent artwork
  build.gradle.kts               Module config, dependencies, packaging
settings.gradle.kts              Root project (":composeApp")
```
