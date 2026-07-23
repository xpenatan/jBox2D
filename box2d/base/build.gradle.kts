plugins {
    id("java-library")
}

dependencies {
    implementation(libs.jparserLoaderCore)
    implementation(libs.jparserRuntimeBase)
    implementation(libs.jparserRuntimeCore)
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java"))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaMain.get())
}
