plugins {
    id("java")
    alias(libs.plugins.easyPublishing)
}

val jbox2dGroup = "com.github.xpenatan.jBox2D"

mapOf(
    ":box2d:shared:jni" to "shared",
    ":box2d:shared:c" to "shared",
    ":box2d:desktop:jni" to "desktop",
    ":box2d:desktop:c" to "desktop",
    ":box2d:android:jni" to "android",
    ":box2d:android:c" to "android"
).forEach { (projectPath, internalGroup) ->
    project(projectPath).afterEvaluate {
        // These projects share the names "jni" and "c", so their internal Gradle
        // coordinates must remain distinct even though their published group is shared.
        group = "$jbox2dGroup.$internalGroup"
        publishing.publications.withType<MavenPublication>().configureEach {
            setGroupId(jbox2dGroup)
        }
    }
}

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
            if(requested.group == "com.github.xpenatan.jParser") {
                useVersion(libs.versions.jparser.get())
            }
            else if(requested.group == "com.github.xpenatan.gdx-teavm") {
                useVersion(libs.versions.gdxTeavm.get())
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
    automaticRelease.set(true)

    pomName.set("jBox2D")
    pomDescription.set("Box2D Java bindings")
    projectUrl.set("https://github.com/xpenatan/jBox2d")

    developerId.set("Xpe")
    developerName.set("Natan")

    scmUrl.set("https://github.com/xpenatan/jBox2d")
    scmConnection.set("scm:git:https://github.com/xpenatan/jBox2d.git")
    scmDeveloperConnection.set("scm:git:ssh://git@github.com/xpenatan/jBox2d.git")
}
