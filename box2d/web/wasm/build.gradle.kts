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
    api("com.github.xpenatan.jParser:runtime-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-web:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-web_wasm:${LibExt.jParserVersion}")
    api("org.teavm:teavm-jso:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-core:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-classlib:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso-apis:${LibExt.teaVMVersion}")
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
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            groupId = LibExt.groupId
            version = LibExt.libVersion
            from(components["java"])
        }
    }
}
