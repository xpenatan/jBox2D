plugins {
    id("java-library")
}

val moduleName = "desktop-jni"
val nativeRoot = file("$projectDir/../../builder/build/c++/libs")
val nativePaths = listOf(
    "$nativeRoot/windows/vc/jni/box2d64.dll",
    "$nativeRoot/linux/jni/libbox2d64.so",
    "$nativeRoot/mac/jni/libbox2d64.dylib",
    "$nativeRoot/mac/arm/jni/libbox2darm64.dylib"
)

base { archivesName.set(moduleName) }

tasks.named<Jar>("jar") {
    from(provider { nativePaths.map(::file).filter { it.exists() } })
}

tasks.named<Copy>("processTestResources") {
    from(provider { nativePaths.map(::file).filter { it.exists() } })
}

val currentPlatformBuildTask = when {
    System.getProperty("os.name").lowercase().contains("windows") && System.getProperty("os.arch").contains("64") -> ":box2d:builder:jParser_build_windows64_jni"
    System.getProperty("os.name").lowercase().contains("linux") && System.getProperty("os.arch").contains("64") -> ":box2d:builder:jParser_build_linux64_jni"
    System.getProperty("os.name").lowercase().contains("mac") && System.getProperty("os.arch").lowercase().let { it.contains("aarch64") || it.contains("arm64") } -> ":box2d:builder:jParser_build_macArm_jni"
    System.getProperty("os.name").lowercase().contains("mac") && System.getProperty("os.arch").contains("64") -> ":box2d:builder:jParser_build_mac64_jni"
    else -> null
}

tasks.named<Copy>("processTestResources") {
    currentPlatformBuildTask?.let { dependsOn(it) }
}

tasks.named<Test>("test") {
    currentPlatformBuildTask?.let { dependsOn(it) }
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

dependencies {
    api(project(":box2d:shared:jni"))
    implementation("com.github.xpenatan.jParser:runtime-desktop-jni_windows_x64:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-desktop-jni_linux_x64:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-desktop-jni_mac_x64:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-desktop-jni_mac_arm64:${LibExt.jParserVersion}")
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
