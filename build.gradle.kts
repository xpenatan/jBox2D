plugins {
    id("java")
    alias(libs.plugins.easyPublishing)
}

val jbox2dGroup = libs.versions.jbox2dGroup.get()
val jbox2dSamplesUseMavenArtifacts = libs.versions.jbox2dSamplesUseMavenArtifacts.get().let { value ->
    value.toBooleanStrictOrNull()
        ?: throw GradleException(
            "jbox2dSamplesUseMavenArtifacts in gradle/libs.versions.toml must be true or false, but was '$value'."
        )
}
val jbox2dSamplesMavenVersion = libs.versions.jbox2dSamplesMavenVersion.get()

extra["jbox2dSamplesUseMavenArtifacts"] = jbox2dSamplesUseMavenArtifacts

val jbox2dPublishedArtifacts = mapOf(
    ":box2d:core" to "core",
    ":box2d:shared:jni" to "shared-jni",
    ":box2d:shared:c" to "shared-c",
    ":box2d:desktop:jni" to "desktop-jni",
    ":box2d:desktop:ffm" to "desktop-ffm",
    ":box2d:desktop:c" to "desktop-c",
    ":box2d:web:wasm" to "web-wasm",
    ":box2d:android:jni" to "android-jni",
    ":box2d:android:c" to "android-c",
    ":extensions:gdx:gl" to "gdx-gl",
    ":extensions:fdx" to "fdx"
)

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
        maven {
            url = uri("http://teavm.org/maven/repository/")
            isAllowInsecureProtocol = true
        }
    }

    configurations.configureEach {
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
        resolutionStrategy.eachDependency {
            val isJParserRuntime = requested.group == "com.github.xpenatan.jParser" && (
                    requested.name.startsWith("api-") ||
                    requested.name.startsWith("loader-") ||
                    requested.name.startsWith("runtime-")
            )
            if(isJParserRuntime) {
                useVersion(libs.versions.jParser.get())
            }
        }
    }
}

if(jbox2dSamplesUseMavenArtifacts) {
    subprojects {
        if(path.startsWith(":samples:")) {
            configurations.configureEach {
                resolutionStrategy.dependencySubstitution {
                    jbox2dPublishedArtifacts.forEach { (projectPath, artifactId) ->
                        substitute(project(projectPath))
                            .using(module("$jbox2dGroup:$artifactId:$jbox2dSamplesMavenVersion"))
                            .because("the samples are configured to test published jBox2D artifacts")
                    }
                }
            }
        }
    }
}

easyPublishing {
    modules(
        ":box2d:core",
        ":box2d:shared:jni",
        ":box2d:shared:c",
        ":box2d:desktop:jni",
        ":box2d:desktop:ffm",
        ":box2d:desktop:c",
        ":box2d:web:wasm",
        ":box2d:android:jni",
        ":box2d:android:c",
        ":extensions:gdx:gl",
        ":extensions:fdx"
    )

    groupId.set(jbox2dGroup)
    releaseVersion.set(libs.versions.jbox2dRelease)
    snapshotVersion.set(libs.versions.jbox2dSnapshot)

    snapshotRepositoryUrl.set("https://central.sonatype.com/repository/maven-snapshots/")
    releaseRepositoryUrl.set("https://central.sonatype.com")
    username.set(providers.environmentVariable("CENTRAL_PORTAL_USERNAME"))
    password.set(providers.environmentVariable("CENTRAL_PORTAL_PASSWORD"))
    signingKey.set(providers.environmentVariable("SIGNING_KEY"))
    signingPassword.set(providers.environmentVariable("SIGNING_PASSWORD"))

    pomName.set(libs.versions.jbox2dName)
    pomDescription.set("Box2D Java bindings")
    projectUrl.set("https://github.com/xpenatan/jBox2d")

    developerId.set("Xpe")
    developerName.set("Natan")

    scmUrl.set("https://github.com/xpenatan/jBox2d")
    scmConnection.set("scm:git:https://github.com/xpenatan/jBox2d.git")
    scmDeveloperConnection.set("scm:git:ssh://git@github.com/xpenatan/jBox2d.git")
}
