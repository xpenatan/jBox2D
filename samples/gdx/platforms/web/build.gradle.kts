plugins {
    alias(libs.plugins.gdxTeavm)
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaWeb.get())
}

dependencies {
    implementation(project(":samples:gdx:core"))
    implementation(project(":box2d:web:wasm"))
}

val sampleMainClass = "com.github.xpenatan.box2d.sample.gdx.web.Box2DGdxWebLauncher"
val box2dVersion = libs.versions.box2d.get()

gdxTeaVM {
    js {
        mainClass.set(sampleMainClass)
        htmlTitle.set("jBox2D $box2dVersion Samples - Web JS")
        htmlWidth.set(0)
        htmlHeight.set(0)
        serverPort.set(8081)
        processMemory.set(3072)
        obfuscated.set(false)
    }
    wasm {
        mainClass.set(sampleMainClass)
        htmlTitle.set("jBox2D $box2dVersion Samples - Web Wasm")
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

tasks.register<Sync>("box2d_gdx_samples_pages_build") {
    group = "samples"
    description = "Builds and stages the jBox2D libGDX and libfdx browser samples for GitHub Pages."
    dependsOn(
        "gdx_teavm_web_js_build",
        "gdx_teavm_web_wasm_build",
        ":samples:fdx:platforms:web:box2d_fdx_webgl_js_build",
        ":samples:fdx:platforms:web:box2d_fdx_webgl_wasm_build",
        ":samples:fdx:platforms:web:box2d_fdx_webgpu_js_build"
    )

    into(pagesDirectory)
    from(layout.buildDirectory.dir("dist/js/webapp")) {
        into("gdx/gl/js")
        exclude("WEB-INF/**")
    }
    from(layout.buildDirectory.dir("dist/wasm/webapp")) {
        into("gdx/gl/wasm")
        exclude("WEB-INF/**")
    }
    from(project(":samples:fdx:platforms:web").layout.buildDirectory.dir("dist/web-js/webapp")) {
        into("fdx/gl/js")
        exclude("webgpu.html")
    }
    from(project(":samples:fdx:platforms:web").layout.buildDirectory.dir("dist/web-wasm/webapp")) {
        into("fdx/gl/wasm")
        exclude("webgpu.html")
    }
    from(project(":samples:fdx:platforms:web").layout.buildDirectory.dir("dist/web-js/webapp")) {
        into("fdx/webgpu/js")
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
            "gdx/gl/wasm/scripts/box2d.wasm",
            "fdx/gl/js/index.html",
            "fdx/gl/js/scripts/box2d.js",
            "fdx/gl/js/scripts/box2d.wasm",
            "fdx/gl/wasm/index.html",
            "fdx/gl/wasm/scripts/box2d.js",
            "fdx/gl/wasm/scripts/box2d.wasm",
            "fdx/webgpu/js/webgpu.html"
        )
        val missing = requiredFiles.filterNot { pagesDirectory.get().file(it).asFile.isFile }
        if(missing.isNotEmpty()) {
            throw GradleException("Incomplete GitHub Pages site. Missing: ${missing.joinToString()}")
        }
    }
}
