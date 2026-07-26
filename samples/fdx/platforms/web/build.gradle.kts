plugins {
    id("java")
    alias(libs.plugins.libfdx)
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaFfm.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaFfm.get())
}

val useMavenArtifacts = rootProject.extra["jbox2dSamplesUseMavenArtifacts"] as Boolean
val box2dWebRuntimeClasspath = configurations.create("box2dWebRuntimeClasspath") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":samples:fdx:core"))
    implementation(project(":box2d:web:wasm"))
    implementation(libs.fdxBackendWeb)
    implementation(libs.fdxGlWeb)
    implementation(libs.fdxWgpuWeb)
    box2dWebRuntimeClasspath(project(":box2d:web:wasm")) {
        isTransitive = false
    }
}

libfdx {
    js {
        mainClass.set("com.github.xpenatan.box2d.sample.fdx.web.Box2DFdxWebJsLauncher")
        htmlTitle.set("jBox2D libfdx - WebGL JS")
        canvasId.set("libfdx-canvas")
        htmlWidth.set(0)
        htmlHeight.set(0)
    }
    wasm {
        mainClass.set("com.github.xpenatan.box2d.sample.fdx.web.Box2DFdxWebWasmLauncher")
        htmlTitle.set("jBox2D libfdx - WebGL Wasm")
        canvasId.set("libfdx-canvas")
        htmlWidth.set(0)
        htmlHeight.set(0)
    }
}

val jsWebappDir = layout.buildDirectory.dir("dist/web-js/webapp")
val wasmWebappDir = layout.buildDirectory.dir("dist/web-wasm/webapp")
val box2dWebRuntime = if(useMavenArtifacts) {
    box2dWebRuntimeClasspath.incoming.files.elements.map { files ->
        files.map { zipTree(it.asFile) }
    }
} else {
    listOf(
        project(":box2d:builder").layout.buildDirectory.file("c++/libs/emscripten/box2d.js"),
        project(":box2d:builder").layout.buildDirectory.file("c++/libs/emscripten/box2d.wasm")
    )
}

fun registerBox2DRuntimeScriptCopy(
    taskName: String,
    webBuildTaskName: String,
    webappDir: Provider<Directory>
): TaskProvider<Task> {
    return tasks.register(taskName) {
        dependsOn(webBuildTaskName)
        if(!useMavenArtifacts) {
            dependsOn(":box2d:builder:jParser_build_web_wasm")
        }
        inputs.files(box2dWebRuntime)
        outputs.file(webappDir.map { it.file("scripts/box2d.js") })
        outputs.file(webappDir.map { it.file("scripts/box2d.wasm") })
        doLast {
            val scriptsDir = webappDir.get().dir("scripts").asFile
            val scriptNames = setOf("box2d.js", "box2d.wasm")
            project.delete(scriptNames.map { File(scriptsDir, it) })
            project.copy {
                from(box2dWebRuntime) {
                    include("box2d.js", "box2d.wasm")
                }
                into(scriptsDir)
            }
            val missing = scriptNames.filterNot { File(scriptsDir, it).isFile }
            if(missing.isNotEmpty()) {
                val sourceHint = if(useMavenArtifacts) {
                    "The resolved jBox2D web-wasm Maven artifact does not contain the runtime scripts."
                } else {
                    "Run :box2d:builder:jParser_build_web_wasm before building the web sample."
                }
                throw GradleException(
                    "Missing jBox2D web runtime scripts: ${missing.joinToString()}. " +
                            sourceHint
                )
            }
        }
    }
}

val copyBox2DJsRuntimeScripts = registerBox2DRuntimeScriptCopy(
    "copyBox2DJsRuntimeScripts",
    "libfdx_web_js_build",
    jsWebappDir
)
val copyBox2DWasmRuntimeScripts = registerBox2DRuntimeScriptCopy(
    "copyBox2DWasmRuntimeScripts",
    "libfdx_web_wasm_build",
    wasmWebappDir
)

tasks.register("box2d_fdx_webgl_js_build") {
    group = "samples"
    description = "Builds the jBox2D libfdx WebGL JavaScript sample."
    dependsOn("libfdx_web_js_build", copyBox2DJsRuntimeScripts)
}

