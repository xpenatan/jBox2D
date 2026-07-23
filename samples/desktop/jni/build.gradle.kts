plugins {
    id("java")
}

val box2dRuntimeName = "jni"
val box2dRuntimeProject = ":box2d:desktop:jni"
val box2dRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":samples:core"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")

    box2dRuntimeClasspath(project(box2dRuntimeProject))
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
}

val sampleMainClass = "com.github.xpenatan.box2d.sample.desktop.Box2DDesktopLauncher"

fun Task.configureRuntimeInputs() {
    dependsOn("$box2dRuntimeProject:jar")
    inputs.files(box2dRuntimeClasspath)
}

tasks.register("box2d_sample_${box2dRuntimeName}_build") {
    group = "samples"
    description = "Build the Box2D 3.1.1 desktop sample with the JNI binding."
    dependsOn("classes")
    configureRuntimeInputs()
}

tasks.register<JavaExec>("box2d_sample_jni_run") {
    group = "samples"
    description = "Run the interactive Box2D 3.1.1 Java sample browser with the desktop JNI binding."
    dependsOn("box2d_sample_${box2dRuntimeName}_build")
    mainClass.set(sampleMainClass)
    classpath = box2dRuntimeClasspath + sourceSets["main"].runtimeClasspath
    listOf(
        "jbox2d.sample.sample",
        "jbox2d.sample.sampleIndex",
        "jbox2d.sample.exitAfterFrames",
        "jbox2d.sample.screenshot"
    ).forEach { property ->
        System.getProperty(property)?.takeIf { it.isNotBlank() }?.let { systemProperty(property, it) }
    }
}
