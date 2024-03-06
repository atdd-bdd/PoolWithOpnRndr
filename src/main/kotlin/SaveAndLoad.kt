import org.openrndr.dialogs.getDefaultPathForContext
import org.openrndr.dialogs.openFileDialog
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.dialogs.setDefaultPathForContext
import java.io.File

fun saveConfigurationToFile(configuration: Configuration) {
    val defaultPath = getDefaultPathForContext(contextID = "pool")
    val defaultSaveFolder = "pool"
    if (defaultPath == null) {
        val local = File(".")
        val parameters = File(local, defaultSaveFolder)
        if (parameters.exists() && parameters.isDirectory) {
            setDefaultPathForContext(contextID = "pool", file = parameters)
        } else {
            if (parameters.mkdirs()) {
                setDefaultPathForContext(
                    contextID = "pool",
                    file = parameters
                )
            } else {
                println("Could not create directory ${parameters.absolutePath}")
            }
        }
    }

    saveFileDialog(
        suggestedFilename = "game1.poolc",
        contextID = "pool",
  //      supportedExtensions = listOf("poolc")
        supportedExtensions = listOf(
            "poolc" to listOf("poolc"))
    )
    {
        saveConfiguration(it, configuration)
    }
}


 fun loadConfigurationFromFile() : Configuration {
    var configuration = Configuration()
    openFileDialog(   supportedExtensions = listOf(
        "poolc" to listOf("poolc")), contextID = "pool")
    {
        configuration = loadConfiguration(it)
    }

    return configuration
}

fun readConfigurationString(text: String): Configuration {
    val lines = text.split("\r\n", "\n", "\r")
    val configuration = Configuration()
    configuration.fromStringList(lines)
    return configuration
}

fun loadConfiguration(it: File): Configuration {

    val text = it.readText()
    return readConfigurationString(text)
}

fun loadGameFromFile(balls: Array<Ball>) : Array<Ball> {
    var ballsOut = balls.clone()
    openFileDialog(   supportedExtensions = listOf(
        "pool" to listOf("pool")), contextID = "pool") { ballsOut = loadGame(it, balls) }
    return ballsOut
}

fun saveGameToFile(balls: Array<Ball>) {
    val defaultPath = getDefaultPathForContext(contextID = "pool")
    val defaultSaveFolder = "pool"
    if (defaultPath == null) {
        val local = File(".")
        val parameters = File(local, defaultSaveFolder)
        if (parameters.exists() && parameters.isDirectory) {
            setDefaultPathForContext(contextID = "pool", file = parameters)
        } else {
            if (parameters.mkdirs()) {
                setDefaultPathForContext(
                    contextID = "pool",
                    file = parameters
                )
            } else {
                println("Could not create directory ${parameters.absolutePath}")
            }
        }
    }

    saveFileDialog(
        suggestedFilename = "game1.pool",
        contextID = "pool",
        supportedExtensions = listOf(
            "pool" to listOf("pool"))

    ) {
        saveGame(it, balls)
    }
}


fun loadGame(it: File, ballsCurrent: Array<Ball>): Array<Ball> {

    val text = it.readText()
    return readGameString(text, ballsCurrent)
}

fun readGameString(text: String, ballsCurrent: Array<Ball>): Array<Ball> {
    val lines = text.split("\r\n", "\n", "\r")
    return readGameStringList(lines, ballsCurrent)
}

fun readGameStringList(lines: List<String>, ballsCurrent: Array<Ball>): Array<Ball> {
    val ballsOut = copyBalls(ballsCurrent)
    if (lines.size < 17) {
        Debug.println("Not changing balls on input - bad size")
        return ballsOut
    }

    var firstLine = true
    var index = 0
    for (line in lines) {
        if (firstLine) {
            firstLine = false
            continue
        }
        val fields = line.split(',')
        try {
            val ball = Ball(
                fields[0].toInt(), Position(
                    fields[1].toDouble(),
                    fields[2].toDouble()
                ), Velocity(fields[3].toDouble(), fields[4].toDouble()),
                fields[5].toBoolean()
            )
            ballsOut[index] = ball
            index++

        } catch (e: NumberFormatException) {
            print("Number format exception")
        }
        val size = ballsOut.size
        if (index >= size)
            break
    }
    return ballsOut
}

fun saveGame(it: File, ballsIn: Array<Ball>) {

    val text = getGameString(ballsIn)
    it.writeText(text)
}

fun saveConfiguration(it: File, configuration: Configuration) {

    val text = getConfigurationString(configuration)
    it.writeText(text)
}

fun getConfigurationString(configuration: Configuration): String {
    val list = configuration.toStringList()
    return list.joinToString(separator = "\n")
}


fun getGameString(balls: Array<Ball>): String {
    val list = getGameStringList(balls)
    return list.joinToString(separator = "\n")
}

fun getGameStringList(balls: Array<Ball>): List<String> {
    val result = mutableListOf<String>()
    result.add("Symbol,PositionX,PositionY,VelocityX,VelocityY,Active")
    for (i in balls.indices) {
        val ball = balls[i]
        val line = String.format(
            "${ball.symbol},${ball.position.x},${ball.position.y}," +
                    "${ball.velocity.x},${ball.velocity.y},${ball.active}"
        )
        result.add(line)
    }
    return result
}
