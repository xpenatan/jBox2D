# jBox2D

Java bindings for [Box2D](https://github.com/erincatto/box2d) across desktop, web, and Android.

The project uses [jParser](https://github.com/xpenatan/jParser) to generate bindings from a WebIDL contract and a small native facade. It downloads the pinned upstream Box2D source into the build directory, generates Java and native glue, and packages the platform runtime modules.

The generated API stays close to upstream Box2D semantics while adapting native handles and structs for Java. The repository also includes cross-platform sample applications with solver controls, debug rendering, body interaction, and camera controls.

**Live samples:** [xpenatan.github.io/jBox2D](https://xpenatan.github.io/jBox2D) | **3D companion project:** [jBox3D](https://github.com/xpenatan/jBox3D)

## Modules

| Area | Modules | Purpose |
| --- | --- | --- |
| Source and bindings | `:box2d:download`, `:box2d:builder` | Download the pinned Box2D source, own the binding contract and facade, generate Java/native glue, and build native targets. |
| Core Java API | `:box2d:base`, `:box2d:core` | Runtime-loader support and the generated platform-neutral API. |
| Desktop JNI | `:box2d:shared:jni`, `:box2d:desktop:jni` | Generated JNI bindings and native libraries for Windows, Linux, and macOS. |
| Desktop FFM | `:box2d:desktop:ffm` | Java 25 Foreign Function & Memory bindings and desktop native libraries. |
| TeaVM C | `:box2d:shared:c`, `:box2d:desktop:c` | Generated C-runtime bindings and desktop native packaging. |
| WebAssembly | `:box2d:web:wasm` | TeaVM web API plus the Emscripten side module. |
| Android | `:box2d:android:jni`, `:box2d:android:c` | Android JNI and TeaVM C runtime packaging. |
| Samples | `:samples:shared`, `:samples:core`, `:samples:desktop:*`, `:samples:web`, `:samples:android` | Shared Box2D sample ports, UI/rendering, and platform launchers. |

The binding contract is [`box2d.idl`](box2d/builder/src/main/cpp/box2d.idl). The [`custom`](box2d/builder/src/main/cpp/custom) facade converts opaque Box2D IDs and C structs into a Java-friendly API while preserving the upstream semantics.

The Java API follows Box2D's handle-based ownership model: worlds own bodies, bodies own shapes and chains, and contacts are exposed through buffered events.

## Samples

The Java ports of the official Box2D sample suite live in `:samples:shared`. The shared libGDX sample browser, controls, and renderer live in `:samples:core`; desktop, web, and Android all run that same Java sample catalog through the generated bindings.

| Front end | Desktop | Web | Android |
| --- | --- | --- | --- |
| libGDX / OpenGL | JNI, FFM, and TeaVM C | TeaVM JavaScript and WasmGC | JNI |

There is no embedded C++ sample suite. Each scenario is a Java class under [`samples/shared`](samples/shared/src/main/java/com/github/xpenatan/box2d/sample/shared/samples), and the registry preserves the official Box2D category and sample names.

## Requirements

- JDK 25 for the full build and FFM runtime.
- A platform C/C++ toolchain: MSVC on Windows, clang or GCC on Linux, or Xcode command-line tools on macOS.
- Emscripten for the WebAssembly target.
- Android SDK and NDK for Android native targets (compile SDK 36; API 29 minimum).
- Access to the configured Maven repositories for the jParser Gradle plugin snapshot and other project dependencies.

The Gradle wrapper is pinned to 9.4.1 so the full build can run on JDK 25. On macOS or Linux, use `./gradlew` in place of `.\gradlew.bat` in the commands below.

## Generate the bindings

```powershell
.\gradlew.bat :box2d:download:box2d_download_source
.\gradlew.bat :box2d:builder:jParser_generate
```

The download task writes the upstream source to `box2d/download/build/box2d-source`; Box2D source is not vendored in this repository. Generated source directories are removed by their module `clean` tasks, so a from-scratch build must regenerate them:

```powershell
.\gradlew.bat clean
.\gradlew.bat :box2d:builder:jParser_generate
.\gradlew.bat build
```

## Build native runtimes

Windows x64 examples:

```powershell
.\gradlew.bat :box2d:builder:jParser_build_windows64_jni
.\gradlew.bat :box2d:builder:jParser_build_windows64_ffm
.\gradlew.bat :box2d:builder:jParser_build_windows64_teavm_c
.\gradlew.bat :box2d:builder:jParser_build_web_wasm
```

Replace `windows64` with `linux64`, `mac64`, or `macArm` for the corresponding desktop target. Android native tasks are:

```powershell
.\gradlew.bat :box2d:builder:jParser_build_android_jni
.\gradlew.bat :box2d:builder:jParser_build_android_teavm_c
```

The builder also exposes `:box2d:builder:jParser_build_ios_jni`, but this repository does not yet include an iOS runtime-packaging or sample module.

## Run the samples

### Desktop

Build the matching native target for the current host before launching a desktop sample. The launcher tasks consume the native libraries already produced by the builder.

```powershell
.\gradlew.bat :samples:desktop:jni:box2d_sample_jni_run
.\gradlew.bat :samples:desktop:ffm:box2d_sample_ffm_run
.\gradlew.bat :samples:desktop:c:gdx_teavm_glfw_run
```

The TeaVM C module uses the gdx-teavm plugin tasks `gdx_teavm_glfw_generate`, `gdx_teavm_glfw_build`, and `gdx_teavm_glfw_run`.

### Web

Build or run both browser variants with:

```powershell
.\gradlew.bat :samples:web:gdx_teavm_web_js_build
.\gradlew.bat :samples:web:gdx_teavm_web_wasm_build
.\gradlew.bat :samples:web:gdx_teavm_web_js_run
.\gradlew.bat :samples:web:gdx_teavm_web_wasm_run
```

These tasks also build and stage the Emscripten Box2D side module used by both distributions.

### Android

The Android sample requires API 29 or newer. Its build task builds the JNI runtime; the run task installs and starts the debug application through `adb`.

```powershell
.\gradlew.bat :samples:android:box2d_samples_android_build
.\gradlew.bat :samples:android:box2d_samples_android_run
```

`adb` is resolved from `local.properties`, `ANDROID_HOME`, `ANDROID_SDK_ROOT`, or `PATH`.

### Controls and sample selection

Select a scenario from the left panel. The right panel contains global solver/debug settings and the selected sample's controls. Drag with the primary pointer to interact with bodies, use the right or middle mouse button to pan, use the wheel to zoom, and press `Home` to reset the camera. Keyboard-driven samples receive the same letter, number, space, enter, and arrow keys as the upstream application.

Launchers accept a category/name through `jbox2d.sample.sample` or a registry index through `jbox2d.sample.sampleIndex`. For example:

```powershell
$env:GRADLE_OPTS = '-Djbox2d.sample.sample=Benchmark/Cast'
.\gradlew.bat :samples:desktop:jni:box2d_sample_jni_run
```

## GitHub Pages

The Pages bundle contains both TeaVM JavaScript and WasmGC builds behind a platform selector:

```powershell
.\gradlew.bat :samples:web:box2d_samples_pages_build
```

The static site is written to `samples/web/build/pages`, with distributions under `gdx/gl/js` and `gdx/gl/wasm`. The manually dispatched [GitHub Pages workflow](.github/workflows/gh-pages.yml) builds the site and deploys it when run from `master`; the repository's Pages source must be set to **GitHub Actions**.

## License

jBox2D is licensed under the [Apache License 2.0](LICENSE). Upstream Box2D is developed by Erin Catto and is licensed under the MIT license.
