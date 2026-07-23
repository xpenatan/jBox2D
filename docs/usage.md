# Build and sample usage

This guide covers building jBox2D from source and running its sample applications. For library dependencies and artifact selection, see the main [README](../README.md).

## Requirements

- JDK 25 for the full build and FFM runtime.
- A platform C/C++ toolchain: MSVC on Windows, clang or GCC on Linux, or Xcode command-line tools on macOS.
- Emscripten for the WebAssembly target.
- Android SDK and NDK for Android native targets. The build uses compile SDK 36 and requires API 29 or newer.
- Access to the configured Maven repositories for build dependencies.

The Gradle wrapper uses Gradle 9.4.1. The commands below use PowerShell on Windows; on macOS or Linux, use `./gradlew` instead of `.\gradlew.bat`.

## Project layout

| Area | Modules | Purpose |
| --- | --- | --- |
| Source and bindings | `:box2d:download`, `:box2d:builder` | Download Box2D, generate bindings, and build native targets. |
| Core Java API | `:box2d:base`, `:box2d:core` | Runtime-loader support and the platform-neutral API. |
| Desktop JNI | `:box2d:shared:jni`, `:box2d:desktop:jni` | JNI bindings and native libraries for Windows, Linux, and macOS. |
| Desktop FFM | `:box2d:desktop:ffm` | Java 25 FFM bindings and native libraries. |
| TeaVM C | `:box2d:shared:c`, `:box2d:desktop:c` | TeaVM C bindings and desktop native packaging. |
| WebAssembly | `:box2d:web:wasm` | TeaVM web API and the Emscripten side module. |
| Android | `:box2d:android:jni`, `:box2d:android:c` | Android JNI and TeaVM C runtime packaging. |
| Samples | `:samples:shared`, `:samples:core`, `:samples:desktop:*`, `:samples:web`, `:samples:android` | Shared scenarios, rendering, and platform launchers. |

## Generate the bindings

Download the pinned Box2D source and generate the Java and native bindings:

```powershell
.\gradlew.bat :box2d:download:box2d_download_source
.\gradlew.bat :box2d:builder:jParser_generate
```

The downloaded source is written to `box2d/download/build/box2d-source` and is not vendored in the repository. Generated source directories are removed by their module `clean` tasks, so a clean build must regenerate them:

```powershell
.\gradlew.bat clean
.\gradlew.bat :box2d:builder:jParser_generate
.\gradlew.bat build
```

## Build native runtimes

Build the runtime needed by the application before launching it. Sample launcher tasks package native libraries already produced by these builder tasks.

### Desktop

Windows x64:

```powershell
.\gradlew.bat :box2d:builder:jParser_build_windows64_jni
.\gradlew.bat :box2d:builder:jParser_build_windows64_ffm
.\gradlew.bat :box2d:builder:jParser_build_windows64_teavm_c
```

Replace `windows64` with `linux64`, `mac64`, or `macArm` for the corresponding desktop target.

### WebAssembly

```powershell
.\gradlew.bat :box2d:builder:jParser_build_web_wasm
```

### Android

```powershell
.\gradlew.bat :box2d:builder:jParser_build_android_jni
.\gradlew.bat :box2d:builder:jParser_build_android_teavm_c
```

The builder also exposes `:box2d:builder:jParser_build_ios_jni`, but the repository does not currently include an iOS runtime module or sample application.

## Run the samples

The Java ports of the official Box2D scenarios are shared by the desktop, web, and Android launchers.

### Desktop

Build the matching native target for the current host before launching a desktop sample:

```powershell
.\gradlew.bat :samples:desktop:jni:box2d_sample_jni_run
.\gradlew.bat :samples:desktop:ffm:box2d_sample_ffm_run
.\gradlew.bat :samples:desktop:c:gdx_teavm_glfw_run
```

### Web

Build or run either browser variant:

```powershell
.\gradlew.bat :samples:web:gdx_teavm_web_js_build
.\gradlew.bat :samples:web:gdx_teavm_web_wasm_build
.\gradlew.bat :samples:web:gdx_teavm_web_js_run
.\gradlew.bat :samples:web:gdx_teavm_web_wasm_run
```

These tasks also build and stage the Emscripten Box2D side module used by both distributions.

### Android

The build task creates the JNI runtime. The run task installs and starts the debug application through `adb`:

```powershell
.\gradlew.bat :samples:android:box2d_samples_android_build
.\gradlew.bat :samples:android:box2d_samples_android_run
```

`adb` is resolved from `local.properties`, `ANDROID_HOME`, `ANDROID_SDK_ROOT`, or `PATH`.

## Controls and sample selection

Select a scenario from the left panel. The right panel contains global solver and debug settings plus controls for the selected sample.

- Drag with the primary pointer to interact with bodies.
- Use the right or middle mouse button to pan.
- Use the wheel to zoom.
- Press `Home` to reset the camera.
- Keyboard-driven samples receive the same letter, number, space, enter, and arrow keys as the upstream application.

Launchers accept a category and name through `jbox2d.sample.sample`, or a registry index through `jbox2d.sample.sampleIndex`:

```powershell
$env:GRADLE_OPTS = '-Djbox2d.sample.sample=Benchmark/Cast'
.\gradlew.bat :samples:desktop:jni:box2d_sample_jni_run
```
