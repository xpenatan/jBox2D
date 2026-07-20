import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("java")
}

val box2dRuntimeName = "ffm"
val box2dRuntimeProject = ":box2d:desktop:ffm"
val box2dRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":samples:gdx:core"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")

    box2dRuntimeClasspath(project(box2dRuntimeProject))
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
}

val sampleMainClass = "com.github.xpenatan.box2d.sample.gdx.desktop.Box2DGdxDesktopLauncher"

fun Task.configureRuntimeInputs() {
    dependsOn("$box2dRuntimeProject:jar")
    inputs.files(box2dRuntimeClasspath)
}

fun JavaExec.useJava25Launcher() {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(LibExt.javaFFMTarget.toInt()))
    })
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.register("box2d_gdx_desktop_${box2dRuntimeName}_build") {
    group = "samples"
    description = "Build the Box2D 3.1.1 desktop sample with the FFM binding."
    dependsOn("classes")
    configureRuntimeInputs()
}

tasks.register<JavaExec>("box2d_gdx_desktop_${box2dRuntimeName}_run") {
    group = "samples"
    description = "Run the interactive Box2D 3.1.1 Java sample browser with the desktop FFM binding."
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
