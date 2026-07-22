plugins {
    id("com.android.library")
}

val moduleName = "android-jni"

android {
    namespace = "com.github.xpenatan.box2d.android.jni"
    compileSdk = 36

    defaultConfig { minSdk = 29 }

    sourceSets {
        named("main") {
            jniLibs.srcDirs("$projectDir/../../builder/build/c++/libs/android")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
        targetCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    publishing { singleVariant("release") }
}

dependencies {
    api(project(":box2d:shared:jni"))
    api("com.github.xpenatan.jParser:runtime-jni:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-android:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-android_x86:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-android_x86_64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-android_armeabi_v7a:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-android_arm64_v8a:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
        }
    }
}

afterEvaluate {
    publishing.publications.named<MavenPublication>("maven") {
        from(components["release"])
    }
}
