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
| Integrations | `:extensions:gdx:gl`, `:extensions:fdx` | libGDX and libfdx converters and debug renderers. |
| Samples | `:samples:shared`, `:samples:gdx:*`, `:samples:fdx:*` | Shared scenarios plus libGDX and libfdx frontends and platform launchers. |

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

The Java ports of the official Box2D scenarios live in `:samples:shared`. The libGDX and libfdx frontends consume that same catalog and provide their own platform launchers.

### Desktop

libGDX:

```powershell
.\gradlew.bat :samples:gdx:platforms:desktop-jni:box2d_gdx_desktop_jni_run
.\gradlew.bat :samples:gdx:platforms:desktop-ffm:box2d_gdx_desktop_ffm_run
.\gradlew.bat :samples:gdx:platforms:desktop-c:gdx_teavm_glfw_run
```

libfdx OpenGL, WGPU, and Vulkan with the JNI runtime:

```powershell
.\gradlew.bat :samples:fdx:platforms:desktop-jni:box2d_fdx_desktop_gl_jni_run
.\gradlew.bat :samples:fdx:platforms:desktop-jni:box2d_fdx_desktop_wgpu_jni_run
.\gradlew.bat :samples:fdx:platforms:desktop-jni:box2d_fdx_desktop_vulkan_jni_run
```

Replace `desktop-jni`/`jni` with `desktop-ffm`/`ffm` to use the Box2D FFM runtime. The libfdx `desktop-c` module exposes `box2d_fdx_desktop_<graphics>_c_build` tasks for the TeaVM C compile paths.

### Web

libGDX WebGL:

```powershell
.\gradlew.bat :samples:gdx:platforms:web:gdx_teavm_web_js_run
.\gradlew.bat :samples:gdx:platforms:web:gdx_teavm_web_wasm_run
```

libfdx WebGL and WebGPU:

```powershell
.\gradlew.bat :samples:fdx:platforms:web:box2d_fdx_webgl_js_run
.\gradlew.bat :samples:fdx:platforms:web:box2d_fdx_webgl_wasm_run
.\gradlew.bat :samples:fdx:platforms:web:box2d_fdx_webgpu_js_run
```

These tasks build and stage the Emscripten Box2D side module used by the browser distributions. libfdx WebGPU uses the JavaScript target; its WasmGC target currently uses WebGL.

### Android

libGDX with the Box2D JNI runtime:

```powershell
.\gradlew.bat :samples:gdx:platforms:android:box2d_gdx_android_jni_build
.\gradlew.bat :samples:gdx:platforms:android:box2d_gdx_android_jni_run
```

libfdx OpenGL ES, WGPU, and Vulkan:

```powershell
.\gradlew.bat :samples:fdx:platforms:android:box2d_fdx_android_gles_run
.\gradlew.bat :samples:fdx:platforms:android:box2d_fdx_android_wgpu_jni_run
.\gradlew.bat :samples:fdx:platforms:android:box2d_fdx_android_vulkan_run
```

`adb` is resolved from `local.properties`, `ANDROID_HOME`, `ANDROID_SDK_ROOT`, or `PATH`.

## Controls and sample selection

Select a scenario from the sample panel. Both frontends expose the global solver settings and controls supplied by the selected shared scenario.

- Drag with the primary pointer to interact with bodies.
- Use the right or middle mouse button to pan.
- Use the wheel to zoom.
- Press `Home` to reset the camera.
- Keyboard-driven samples receive the same letter, number, space, enter, and arrow keys as the upstream application.

Launchers accept a category and name through `jbox2d.sample.sample`, or a registry index through `jbox2d.sample.sampleIndex`:

```powershell
$env:GRADLE_OPTS = '-Djbox2d.sample.sample=Benchmark/Cast'
.\gradlew.bat :samples:gdx:platforms:desktop-jni:box2d_gdx_desktop_jni_run
```
