class Header {

    var yourID =""
    var opponentID =""
    var yourMessage =""
    var yourStatus =""
    var startMoving = false
    var yourTurn = true
    var dateStamp = 0.0
    private var version = "V1.0"
    fun toStringList(): List<String> {
        return listOf(
            opponentID, yourID, yourMessage, yourStatus, startMoving.toString(),
            yourTurn.toString(), version, dateStamp.toString()
        )
    }
        fun fromStringList(lines : List<String>): List<String> {
            if (lines.size < 8) {
                Debug.println("Not long enough for header on input ")
                return listOf("")
            }
            // First two lines are not need on reception
//            opponentID  = lines[0]
//            yourID = lines[1]
            yourMessage = lines[2]
            yourStatus = lines[3]
            startMoving = lines[4].toBoolean()
            yourTurn = (lines[5].toBoolean())
            version = lines[6]
            dateStamp = lines[7].toDouble()
            return lines.subList(8, lines.size)
          }
    }
