pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if(requested.id.id == "com.github.xpenatan.jparser") {
                val version = requested.version
                    ?: throw GradleException("The jParser plugin version must be declared in the version catalog")
                useModule("com.github.xpenatan.jParser:jparser-gradle-plugin:$version")
            }
        }
    }

    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
        gradlePluginPortal()
        maven {
            url = uri("http://teavm.org/maven/repository/")
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "jBox2D"

include(":box2d:builder")
include(":box2d:download")
include(":box2d:base")
include(":box2d:core")
include(":box2d:shared:jni")
include(":box2d:shared:c")
include(":box2d:desktop:jni")
include(":box2d:desktop:ffm")
include(":box2d:desktop:c")
include(":box2d:web:wasm")
include(":box2d:android:jni")
include(":box2d:android:c")
include(":extensions:gdx:gl")
include(":extensions:fdx")

include(":samples:shared")
include(":samples:gdx:core")
include(":samples:gdx:platforms:desktop-jni")
include(":samples:gdx:platforms:desktop-ffm")
include(":samples:gdx:platforms:desktop-c")
include(":samples:gdx:platforms:web")
include(":samples:gdx:platforms:android")
include(":samples:fdx:core")
include(":samples:fdx:platforms:desktop-jni")
include(":samples:fdx:platforms:desktop-ffm")
include(":samples:fdx:platforms:desktop-c")
include(":samples:fdx:platforms:web")
include(":samples:fdx:platforms:android")
