import org.gradle.api.file.RelativePath
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    id("java")
}

val box2dVersion = libs.versions.box2d.get()

val box2dSourceRoot = layout.buildDirectory.dir("box2d-source").get().asFile
val box2dArchiveFile = layout.buildDirectory.file("tmp/box2d-source.zip").get().asFile

tasks.register("box2d_download_source") {
    group = "box2d"
    description = "Download Box2D ${box2dVersion} source into the build directory."
    inputs.property("box2dVersion", box2dVersion)
    outputs.dir(box2dSourceRoot)

    doLast {
        val url = "https://github.com/erincatto/box2d/archive/refs/tags/v${box2dVersion}.zip"
        println("Downloading $url")
        delete(box2dSourceRoot)
        box2dArchiveFile.parentFile.mkdirs()
        URI.create(url).toURL().openStream().use { input ->
            Files.copy(input, box2dArchiveFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        copy {
            from(zipTree(box2dArchiveFile)) {
                eachFile {
                    val strippedSegments = relativePath.segments.drop(1)
                    if(strippedSegments.isEmpty()) {
                        exclude()
                    }
                    else {
                        relativePath = RelativePath(!isDirectory, *strippedSegments.toTypedArray())
                    }
                }
                includeEmptyDirs = false
            }
            into(box2dSourceRoot)
        }
        delete(box2dArchiveFile)
    }
}
