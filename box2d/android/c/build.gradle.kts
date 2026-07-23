plugins {
    alias(libs.plugins.androidLibrary)
}

val moduleName = "android-c"
val cLibsDir = "$projectDir/../../builder/build/c++/libs/android"
val stagedJniLibsDir = layout.buildDirectory.dir("generated/cJniLibs")
val androidAbis = listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")

val stageCJniLibs by tasks.registering(Copy::class) {
    androidAbis.forEach { abi ->
        from("$cLibsDir/$abi/teavm_c") {
            include("*.so")
            into(abi)
        }
    }
    into(stagedJniLibsDir)
}

android {
    namespace = "com.github.xpenatan.box2d.android.c"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig { minSdk = libs.versions.androidMinSdk.get().toInt() }

    sourceSets {
        named("main") { jniLibs.srcDirs(stagedJniLibsDir) }
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

tasks.matching { it.name == "mergeReleaseJniLibFolders" || it.name == "mergeDebugJniLibFolders" }.configureEach {
    dependsOn(stageCJniLibs)
}

dependencies {
    api(project(":box2d:shared:c"))
    api(libs.jparserRuntimeAndroidC)
    runtimeOnly(libs.jparserRuntimeAndroidCX86)
    runtimeOnly(libs.jparserRuntimeAndroidCX8664)
    runtimeOnly(libs.jparserRuntimeAndroidCArmeabiV7a)
    runtimeOnly(libs.jparserRuntimeAndroidCArm64V8a)
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
