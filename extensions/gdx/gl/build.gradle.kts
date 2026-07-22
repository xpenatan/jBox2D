plugins {
    id("java-library")
}

val moduleName = "gdx-gl"

base {
    archivesName.set(moduleName)
}

dependencies {
    compileOnly(project(":box2d:core"))
    api("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation(project(":box2d:core"))
    testImplementation("junit:junit:${LibExt.jUnitVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
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
