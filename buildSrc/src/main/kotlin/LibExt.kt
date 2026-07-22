import java.io.File
import java.util.Properties

object LibExt {
    const val groupId = "com.github.xpenatan.jBox2D"
    const val libName = "jBox2D"
    const val snapshotVersion = "-SNAPSHOT"
    var isRelease = false
    val releaseVersion: String
        get() = readReleaseVersion()
    val libVersion: String
        get() = if(isRelease) releaseVersion else snapshotVersion

    const val javaMainTarget = "1.8"
    const val javaWebTarget = "17"
    const val javaFFMTarget = "25"

    const val jParserVersion = "-SNAPSHOT"
    const val fdxVersion = "-SNAPSHOT"
    const val gdxVersion = "1.14.2"
    const val gdxTeaVMVersion = "1.6.0"
    const val jUnitVersion = "4.13.2"
}

private fun readReleaseVersion(): String {
    val file = File("gradle.properties")
    if(!file.exists()) {
        throw RuntimeException("gradle.properties must exist for release builds")
    }

    val properties = Properties()
    file.inputStream().use(properties::load)
    return properties.getProperty("version")?.trim()?.takeIf(String::isNotEmpty)
        ?: throw RuntimeException("version is missing from gradle.properties")
}
