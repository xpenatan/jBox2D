plugins {
    id("java-library")
}

val moduleName = "fdx"

base {
    archivesName.set(moduleName)
}

dependencies {
    compileOnly(project(":box2d:core"))
    api("io.github.libfdx:graphics:${LibExt.fdxVersion}")
    api("io.github.libfdx:camera:${LibExt.fdxVersion}")

    testImplementation(project(":box2d:core"))
    testImplementation("junit:junit:${LibExt.jUnitVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
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
