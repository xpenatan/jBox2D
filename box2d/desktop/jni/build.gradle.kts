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

tasks.named<Test>("test") {
    jvmArgs("--enable-native-access=ALL-UNNAMED")

    val isolatedTestTmp = layout.buildDirectory.dir("tmp/native-tests").get().asFile
    systemProperty("java.io.tmpdir", isolatedTestTmp.absolutePath)
    doFirst {
        isolatedTestTmp.mkdirs()
    }
}

dependencies {
    api(project(":box2d:shared:jni"))
    implementation(libs.jparserRuntimeDesktopJniWindowsX64)
    implementation(libs.jparserRuntimeDesktopJniLinuxX64)
    implementation(libs.jparserRuntimeDesktopJniMacX64)
    implementation(libs.jparserRuntimeDesktopJniMacArm64)
    testImplementation(libs.junit)
    testImplementation(project(":samples:shared"))
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
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
