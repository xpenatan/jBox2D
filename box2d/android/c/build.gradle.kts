import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory

plugins {
    alias(libs.plugins.androidLibrary)
}

abstract class StageCJniLibsTask : Sync() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty
}

val moduleName = "android-c"
val cLibsDir = "$projectDir/../../builder/build/c++/libs/android"
val androidAbis = listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")

val stageCJniLibs = tasks.register<StageCJniLibsTask>("stageCJniLibs") {
    androidAbis.forEach { abi ->
        from("$cLibsDir/$abi/teavm_c") {
            include("*.so")
            into(abi)
        }
    }
    outputDirectory.set(layout.buildDirectory.dir("generated/cJniLibs"))
    into(outputDirectory)
}

android {
    namespace = "com.github.xpenatan.box2d.android.c"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    enableKotlin = false

    defaultConfig { minSdk = libs.versions.androidMinSdk.get().toInt() }

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

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.sources.jniLibs?.addGeneratedSourceDirectory(
            stageCJniLibs,
            StageCJniLibsTask::outputDirectory
        )
    }
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
