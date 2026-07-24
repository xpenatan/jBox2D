plugins {
    id("java-library")
}

val moduleName = "web-wasm"
val nativePaths = listOf(
    "$projectDir/../../builder/build/c++/libs/emscripten/box2d.js",
    "$projectDir/../../builder/build/c++/libs/emscripten/box2d.wasm"
)

base { archivesName.set(moduleName) }

tasks.named<Jar>("jar") {
    dependsOn(":box2d:builder:jParser_build_web_wasm")
    from(provider { nativePaths.map(::file).filter { it.exists() } })
}

dependencies {
    api(project(":box2d:core"))
    api(libs.jparserRuntimeCore)
    api(libs.jparserRuntimeWeb)
    api(libs.jparserRuntimeWebWasm)
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java", "src/main/support/java"))
        resources.setSrcDirs(listOf("src/main/resources"))
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
