# AGENTS.md

## Overview

T4 Launcher is a minimal Android launcher app (Kotlin, no external runtime dependencies beyond the Kotlin stdlib) that filters installed apps via four predictive-text buttons. It is a home-screen app (`HOME` category) with a single Activity.

The entire application is three Kotlin files in `src/main/kotlin/com/github/rsteube/t4/`:

- `T4Launcher.kt` - the (only) Activity. Builds the whole UI **programmatically in `onCreate`**; there are no XML layouts. Listens for button taps (add filter), back press (remove last filter), and long-press back (reload app list).
- `RegexFilter.kt` - an `Observable` holding a list of character-range patterns. Each button press appends a regex fragment like `([a-f]|[A-F])(\W|[0-9_])*`. `matches()` filters app labels; `format()` HTML-highlights matched characters in the list. `Pattern` is an enum (`A_F`, `G_L`, `M_R`, `S_Z`) whose regex is derived from the enum name, so renaming/adding enum values changes button labels and regexes together.
- `LauncherAdapter.kt` - `ArrayAdapter` that queries `PackageManager` for launchable activities, excludes Settings and itself, sorts by label+package, and re-filters whenever `RegexFilter` notifies observers.

### Data/control flow

Button press -> `RegexFilter.add()` -> `update()` -> `notifyObservers()` -> `LauncherAdapter.performFiltering()`.

Gotcha: if filtering yields zero results, `performFiltering` calls `filter.removeLast()` instead of showing an empty list. App launch failures (uninstalled/hidden apps) are caught and trigger a full `reload()`. `onResume` clears the filter, and the manifest uses `singleTask` + `stateNotNeeded` so returning home restarts in a clean state.

## Build commands

Builds via the committed Gradle wrapper (Gradle 9.7.1); no system Gradle needed. Requires JDK 17+ and an Android SDK with `platforms;android-36` + `build-tools;36.0.0`, located via `ANDROID_HOME` (or an uncommitted `local.properties`).

```sh
./gradlew assembleDebug    # debug APK -> build/outputs/apk/debug/t4-launcher-debug.apk
./gradlew assembleRelease  # unsigned release APK -> build/outputs/apk/release/t4-launcher-release-unsigned.apk (CI stores this artifact)
```

There are no tests and no lint configuration in the repo; CI only builds the release APK. On-device verification: `adb install -r build/outputs/apk/debug/t4-launcher-debug.apk`.

## Versioning / release

- Version lives in `build.gradle` (`versionCode` / `versionName`). Bump both together (e.g. versionCode 4 / versionName "1.4").
- `project.archivesBaseName = "t4-launcher"` controls the APK filename; CI artifact path depends on it.
- Release builds enable minify + resource shrinking with only the default ProGuard file.
- Fastlane metadata (`fastlane/metadata/android/en-US/`) holds F-Droid descriptions and screenshots; update the short/full descriptions when the user-facing behavior changes.
- The launcher APK output name is derived from `archivesBaseName` plus build type (e.g. `t4-launcher-release-unsigned.apk`), which the CircleCI `store_artifacts` path hardcodes.

## Conventions / gotchas

- Toolchain: AGP 9.4.0 + Gradle 9.7.1 wrapper, compileSdk/targetSdk 36, minSdk 14. Repositories are declared in `settings.gradle` (`google()` + `mavenCentral()`; jcenter is dead - never reintroduce it). Groovy DSL must use `prop = value` assignment syntax; space-separated assignment is deprecated and breaks on Gradle 10.
- Kotlin: AGP 9 uses **built-in Kotlin support** - do not apply `org.jetbrains.kotlin.android` or add a stdlib dependency; AGP manages the Kotlin version and adds the stdlib automatically. Kotlin stdlib deprecations are compiled as **errors** (e.g. `toLowerCase()` must be `lowercase()`), while Android API deprecations (e.g. `Html.fromHtml` without mode) are warnings.
- Manifest: no `package` attribute anymore (namespace lives in `build.gradle`); activities with intent filters must declare `android:exported` explicitly (targetSdk 31+ requirement).
- Legacy source location: `src/main/kotlin`. Add new source files there, not under `java/`.
- No third-party libraries: keep it that way; the app intentionally has zero dependencies beyond the Kotlin stdlib.
- UI is built entirely in code with `apply {}` blocks; theme is the system `Theme.Black.NoTitleBar`. Button look comes from `res/drawable/button.xml`.
- App identity: namespace/package `com.github.rsteube.t4` in both `build.gradle` and the Kotlin package. Changing one requires changing the other.
- CI is CircleCI (`.circleci/config.yml`, `cimg/openjdk` image + SDK cmdline-tools bootstrap), not GitHub Actions.
