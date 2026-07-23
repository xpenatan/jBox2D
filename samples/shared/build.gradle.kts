plugins {
    id("java-library")
}

dependencies {
    compileOnlyApi(project(":box2d:core"))
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}
