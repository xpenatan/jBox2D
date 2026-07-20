# jBox2D

[![Snapshot build](https://github.com/xpenatan/jBox2d/actions/workflows/snapshot.yml/badge.svg)](https://github.com/xpenatan/jBox2d/actions/workflows/snapshot.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.xpenatan.jBox2D/core)](https://central.sonatype.com/namespace/com.github.xpenatan.jBox2D)

Java bindings for [Box2D](https://github.com/erincatto/box2d) across desktop, web, and Android.

jBox2D provides a platform-neutral Java API and native runtimes for JNI, Java FFM, TeaVM C, and WebAssembly, plus libGDX and libfdx sample frontends. The bindings are generated from a WebIDL contract and stay close to the upstream Box2D API.

**Online samples:** [xpenatan.github.io/jBox2D](https://xpenatan.github.io/jBox2D) | **3D companion project:** [jBox3D](https://github.com/xpenatan/jBox3D)

## Use jBox2D

Artifacts use the Maven group `com.github.xpenatan.jBox2D`. Choose the runtime artifact for the target application:

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        content {
            includeGroup("com.github.xpenatan.jBox2D")
        }
    }
}

val jbox2dVersion = "3.1.1.0"

dependencies {
    implementation("com.github.xpenatan.jBox2D:desktop-jni:$jbox2dVersion")
}
```

To use the current development build, set `jbox2dVersion` to `-SNAPSHOT`. The snapshot repository can be removed when using a release.

### Artifacts

| Artifact | Purpose |
| --- | --- |
| `core` | Platform-neutral API for shared-source compilation. |
| `desktop-jni` | Java 8+ desktop JNI runtime for Windows, Linux, and macOS. |
| `desktop-ffm` | Java 25 desktop FFM runtime for Windows, Linux, and macOS. |
| `desktop-c` | TeaVM C desktop runtime. |
| `web-wasm` | TeaVM web runtime and Box2D WebAssembly side module. |
| `android-jni` | Android JNI runtime for x86, x86_64, armeabi-v7a, and arm64-v8a. |
| `android-c` | Android TeaVM C runtime for the same four ABIs. |
| `shared-jni`, `shared-c` | Shared implementation artifacts pulled transitively by platform runtimes. |

## API model

The Java API follows Box2D's handle-based ownership model: worlds own bodies, bodies own shapes and chains, and contacts are exposed through buffered events.

## Samples

The sample suite ports the official Box2D scenarios to Java and runs the same catalog through separate libGDX and libfdx frontends on desktop, web, and Android. The libfdx frontend supports OpenGL, WebGPU/WGPU, and Vulkan where the platform provider is available.

## Documentation

- [Build from source and run the samples](docs/usage.md)
- [Binding contract](box2d/builder/src/main/cpp/box2d.idl)

## Status

Box2D and its Java API continue to evolve. Binding generation, native packaging, and cross-platform samples are available, but APIs may change between releases.

## License

jBox2D is licensed under the [Apache License 2.0](LICENSE). Upstream Box2D is developed by Erin Catto and is licensed under the MIT license.
