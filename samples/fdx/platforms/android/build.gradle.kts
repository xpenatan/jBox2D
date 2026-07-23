import java.io.File
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
}

group = "com.github.xpenatan.box2d.sample.fdx.android"

android {
    namespace = "com.github.xpenatan.box2d.sample.fdx.android"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.github.xpenatan.box2d.sample.fdx.android"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1
        versionName = libs.versions.box2d.get()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFfm.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaFfm.get())
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibs)

    implementation(project(":samples:fdx:core"))
    implementation(project(":box2d:android:jni"))
    implementation(libs.fdxBackendAndroid)
    implementation(libs.fdxWgpuAndroidJni)
    implementation(libs.fdxVulkanAndroidJni)
}

fun adbExecutable(): String {
    val executable = if(System.getProperty("os.name").lowercase().contains("win")) "adb.exe" else "adb"
    val sdkRoots = mutableListOf<String>()
    val localPropertiesFile = rootProject.file("local.properties")
    if(localPropertiesFile.isFile) {
        val localProperties = Properties()
        localPropertiesFile.inputStream().use { localProperties.load(it) }
        localProperties.getProperty("sdk.dir")?.let { sdkRoots += it }
    }
    System.getenv("ANDROID_HOME")?.let { sdkRoots += it }
    System.getenv("ANDROID_SDK_ROOT")?.let { sdkRoots += it }
    sdkRoots.asSequence()
            .map { file("$it/platform-tools/$executable") }
            .firstOrNull { it.isFile }
            ?.let { return it.absolutePath }

    System.getenv("PATH").orEmpty().split(File.pathSeparator)
            .asSequence()
            .map { File(it, executable) }
            .firstOrNull { it.isFile }
            ?.let { return it.absolutePath }

    throw GradleException(
        "Could not find $executable. Set sdk.dir in local.properties, set ANDROID_HOME or ANDROID_SDK_ROOT, or add adb to PATH."
    )
}

fun registerAndroidBuildTask(name: String, descriptionText: String) {
    tasks.register(name) {
        group = "samples"
        description = descriptionText
        dependsOn("assembleDebug")
    }
}

fun registerAndroidRunTask(name: String, activityName: String) {
    tasks.register<Exec>(name) {
        group = "samples"
        description = "Installs and launches the jBox2D libfdx Android sample."
        dependsOn("installDebug")
        val command = mutableListOf(
            adbExecutable(),
            "shell",
            "am",
            "start",
            "-n",
            "com.github.xpenatan.box2d.sample.fdx.android/$activityName"
        )
        System.getProperties().stringPropertyNames()
                .filter { it.startsWith("jbox2d.sample.") }
                .sorted()
                .forEach { key ->
                    val value = System.getProperty(key)
                    if(!value.isNullOrBlank()) command.addAll(listOf("--es", key, value))
                }
        commandLine(command)
    }
}

registerAndroidBuildTask(
    "box2d_fdx_android_gles_build",
    "Builds the jBox2D libfdx Android OpenGL ES sample."
)
registerAndroidBuildTask(
    "box2d_fdx_android_wgpu_jni_build",
    "Builds the jBox2D libfdx Android WGPU JNI sample."
)
registerAndroidBuildTask(
    "box2d_fdx_android_vulkan_build",
    "Builds the jBox2D libfdx Android Vulkan JNI sample."
)

registerAndroidRunTask(
    "box2d_fdx_android_gles_run",
    "com.github.xpenatan.box2d.sample.fdx.android.Box2DFdxAndroidGlesActivity"
)
registerAndroidRunTask(
    "box2d_fdx_android_wgpu_jni_run",
    "com.github.xpenatan.box2d.sample.fdx.android.Box2DFdxAndroidWgpuActivity"
)
registerAndroidRunTask(
    "box2d_fdx_android_vulkan_run",
    "com.github.xpenatan.box2d.sample.fdx.android.Box2DFdxAndroidVulkanActivity"
)
