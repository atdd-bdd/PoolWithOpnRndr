class Header {

    var yourID =""
    var opponentID =""
    var yourMessage =""
    var opponentMessage =""
    var startMoving = false
    var yourTurn = true
    var dateStamp = 0.0
    var version = "V1.0"
    fun toStringList(): List<String> {
        val ret = listOf(opponentID, yourID, yourMessage, "NOT NEEDED", startMoving.toString(),
            yourTurn.toString(), version, dateStamp.toString())
        return ret
        }
        fun fromStringList(lines : List<String>): List<String> {
            if (lines.size < 8) {
                Debug.println("Not long enough for header on input ")
                return listOf("")
            }
//            opponentID  = lines[0]
//            yourID = lines[1]
            opponentMessage = lines[2]
//            opponentMessage = lines[3]
            startMoving = lines[4].toBoolean()
            yourTurn = (lines[5].toBoolean())
            version = lines[6]
            dateStamp = lines[7].toDouble()
            return lines.subList(8, lines.size)
          }
    }
