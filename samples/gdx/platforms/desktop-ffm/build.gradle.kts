import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("java")
}

val box2dRuntimeName = "ffm"
val box2dRuntimeProject = ":box2d:desktop:ffm"
val box2dVersion = libs.versions.box2d.get()
val box2dRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":samples:gdx:core"))
    implementation(libs.gdxBackendLwjgl3)
    implementation(variantOf(libs.gdxPlatform) { classifier("natives-desktop") })

    box2dRuntimeClasspath(project(box2dRuntimeProject))
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}

val sampleMainClass = "com.github.xpenatan.box2d.sample.gdx.desktop.Box2DGdxDesktopLauncher"

fun Task.configureRuntimeInputs() {
    dependsOn("$box2dRuntimeProject:jar")
    inputs.files(box2dRuntimeClasspath)
}

fun JavaExec.useJava25Launcher() {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.javaFfm.get().toInt()))
    })
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.register("box2d_gdx_desktop_${box2dRuntimeName}_build") {
    group = "samples"
    description = "Build the Box2D $box2dVersion desktop sample with the FFM binding."
    dependsOn("classes")
    configureRuntimeInputs()
}

tasks.register<JavaExec>("box2d_gdx_desktop_${box2dRuntimeName}_run") {
    group = "samples"
    description = "Run the interactive Box2D $box2dVersion Java sample browser with the desktop FFM binding."
    dependsOn("box2d_gdx_desktop_${box2dRuntimeName}_build")
    mainClass.set(sampleMainClass)
    classpath = box2dRuntimeClasspath + sourceSets["main"].runtimeClasspath
    useJava25Launcher()
    listOf(
        "jbox2d.sample.sample",
        "jbox2d.sample.sampleIndex",
        "jbox2d.sample.exitAfterFrames",
        "jbox2d.sample.screenshot"
    ).forEach { property ->
        System.getProperty(property)?.takeIf { it.isNotBlank() }?.let { systemProperty(property, it) }
    }
}
