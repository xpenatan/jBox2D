plugins {
    id("java-library")
}

dependencies {
    implementation(project(":samples:shared"))
    compileOnly(project(":box2d:core"))
    api(project(":extensions:fdx"))

    api(libs.fdxApplication)
    api(libs.fdxCamera)
    api(libs.fdxDisplay)
    api(libs.fdxGraphics)
    api(libs.fdxUiKit)
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFfm.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFfm.get())
}
