import com.github.xpenatan.jParser.builder.targets.AndroidTarget
import com.github.xpenatan.jParser.builder.targets.SourceLanguage
import com.github.xpenatan.jParser.gradle.JParserTargets
import java.io.File

plugins {
    id("java-library")
    id("com.github.xpenatan.jparser")
}

fun File.normalizedPath(): String = absolutePath.replace('\\', '/')

val downloadBuildDir = file("../download/build")
val box2dSourceRoot = File(downloadBuildDir, "box2d-source")
val box2dIncludeDir = File(box2dSourceRoot, "include")
val box2dPrivateSourceDir = File(box2dSourceRoot, "src")
val box2dCustomSourceDir = file("src/main/cpp/custom")
val box2dSourcePattern = "${box2dPrivateSourceDir.normalizedPath()}/*.c"
val box2dTimerSource = File(box2dPrivateSourceDir, "timer.c")
val box2dWebTimerSource = File(box2dCustomSourceDir, "box2d_web_timer.c")

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaMainTarget)
}

val jParserTargetNames = listOf(
    JParserTargets.WEB_WASM,
    JParserTargets.WINDOWS64_JNI,
    JParserTargets.LINUX64_JNI,
    JParserTargets.MAC64_JNI,
    JParserTargets.MAC_ARM_JNI,
    JParserTargets.ANDROID_JNI,
    JParserTargets.IOS_JNI,
    JParserTargets.WINDOWS64_FFM,
    JParserTargets.LINUX64_FFM,
    JParserTargets.MAC64_FFM,
    JParserTargets.MAC_ARM_FFM,
    JParserTargets.WINDOWS64_TEAVM_C,
    JParserTargets.LINUX64_TEAVM_C,
    JParserTargets.MAC64_TEAVM_C,
    JParserTargets.MAC_ARM_TEAVM_C,
    JParserTargets.ANDROID_TEAVM_C
)
val windowsTargetNames = setOf(
    JParserTargets.WINDOWS64_JNI,
    JParserTargets.WINDOWS64_FFM,
    JParserTargets.WINDOWS64_TEAVM_C
)
val linuxTargetNames = setOf(
    JParserTargets.LINUX64_JNI,
    JParserTargets.LINUX64_FFM,
    JParserTargets.LINUX64_TEAVM_C
)

jParser {
    libName.set("box2d")
    modulePrefix.set("")
    modulePath(file(".."))
    moduleBuildSuffix.set("builder")
    moduleBaseSuffix.set("base")
    moduleCoreSuffix.set("core")
    moduleJNISuffix.set("shared/jni")
    moduleFFMSuffix.set("desktop/ffm")
    moduleWebSuffix.set("web/wasm")
    moduleCSuffix.set("shared/c")
    packageName.set("com.github.xpenatan.box2d")
    cppSourcePath(box2dSourceRoot)
    sourceLanguage.set(SourceLanguage.C)
    cStandard.set("c17")
    // Box2D itself remains C17; only the generated language facade is C++.
    jniCppStandard.set("c++20")
    ffmCppStandard.set("c++20")
    teaVMCCppStandard.set("c++20")
    webCppStandard.set("c++20")
    androidApiLevel.set(AndroidTarget.ApiLevel.Android_10_29)

    native {
        dependsOn(":box2d:download:box2d_download_source")
        headerDir(box2dIncludeDir)
        headerDir(box2dPrivateSourceDir)
        headerDir(box2dCustomSourceDir)
        cppInclude(box2dSourcePattern)
        includeDefaultSources.set(false)
        includeCustomSources.set(false)

        jParserTargetNames.forEach { targetName ->
            target(targetName) {
                includeDefaultSources.set(false)
                includeCustomSources.set(false)
                if(targetName in windowsTargetNames) {
                    compileFlag("/MP2")
                    compileFlag("/Zm200")
                }
                else {
                    compileFlag("-ffp-contract=off")
                }
                if(targetName in linuxTargetNames) {
                    linkerFlag("-lm")
                }
                if(targetName == JParserTargets.ANDROID_JNI || targetName == JParserTargets.ANDROID_TEAVM_C) {
                    androidTarget(AndroidTarget.Target.armeabi_v7a) {
                        compileFlag("-DBOX2D_DISABLE_SIMD")
                    }
                }
                if(targetName == JParserTargets.WEB_WASM) {
                    cppExclude(box2dTimerSource)
                    cppInclude(box2dWebTimerSource)
                    compileFlag("-msimd128")
                    compileFlag("-msse2")
                    linkerFlag("-msimd128")
                    linkerFlag("-msse2")
                }
            }
        }
    }
}
