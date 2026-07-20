plugins {
    id("java-library")
}

dependencies {
    implementation(project(":samples:shared"))
    compileOnly(project(":box2d:core"))
    api(project(":extensions:fdx"))

    api("io.github.libfdx:application:${LibExt.fdxVersion}")
    api("io.github.libfdx:camera:${LibExt.fdxVersion}")
    api("io.github.libfdx:display:${LibExt.fdxVersion}")
    api("io.github.libfdx:graphics:${LibExt.fdxVersion}")
    api("io.github.libfdx:ui_kit:${LibExt.fdxVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
    targetCompatibility = JavaVersion.toVersion(LibExt.javaFFMTarget)
}
