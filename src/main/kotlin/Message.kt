class Message {
    val header = Header()
    val configuration = Configuration()
    var balls = initialBalls()
    override fun toString(): String {
        val headerValue = header.toStringList()
        val configurationValue = configuration.toStringList()
        val ballValue = getGameStringList(balls)
        val lines = concatenate(headerValue, configurationValue, ballValue)
//        println("**** Output is ")
//        printStringList(lines)
        val ret = lines.joinToString(separator = "\n")
        return  ret
    }
    fun fromString(value: String){
         var lines = value.split("\r\n", "\n", "\r");
//        println("***** Input lines are $lines.size *****")
//        printStringList(lines)
        lines = header.fromStringList(lines)
//        println("Configuration lines ")
//        printStringList(lines)
        lines = configuration.fromStringList(lines)
//        println("Ball lines are ")
//        printStringList(lines)
        balls = readGameStringList(lines, balls)
        return
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

