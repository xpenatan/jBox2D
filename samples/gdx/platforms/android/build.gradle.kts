import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import java.io.File
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
}

abstract class StageGdxJniLibsTask : Sync() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty
}

group = "com.github.xpenatan.box2d.sample.gdx.android"

val box2dVersion = libs.versions.box2dSource.get()
val gdxNativeClassifiers = linkedMapOf(
    "armeabi-v7a" to "natives-armeabi-v7a",
    "arm64-v8a" to "natives-arm64-v8a",
    "x86" to "natives-x86",
    "x86_64" to "natives-x86_64"
)
val gdxNativeConfigurations = gdxNativeClassifiers.keys.associateWith { abi ->
    configurations.create("gdxNatives${abi.replace("-", "").replace("_", "")}") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }
}

val stageGdxJniLibs = tasks.register<StageGdxJniLibsTask>("stageGdxJniLibs") {
    gdxNativeConfigurations.forEach { (abi, configuration) ->
        from(configuration.incoming.artifactView { }.files.elements.map { files ->
            files.map { zipTree(it.asFile) }
        }) {
            include("*.so")
            into(abi)
        }
    }
    outputDirectory.set(layout.buildDirectory.dir("generated/gdxJniLibs"))
    into(outputDirectory)
    doLast {
        val missing = gdxNativeClassifiers.keys.filter { abi ->
            !outputDirectory.get().file("$abi/libgdx.so").asFile.isFile
        }
        if(missing.isNotEmpty()) throw GradleException("Missing libGDX native libraries for: ${missing.joinToString()}")
    }
}

dependencies {
    implementation(project(":samples:gdx:core"))
    implementation(project(":box2d:android:jni"))
    implementation(libs.gdxBackendAndroid)
    gdxNativeClassifiers.forEach { (abi, classifier) ->
        add(gdxNativeConfigurations.getValue(abi).name,
            variantOf(libs.gdxPlatform) { classifier(classifier) })
    }
}

android {
    namespace = "com.github.xpenatan.box2d.sample.gdx.android"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    enableKotlin = false

    defaultConfig {
        applicationId = "com.github.xpenatan.box2d.samples"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1
        versionName = box2dVersion
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    }
}

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.sources.jniLibs?.addGeneratedSourceDirectory(
            stageGdxJniLibs,
            StageGdxJniLibsTask::outputDirectory
        )
    }
}

tasks.matching { task ->
    task.name == "mergeDebugJniLibFolders" || task.name == "mergeReleaseJniLibFolders"
}.configureEach {
    dependsOn(":box2d:builder:jParser_build_android_jni")
}

fun adbExecutable(): String {
    val executable = if(System.getProperty("os.name").lowercase().contains("win")) "adb.exe" else "adb"
    val sdkRoots = mutableListOf<String>()
    val localPropertiesFile = rootProject.file("local.properties")
    if(localPropertiesFile.isFile) {
        val properties = Properties()
        localPropertiesFile.inputStream().use { properties.load(it) }
        properties.getProperty("sdk.dir")?.let { sdkRoots += it }
    }
    System.getenv("ANDROID_HOME")?.let { sdkRoots += it }
    System.getenv("ANDROID_SDK_ROOT")?.let { sdkRoots += it }
    sdkRoots.asSequence().map { file("$it/platform-tools/$executable") }.firstOrNull { it.isFile }
        ?.let { return it.absolutePath }
    System.getenv("PATH").orEmpty().split(File.pathSeparator).asSequence()
        .map { File(it, executable) }.firstOrNull { it.isFile }?.let { return it.absolutePath }
    throw GradleException("Could not find $executable. Configure sdk.dir, ANDROID_HOME, or ANDROID_SDK_ROOT.")
}

tasks.register("box2d_gdx_android_jni_build") {
    group = "samples"
    description = "Builds the Box2D $box2dVersion Android sample browser."
    dependsOn("assembleDebug")
}

tasks.register<Exec>("box2d_gdx_android_jni_run") {
    group = "samples"
    description = "Installs and launches the Box2D $box2dVersion Android sample browser."
    dependsOn("installDebug")
    val command = mutableListOf(adbExecutable(), "shell", "am", "start", "-n",
        "com.github.xpenatan.box2d.samples/com.github.xpenatan.box2d.sample.gdx.android.Box2DGdxAndroidActivity")
    System.getProperties().stringPropertyNames().filter { it.startsWith("jbox2d.sample.") }.sorted().forEach { key ->
        System.getProperty(key)?.takeIf { it.isNotBlank() }?.let { command.addAll(listOf("--es", key, it)) }
    }
    commandLine(command)
}
