plugins {
    id("java-library")
}

dependencies {
    implementation(project(":samples:shared"))
    compileOnlyApi(project(":box2d:core"))
    api("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
}
