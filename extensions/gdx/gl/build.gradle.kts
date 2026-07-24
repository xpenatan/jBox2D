plugins {
    id("java-library")
}

val moduleName = "gdx-gl"

base {
    archivesName.set(moduleName)
}

dependencies {
    compileOnly(project(":box2d:core"))
    api(libs.gdxCore)

    testImplementation(project(":box2d:core"))
    testImplementation(libs.junit)
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
