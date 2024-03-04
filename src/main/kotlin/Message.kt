class Message {
    val header = Header()
    var configuration = Configuration()
    var ballsAll = initialBalls()
    override fun toString(): String {
        val headerValue = header.toStringList()
        val configurationValue = configuration.toStringList()
        val ballValue = getGameStringList(ballsAll)
        val lines = concatenate(headerValue, configurationValue, ballValue)
//        println("**** Output is ")
//        printStringList(lines)
        val ret = lines.joinToString(separator = "\n")
        return  ret
    }
    fun fromString(value: String) : Boolean{
         var lines = value.split("\r\n", "\n", "\r");
         if (lines.size < 8)
             return false
//       println("***** Input lines are $lines.size *****")
//        printStringList(lines)
        lines = header.fromStringList(lines)
//        println("Configuration lines ")
//        printStringList(lines)
        if (lines.size < 6)
            return false
        lines = configuration.fromStringList(lines)
//        println("Ball lines are ")
//        printStringList(lines)
        if (lines.size <17)
            return false
        ballsAll = readGameStringList(lines, ballsAll)
        return true
    }
}

fun <T> concatenate(vararg lists: List<T>): List<T> {
    return listOf(*lists).flatten()
}

fun printStringList(lines: List<String>){
    println("List is")
    for (line in lines){
        println(line)
    }
}

