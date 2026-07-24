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
    implementation(libs.jparserRuntimeDesktopFfm)
    implementation(libs.jparserRuntimeDesktopFfmWindowsX64)
    implementation(libs.jparserRuntimeDesktopFfmLinuxX64)
    implementation(libs.jparserRuntimeDesktopFfmMacX64)
    implementation(libs.jparserRuntimeDesktopFfmMacArm64)
    implementation(libs.jparserApiCore)
    implementation(libs.jparserLoaderCore)
}

sourceSets {
    main { java.setSrcDirs(listOf("src/main/java")) }
}

tasks.named("clean") {
    doFirst { project.delete(files("$projectDir/src/main/java")) }
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
