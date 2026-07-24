import org.gradle.api.attributes.java.TargetJvmVersion

plugins {
    id("java")
}

val box2dRuntimeName = "c"
val box2dRuntimeProject = ":box2d:desktop:c"

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

fun Task.configureRuntimeInputs(providerClasspath: FileCollection) {
    dependsOn("$box2dRuntimeProject:jar")
    inputs.files(providerClasspath)
    inputs.files(box2dRuntimeClasspath)
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
    "Builds the jBox2D libfdx desktop OpenGL sample compile path with Box2D TeaVM C.", glRuntimeClasspath)
registerDesktopSampleBuild("box2d_fdx_desktop_wgpu_${box2dRuntimeName}_build",
    "Builds the jBox2D libfdx desktop WGPU sample compile path with Box2D TeaVM C.", wgpuJniRuntimeClasspath)
registerDesktopSampleBuild("box2d_fdx_desktop_vulkan_${box2dRuntimeName}_build",
    "Builds the jBox2D libfdx desktop Vulkan sample compile path with Box2D TeaVM C.", vulkanRuntimeClasspath)
