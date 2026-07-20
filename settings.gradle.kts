pluginManagement {
    val jParserPluginVersion = "-SNAPSHOT"
    val gdxTeaVMPluginVersion = "1.6.0"
    val libfdxPluginVersion = "-SNAPSHOT"

    resolutionStrategy {
        eachPlugin {
            if(requested.id.id == "com.github.xpenatan.jparser") {
                useModule("com.github.xpenatan.jParser:jparser-gradle-plugin:$jParserPluginVersion")
            }
        }
    }

    plugins {
        id("com.github.xpenatan.jparser") version jParserPluginVersion
        id("com.github.xpenatan.gdx-teavm") version gdxTeaVMPluginVersion
        id("io.github.libfdx") version libfdxPluginVersion
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
