plugins {
    id("java")
    alias(libs.plugins.easyPublishing)
}

val jbox2dGroup = libs.versions.jbox2dGroup.get()

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
