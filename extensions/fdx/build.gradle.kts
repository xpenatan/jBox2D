plugins {
    id("java-library")
}

val moduleName = "fdx"

base {
    archivesName.set(moduleName)
}

dependencies {
    compileOnly(project(":box2d:core"))
    api(libs.fdxGraphics)
    api(libs.fdxCamera)

    testImplementation(project(":box2d:core"))
    testImplementation(libs.junit)
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFfm.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFfm.get())
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
