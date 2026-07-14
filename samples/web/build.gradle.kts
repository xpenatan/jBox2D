plugins {
    id("com.github.xpenatan.gdx-teavm")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaWebTarget)
}

dependencies {
    implementation(project(":samples:core"))
    implementation(project(":box2d:web:wasm"))
}

val sampleMainClass = "com.github.xpenatan.box2d.sample.web.Box2DWebLauncher"

gdxTeaVM {
    js {
        mainClass.set(sampleMainClass)
        htmlTitle.set("jBox2D 3.1.1 Samples - Web JS")
        htmlWidth.set(0)
        htmlHeight.set(0)
        serverPort.set(8081)
        processMemory.set(3072)
        obfuscated.set(false)
    }
    wasm {
        mainClass.set(sampleMainClass)
        htmlTitle.set("jBox2D 3.1.1 Samples - Web Wasm")
        htmlWidth.set(0)
        htmlHeight.set(0)
        serverPort.set(8082)
        processMemory.set(3072)
        obfuscated.set(false)
        strict.set(false)
    }
}

val box2dWebRuntime = listOf(
    project(":box2d:builder").layout.buildDirectory.file("c++/libs/emscripten/box2d.js"),
    project(":box2d:builder").layout.buildDirectory.file("c++/libs/emscripten/box2d.wasm")
)

fun Task.stageBox2DWebRuntime(distribution: String) {
    dependsOn(":box2d:builder:jParser_build_web_wasm")
    doLast {
        val scriptsDir = layout.buildDirectory.dir("dist/$distribution/webapp/scripts").get().asFile
        project.copy {
            from(box2dWebRuntime)
            into(scriptsDir)
        }
        val missing = listOf("box2d.js", "box2d.wasm").filterNot { scriptsDir.resolve(it).isFile }
        if(missing.isNotEmpty()) {
            throw GradleException("Missing jBox2D web runtime files: ${missing.joinToString()}")
        }
    }
}

tasks.matching { it.name == "gdx_teavm_web_js_build" }.configureEach {
    stageBox2DWebRuntime("js")
}

tasks.matching { it.name == "gdx_teavm_web_wasm_build" }.configureEach {
    stageBox2DWebRuntime("wasm")
}

val pagesDirectory = layout.buildDirectory.dir("pages")

tasks.register<Sync>("box2d_samples_pages_build") {
    group = "samples"
    description = "Builds and stages the jBox2D browser samples for GitHub Pages."
    dependsOn("gdx_teavm_web_js_build", "gdx_teavm_web_wasm_build")

    into(pagesDirectory)
    from(layout.buildDirectory.dir("dist/js/webapp")) {
        into("gdx/gl/js")
        exclude("WEB-INF/**")
    }
    from(layout.buildDirectory.dir("dist/wasm/webapp")) {
        into("gdx/gl/wasm")
        exclude("WEB-INF/**")
    }
    from(layout.projectDirectory.dir("src/main/pages"))

    doLast {
        val requiredFiles = listOf(
            "index.html",
            "gdx/gl/js/index.html",
            "gdx/gl/js/scripts/box2d.js",
            "gdx/gl/js/scripts/box2d.wasm",
            "gdx/gl/wasm/index.html",
            "gdx/gl/wasm/scripts/box2d.js",
            "gdx/gl/wasm/scripts/box2d.wasm"
        )
        val missing = requiredFiles.filterNot { pagesDirectory.get().file(it).asFile.isFile }
        if(missing.isNotEmpty()) {
            throw GradleException("Incomplete GitHub Pages site. Missing: ${missing.joinToString()}")
        }
    }
}