tasks.register("box2d_fdx_webgl_wasm_build") {
    group = "samples"
    description = "Builds the jBox2D libfdx WebGL Wasm sample."
    dependsOn("libfdx_web_wasm_build", copyBox2DWasmRuntimeScripts)
}

tasks.register("box2d_fdx_webgpu_js_build") {
    group = "samples"
    description = "Builds the jBox2D libfdx WebGPU JavaScript sample."
    dependsOn("libfdx_web_js_build", copyBox2DJsRuntimeScripts)
    configureWebGpuPage("dist/web-js/webapp", "jBox2D libfdx - WebGPU JS")
}

tasks.register<io.github.libfdx.gradle.LibfdxRunWebTask>("box2d_fdx_webgl_js_run") {
    group = "samples"
    description = "Builds and serves the jBox2D libfdx WebGL JavaScript sample."
    dependsOn("box2d_fdx_webgl_js_build")
    webappDir.set(jsWebappDir)
    port.set(libfdx.js.serverPort)
    defaultPath.set("/")
}

tasks.register<io.github.libfdx.gradle.LibfdxRunWebTask>("box2d_fdx_webgl_wasm_run") {
    group = "samples"
    description = "Builds and serves the jBox2D libfdx WebGL Wasm sample."
    dependsOn("box2d_fdx_webgl_wasm_build")
    webappDir.set(wasmWebappDir)
    port.set(libfdx.wasm.serverPort)
    defaultPath.set("/")
}

tasks.register<io.github.libfdx.gradle.LibfdxRunWebTask>("box2d_fdx_webgpu_js_run") {
    group = "samples"
    description = "Builds and serves the jBox2D libfdx WebGPU JavaScript sample."
    dependsOn("box2d_fdx_webgpu_js_build")
    webappDir.set(jsWebappDir)
    port.set(libfdx.js.serverPort)
    defaultPath.set("/webgpu.html")
}

fun Task.configureWebGpuPage(webappPath: String, title: String) {
    val webappDir = layout.buildDirectory.dir(webappPath)
    val indexFile = webappDir.map { it.file("index.html") }
    val loaderFile = webappDir.map { it.file("scripts/fdx-loader.js") }
    val outputFile = webappDir.map { it.file("webgpu.html") }
    val outputLoaderFile = webappDir.map { it.file("scripts/fdx-webgpu-loader.js") }
    inputs.files(indexFile, loaderFile)
    outputs.files(outputFile, outputLoaderFile)
    doLast {
        writeWebGpuPage(
            indexFile.get().asFile,
            loaderFile.get().asFile,
            outputFile.get().asFile,
            outputLoaderFile.get().asFile,
            title
        )
    }
}

fun writeWebGpuPage(
    indexFile: File,
    loaderFile: File,
    outputFile: File,
    outputLoaderFile: File,
    title: String
) {
    val source = indexFile.readText()
    val withTitle = source.replace(Regex("<title>.*</title>"), "<title>$title</title>")
    val defaultLoader = "scripts/fdx-loader.js"
    val webGpuLoader = "scripts/fdx-webgpu-loader.js"
    if(!withTitle.contains(defaultLoader)) {
        throw GradleException("Could not locate the libfdx loader in ${indexFile.absolutePath}")
    }
    outputFile.writeText(withTitle.replace(defaultLoader, webGpuLoader))

    val loaderSource = loaderFile.readText()
    val defaultArgs = "mainClassArgs: [],"
    if(!loaderSource.contains(defaultArgs)) {
        throw GradleException("Could not configure WebGPU arguments in ${loaderFile.absolutePath}")
    }
    val loaderWithGraphicsArg = loaderSource.replace(
        defaultArgs,
        "mainClassArgs: [\"--graphics=webgpu\"],"
    )
    val spreadEntryCall = "return entry.apply(root, config.mainClassArgs);"
    if(!loaderWithGraphicsArg.contains(spreadEntryCall)) {
        throw GradleException("Could not configure WebGPU entry arguments in ${loaderFile.absolutePath}")
    }
    outputLoaderFile.writeText(
        loaderWithGraphicsArg.replace(
            spreadEntryCall,
            "return entry(config.mainClassArgs);"
        )
    )
}
