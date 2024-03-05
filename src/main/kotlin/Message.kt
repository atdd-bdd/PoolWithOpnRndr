class Message {
    val header = Header()
    var configuration = Configuration()
    var ballsAll = initialBalls()
    override fun toString(): String {
        val headerValue = header.toStringList()
        val configurationValue = configuration.toStringList()
        val ballValue = getGameStringList(ballsAll)
        val lines = concatenate(headerValue, configurationValue, ballValue)

        return lines.joinToString(separator = "\n")
    }
    fun fromString(value: String) : Boolean{
         var lines = value.split("\r\n", "\n", "\r")
        if (lines.size < 8)
             return false

        lines = header.fromStringList(lines)
        if (lines.size < 6)
            return false
        lines = configuration.fromStringList(lines)

        if (lines.size <17)
            return false
        ballsAll = readGameStringList(lines, ballsAll)
        return true
    }
}

fun <T> concatenate(vararg lists: List<T>): List<T> {
    return listOf(*lists).flatten()
}

@Suppress("unused")
fun printStringList(lines: List<String>){
    println("List is")
    for (line in lines){
        println(line)
    }
}

