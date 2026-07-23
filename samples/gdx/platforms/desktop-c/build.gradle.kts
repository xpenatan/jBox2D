import org.teavm.gradle.api.OptimizationLevel

plugins {
    alias(libs.plugins.gdxTeavm)
}

dependencies {
    implementation(libs.gdxCore)
    implementation(project(":samples:gdx:core"))
    implementation(project(":box2d:desktop:c"))
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

val sampleMainClass = "com.github.xpenatan.box2d.sample.gdx.desktop.Box2DGdxDesktopLauncher"

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
