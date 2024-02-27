class Header {

    var yourID =""
    var opponentID =""
    var yourMessage =""
    var opponentMessage =""
    var startMoving = false
    var yourTurn = true
    fun toStringList(): List<String> {
        val ret = listOf(opponentID, yourID, yourMessage, "NOT NEEDED", startMoving.toString(),
            yourTurn.toString() )
        return ret
        }
        fun fromStringList(lines : List<String>): List<String> {
            if (lines.size < 5) {
                println("Not long enough for header on input ")
                return listOf("")
            }
//            opponentID  = lines[0]
//            yourID = lines[1]
            opponentMessage = lines[2]
//            opponentMessage = lines[3]
            startMoving = lines[4].toBoolean()
            yourTurn = !(lines[5].toBoolean())
            return lines.subList(6, lines.size)
          }
    }
