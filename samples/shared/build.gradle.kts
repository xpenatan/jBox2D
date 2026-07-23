plugins {
    id("java-library")
}

dependencies {
    compileOnlyApi(project(":box2d:core"))
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
}
