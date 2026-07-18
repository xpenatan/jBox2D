import java.io.File
import java.util.Properties

object LibExt {
    const val groupId = "com.github.xpenatan.jBox2D"
    const val libName = "jBox2D"
    var isRelease = false
    val libVersion: String
        get() = getVersion()

    const val javaMainTarget = "1.8"
    const val javaWebTarget = "17"
    const val javaFFMTarget = "25"

    const val box2dVersion = "3.1.1"
    const val jParserVersion = "1.2.3"
    const val gdxVersion = "1.14.2"
    const val gdxTeaVMVersion = "1.6.0"
    const val jUnitVersion = "4.13.2"
}

private fun getVersion(): String {
    if(!LibExt.isRelease) {
        return "-SNAPSHOT"
    }

    val file = File("gradle.properties")
    if(!file.exists()) {
        throw RuntimeException("gradle.properties must exist for release builds")
    }

    val properties = Properties()
    file.inputStream().use(properties::load)
    return properties.getProperty("version")?.trim()?.takeIf(String::isNotEmpty)
        ?: throw RuntimeException("version is missing from gradle.properties")
}
