import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Base64

val libraryProjects = setOf(
    project(":box2d:core"),
    project(":box2d:shared:jni"),
    project(":box2d:shared:c"),
    project(":box2d:desktop:jni"),
    project(":box2d:desktop:ffm"),
    project(":box2d:desktop:c"),
    project(":box2d:web:wasm"),
    project(":box2d:android:jni"),
    project(":box2d:android:c")
)

val requestedTaskNames = gradle.startParameter.taskNames
fun isTaskRequested(taskName: String): Boolean {
    return requestedTaskNames.any { it == taskName || it.endsWith(":$taskName") }
}

val isPrepareSnapshotDeploy = isTaskRequested("prepareSnapshotDeploy")
val isReleasePublish = isTaskRequested("publishRelease")
val isPrepareReleaseDeploy = isTaskRequested("prepareReleaseDeploy")
val isUploadToMavenCentral = isTaskRequested("uploadToMavenCentral")
val isTestRelease = isTaskRequested("publishTestRelease")
val isZipStagingDeploy = isTaskRequested("zipStagingDeploy")
val isReleaseIntent = isReleasePublish || isPrepareReleaseDeploy || isUploadToMavenCentral ||
    isTestRelease || isZipStagingDeploy
LibExt.isRelease = isReleaseIntent

configure(libraryProjects) {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    if(LibExt.libVersion.isBlank()) {
        throw GradleException("Version cannot be empty")
    }

    extensions.configure<PublishingExtension> {
        repositories {
            maven {
                val isSnapshot = LibExt.libVersion.endsWith("-SNAPSHOT")
                url = when {
                    !isSnapshot -> uri(rootProject.layout.buildDirectory.dir("staging-deploy"))
                    isPrepareSnapshotDeploy -> uri(rootProject.layout.buildDirectory.dir("snapshot-deploy"))
                    else -> uri("https://central.sonatype.com/repository/maven-snapshots/")
                }

                if(isSnapshot && !isPrepareSnapshotDeploy) {
                    credentials {
                        username = System.getenv("CENTRAL_PORTAL_USERNAME")
                        password = System.getenv("CENTRAL_PORTAL_PASSWORD")
                    }
                }
            }
        }

        publications.withType<MavenPublication>().configureEach {
            pom {
                name.set(LibExt.libName)
                description.set("Box2D ${LibExt.box2dVersion} Java bindings")
                url.set("https://github.com/xpenatan/jBox2d")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("Xpe")
                        name.set("Natan")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/xpenatan/jBox2d.git")
                    developerConnection.set("scm:git:ssh://git@github.com/xpenatan/jBox2d.git")
                    url.set("https://github.com/xpenatan/jBox2d")
                }
            }
        }
    }

    tasks.withType<Javadoc>().configureEach {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    extensions.configure<SigningExtension> {
        isRequired = isReleaseIntent

        val signingKey = System.getenv("SIGNING_KEY").orEmpty()
        val signingPassword = System.getenv("SIGNING_PASSWORD").orEmpty()
        if(signingKey.isNotEmpty() && signingPassword.isNotEmpty()) {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        sign(extensions.getByType<PublishingExtension>().publications)
    }
}

tasks.register<Zip>("zipStagingDeploy") {
    group = "publishing"
    description = "Stage all signed release artifacts in a Central Portal deployment bundle."
    dependsOn(libraryProjects.map { it.tasks.named("publish") })
    from(rootProject.layout.buildDirectory.dir("staging-deploy"))
    archiveFileName.set("staging-deploy.zip")
    destinationDirectory.set(rootProject.layout.buildDirectory)
    onlyIf { !LibExt.libVersion.endsWith("-SNAPSHOT") }
}

tasks.register("uploadToMavenCentral") {
    group = "publishing"
    description = "Upload the staged release bundle to Maven Central and publish it automatically."
    dependsOn("zipStagingDeploy")
    onlyIf { !LibExt.libVersion.endsWith("-SNAPSHOT") }

    doLast {
        val stagingDir = rootProject.layout.buildDirectory.dir("staging-deploy").get().asFile
        val zipFile = rootProject.layout.buildDirectory.file("staging-deploy.zip").get().asFile

        if(!stagingDir.isDirectory) {
            throw GradleException("Staging directory ${stagingDir.absolutePath} does not exist")
        }
        if(!zipFile.isFile || !Files.isReadable(Paths.get(zipFile.absolutePath))) {
            throw GradleException("Release bundle ${zipFile.absolutePath} does not exist or is not readable")
        }

        val username = System.getenv("CENTRAL_PORTAL_USERNAME")?.takeIf(String::isNotBlank)
            ?: throw GradleException("CENTRAL_PORTAL_USERNAME environment variable is not set")
        val password = System.getenv("CENTRAL_PORTAL_PASSWORD")?.takeIf(String::isNotBlank)
            ?: throw GradleException("CENTRAL_PORTAL_PASSWORD environment variable is not set")
        val bearerToken = Base64.getEncoder().encodeToString(
            "$username:$password".toByteArray(StandardCharsets.UTF_8)
        )
        val bundleName = URLEncoder.encode("${LibExt.libName}-${LibExt.libVersion}", StandardCharsets.UTF_8)

        if(System.getenv("GITHUB_ACTIONS") == "true") {
            println("::add-mask::$bearerToken")
        }

        providers.exec {
            commandLine(
                "curl",
                "--fail-with-body",
                "--silent",
                "--show-error",
                "--request", "POST",
                "--header", "Authorization: Bearer $bearerToken",
                "--form", "bundle=@${zipFile.absolutePath};type=application/octet-stream",
                "https://central.sonatype.com/api/v1/publisher/upload?name=$bundleName&publishingType=AUTOMATIC"
            )
        }.result.get()
    }
}

tasks.register("prepareReleaseDeploy") {
    group = "publishing"
    description = "Prepare the signed Maven Central release bundle without uploading it."
    dependsOn("zipStagingDeploy")
    onlyIf { !LibExt.libVersion.endsWith("-SNAPSHOT") }
}

tasks.register("publishRelease") {
    group = "publishing"
    description = "Stage, upload, validate, and automatically publish a release through Central Portal."
    dependsOn("uploadToMavenCentral")
}

tasks.register("publishTestRelease") {
    group = "publishing"
    description = "Build the signed release bundle locally without uploading it."
    dependsOn("prepareReleaseDeploy")
}

tasks.register("publishSnapshot") {
    group = "publishing"
    description = "Publish all snapshot artifacts to the Central Portal snapshot repository."
    dependsOn(libraryProjects.map { it.tasks.withType<PublishToMavenRepository>() })
}

tasks.register("prepareSnapshotDeploy") {
    group = "publishing"
    description = "Publish all snapshot artifacts to a local repository under build/snapshot-deploy."
    dependsOn(libraryProjects.map { it.tasks.withType<PublishToMavenRepository>() })
    onlyIf { LibExt.libVersion.endsWith("-SNAPSHOT") }
}
