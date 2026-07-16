import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":samples:core"))
    implementation(project(":box2d:desktop:c"))
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
}

val sampleMainClass = "com.github.xpenatan.box2d.sample.desktop.Box2DDesktopLauncher"

gdxTeaVM {
    reflection.add("com.badlogic.gdx.math.Vector2")
    reflection.add("com.badlogic.gdx.math.Vector3")

    glfw {
        mainClass.set(sampleMainClass)
        targetFileName.set("jbox2d-gdx")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        obfuscated.set(false)
        minHeapSizeMb.set(64)
        maxHeapSizeMb.set(512)
        buildType.set("Debug")
        consoleLog.set(true)
    }
}
