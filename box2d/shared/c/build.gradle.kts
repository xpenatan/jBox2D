plugins {
    id("java-library")
}

val moduleName = "shared-c"
val generatedResourcesDir = layout.buildDirectory.dir("generated/jparser/resources/main")

base { archivesName.set(moduleName) }

dependencies {
    api(libs.jparserApiCore)
    api(libs.jparserLoaderCore)
    api(libs.jparserRuntimeCore)
    api(libs.jparserRuntimeC)
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java"))
        java.include("gen/c/**/*.java")
        resources.setSrcDirs(listOf("src/main/resources", generatedResourcesDir))
    }
}

tasks.named("clean") {
    doFirst { project.delete(files("$projectDir/src/main/java")) }
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
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
