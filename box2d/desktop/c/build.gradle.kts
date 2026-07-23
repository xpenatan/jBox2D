plugins {
    id("java-library")
}

val moduleName = "desktop-c"
val nativeResourceRoot = "external_cpp/jparser/box2d/native"
val nativeRoot = file("$projectDir/../../builder/build/c++/libs")
data class NativeResource(val sourcePath: String, val platform: String)
val nativeResources = listOf(
    NativeResource("$nativeRoot/windows/vc/teavm_c/box2d64_.lib", "windows_x64"),
    NativeResource("$nativeRoot/windows/vc/teavm_c/box2d64.lib", "windows_x64"),
    NativeResource("$nativeRoot/windows/vc/teavm_c/box2d64.dll", "windows_x64"),
    NativeResource("$nativeRoot/linux/teavm_c/libbox2d64_.a", "linux_x64"),
    NativeResource("$nativeRoot/linux/teavm_c/libbox2d64.so", "linux_x64"),
    NativeResource("$nativeRoot/mac/teavm_c/libbox2d64_.a", "mac_x64"),
    NativeResource("$nativeRoot/mac/teavm_c/libbox2d64.dylib", "mac_x64"),
    NativeResource("$nativeRoot/mac/arm/teavm_c/libbox2d64_.a", "mac_arm64"),
    NativeResource("$nativeRoot/mac/arm/teavm_c/libbox2darm64.dylib", "mac_arm64")
)

base { archivesName.set(moduleName) }

tasks.named<Jar>("jar") {
    nativeResources.forEach { resource ->
        from(provider { listOf(file(resource.sourcePath)).filter { it.exists() } }) {
            into("$nativeResourceRoot/${resource.platform}")
        }
    }
}

dependencies {
    api(project(":box2d:shared:c"))
    implementation(libs.jparserRuntimeDesktopCWindowsX64)
    implementation(libs.jparserRuntimeDesktopCLinuxX64)
    implementation(libs.jparserRuntimeDesktopCMacX64)
    implementation(libs.jparserRuntimeDesktopCMacArm64)
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
