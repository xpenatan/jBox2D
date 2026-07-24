plugins {
    alias(libs.plugins.androidLibrary)
}

val moduleName = "android-jni"

android {
    namespace = "com.github.xpenatan.box2d.android.jni"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    enableKotlin = false

    defaultConfig { minSdk = libs.versions.androidMinSdk.get().toInt() }

    sourceSets {
        named("main") {
            jniLibs.srcDirs("$projectDir/../../builder/build/c++/libs/android")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
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
    api(libs.jparserRuntimeJni)
    api(libs.jparserRuntimeAndroid)
    runtimeOnly(libs.jparserRuntimeAndroidX86)
    runtimeOnly(libs.jparserRuntimeAndroidX8664)
    runtimeOnly(libs.jparserRuntimeAndroidArmeabiV7a)
    runtimeOnly(libs.jparserRuntimeAndroidArm64V8a)
    api(libs.jparserApiCore)
    api(libs.jparserLoaderCore)
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
