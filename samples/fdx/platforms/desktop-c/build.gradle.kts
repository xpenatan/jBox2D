import org.gradle.api.attributes.java.TargetJvmVersion

plugins {
    id("java")
}

val box2dRuntimeName = "c"
val box2dRuntimeProject = ":box2d:desktop:c"

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
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
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, LibExt.javaFFMTarget.toInt())
    }
}

val box2dRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":samples:fdx:core"))
    implementation("io.github.libfdx:backend_desktop:${LibExt.fdxVersion}")
    implementation("io.github.libfdx:wgpu_core:${LibExt.fdxVersion}")

    glRuntimeClasspath("io.github.libfdx:gl_desktop:${LibExt.fdxVersion}")
    vulkanRuntimeClasspath("io.github.libfdx:vulkan_desktop:${LibExt.fdxVersion}")
    wgpuJniRuntimeClasspath("io.github.libfdx:wgpu_desktop_jni:${LibExt.fdxVersion}")
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
