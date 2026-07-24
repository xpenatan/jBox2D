plugins {
    id("java-library")
}

dependencies {
    implementation(project(":samples:shared"))
    compileOnlyApi(project(":box2d:core"))
    api(project(":extensions:gdx:gl"))
    api(libs.gdxCore)
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}
