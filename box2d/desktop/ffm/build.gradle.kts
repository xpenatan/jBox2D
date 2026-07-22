plugins {
    id("java-library")
}

val moduleName = "desktop-ffm"
val nativeRoot = file("$projectDir/../../builder/build/c++/libs")
val nativePaths = listOf(
    "$nativeRoot/windows/vc/ffm/box2d64.dll",
    "$nativeRoot/linux/ffm/libbox2d64.so",
    "$nativeRoot/mac/ffm/libbox2d64.dylib",
    "$nativeRoot/mac/arm/ffm/libbox2darm64.dylib"
)

base { archivesName.set(moduleName) }

tasks.named<Jar>("jar") {
    from(provider { nativePaths.map(::file).filter { it.exists() } })
}

dependencies {
    implementation("com.github.xpenatan.jParser:runtime-desktop-ffm:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-desktop-ffm_windows_x64:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-desktop-ffm_linux_x64:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-desktop-ffm_mac_x64:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-desktop-ffm_mac_arm64:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
}

sourceSets {
    main { java.setSrcDirs(listOf("src/main/java")) }
}

tasks.named("clean") {
    doFirst { project.delete(files("$projectDir/src/main/java")) }
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
