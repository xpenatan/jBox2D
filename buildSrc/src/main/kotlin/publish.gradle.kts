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

val requestedTasks = gradle.startParameter.taskNames
LibExt.isRelease = requestedTasks.any { task ->
    task.contains("release", ignoreCase = true) && task.contains("publish", ignoreCase = true)
}

configure(libraryProjects) {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    extensions.configure<PublishingExtension> {
        repositories {
            maven {
                val snapshot = LibExt.libVersion.endsWith("-SNAPSHOT")
                url = if(snapshot) {
                    uri("https://central.sonatype.com/repository/maven-snapshots/")
                }
                else {
                    uri(rootProject.layout.buildDirectory.dir("staging-deploy"))
                }
                if(snapshot) {
                    credentials {
                        username = System.getenv("CENTRAL_PORTAL_USERNAME")
                        password = System.getenv("CENTRAL_PORTAL_PASSWORD")
                    }
                }
            }
        }

        publications.configureEach {
            if(this is MavenPublication) {
                pom {
                    name.set(LibExt.libName)
                    description.set("Box2D ${LibExt.box2dVersion} Java bindings")
                    url.set("https://github.com/xpenatan/jBox2d")
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
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
            }
        }
    }

    tasks.withType<Javadoc>().configureEach {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    val signingKey = System.getenv("SIGNING_KEY").orEmpty()
    val signingPassword = System.getenv("SIGNING_PASSWORD").orEmpty()
    if(signingKey.isNotEmpty() && signingPassword.isNotEmpty()) {
        extensions.configure<SigningExtension> {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(extensions.getByType<PublishingExtension>().publications)
        }
    }
}

tasks.register("publishSnapshot") {
    group = "publishing"
    dependsOn(libraryProjects.map { it.tasks.withType<PublishToMavenRepository>() })
}
