import org.gradle.api.attributes.java.TargetJvmVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("java")
}

val box2dRuntimeName = "jni"
val box2dRuntimeProject = ":box2d:desktop:jni"

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFfm.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFfm.get())
}

val glRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val vulkanRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val wgpuJniRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes {
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, libs.versions.javaFfm.get().toInt())
    }
}

val box2dRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":samples:fdx:core"))
    implementation(libs.fdxBackendDesktop)
    implementation(libs.fdxWgpuCore)

    glRuntimeClasspath(libs.fdxGlDesktop)
    vulkanRuntimeClasspath(libs.fdxVulkanDesktop)
    wgpuJniRuntimeClasspath(libs.fdxWgpuDesktopJni)
    box2dRuntimeClasspath(project(box2dRuntimeProject))
}

val sampleMainClass = "com.github.xpenatan.box2d.sample.fdx.desktop.Box2DFdxDesktopLauncher"

fun Task.configureRuntimeInputs(providerClasspath: FileCollection) {
    dependsOn("$box2dRuntimeProject:jar")
    inputs.files(providerClasspath)
    inputs.files(box2dRuntimeClasspath)
}

fun JavaExec.configureSampleRun(descriptionText: String, graphics: String, graphicsLabel: String,
                                providerClasspath: FileCollection) {
    group = "samples"
    description = descriptionText
    mainClass.set(sampleMainClass)
    classpath = box2dRuntimeClasspath + sourceSets["main"].runtimeClasspath + providerClasspath
    systemProperty("jbox2d.sample.graphics", graphics)
    systemProperty("jbox2d.sample.graphicsLabel", graphicsLabel)
    listOf(
        "jbox2d.sample.sample",
        "jbox2d.sample.sampleIndex",
        "jbox2d.sample.exitAfterFrames",
        "jbox2d.sample.visible"
    ).forEach { property ->
        System.getProperty(property)?.takeIf { it.isNotBlank() }?.let { systemProperty(property, it) }
    }
}

fun JavaExec.useJava25Launcher() {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.javaFfm.get().toInt()))
    })
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

fun registerDesktopSampleBuild(taskName: String, descriptionText: String, providerClasspath: FileCollection) {
    tasks.register(taskName) {
        group = "samples"
        description = descriptionText
        dependsOn("classes")
        configureRuntimeInputs(providerClasspath)
    }
}

registerDesktopSampleBuild("box2d_fdx_desktop_gl_${box2dRuntimeName}_build",
    "Builds the jBox2D libfdx desktop OpenGL sample with Box2D JNI.", glRuntimeClasspath)
registerDesktopSampleBuild("box2d_fdx_desktop_wgpu_${box2dRuntimeName}_build",
    "Builds the jBox2D libfdx desktop WGPU sample with Box2D JNI.", wgpuJniRuntimeClasspath)
registerDesktopSampleBuild("box2d_fdx_desktop_vulkan_${box2dRuntimeName}_build",
    "Builds the jBox2D libfdx desktop Vulkan sample with Box2D JNI.", vulkanRuntimeClasspath)

tasks.register<JavaExec>("box2d_fdx_desktop_gl_${box2dRuntimeName}_run") {
    configureSampleRun("Runs the jBox2D libfdx desktop OpenGL sample with Box2D JNI.",
        "gl", "OpenGL", glRuntimeClasspath)
    dependsOn("box2d_fdx_desktop_gl_${box2dRuntimeName}_build")
    useJava25Launcher()
}

tasks.register<JavaExec>("box2d_fdx_desktop_wgpu_${box2dRuntimeName}_run") {
    configureSampleRun("Runs the jBox2D libfdx desktop WGPU sample with Box2D JNI.",
        "wgpu", "WGPU JNI", wgpuJniRuntimeClasspath)
    dependsOn("box2d_fdx_desktop_wgpu_${box2dRuntimeName}_build")
    useJava25Launcher()
}

tasks.register<JavaExec>("box2d_fdx_desktop_vulkan_${box2dRuntimeName}_run") {
    configureSampleRun("Runs the jBox2D libfdx desktop Vulkan sample with Box2D JNI.",
        "vulkan", "Vulkan", vulkanRuntimeClasspath)
    dependsOn("box2d_fdx_desktop_vulkan_${box2dRuntimeName}_build")
    useJava25Launcher()
}
