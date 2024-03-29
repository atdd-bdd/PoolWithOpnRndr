class Configuration
{
    var cueAngle = 0.0
    var cueForce = 0.0
    var rollingResistance = 0.01
    var restitution = 0.95
    var cushionElasticity = 0.70
    var displayIncrement = 100.0
    var cueAngleTrim = 0.0
    fun copyTo(out: Configuration) {

        out.cueAngle = cueAngle
        out.cueForce = cueForce
        out.rollingResistance = rollingResistance
        out.restitution = restitution
        out.cushionElasticity = cushionElasticity
        out.displayIncrement = displayIncrement
        out.cueAngleTrim = cueAngleTrim

    }
    fun copy() : Configuration{
        val out = Configuration()
        out.cueAngle = cueAngle
        out.cueForce = cueForce
        out.rollingResistance = rollingResistance
        out.restitution = restitution
        out.cushionElasticity = cushionElasticity
        out.displayIncrement = displayIncrement
        out.cueAngleTrim = cueAngleTrim
        return out
    }
    fun toStringList(): List<String> {
        return listOf(
            cueAngle.toString(), cueForce.toString(), rollingResistance.toString(),
            restitution.toString(), cushionElasticity.toString(), displayIncrement.toString(),
            cueAngleTrim.toString()
        )

    }
    fun fromStringList(lines: List<String>) : List<String> {
        if (lines.size < 7)
            return listOf("")
        cueAngle = lines[0].toDouble()
        cueForce = lines[1].toDouble()
        rollingResistance = lines[2].toDouble()
        restitution = lines[3].toDouble()
        cushionElasticity = lines[4].toDouble()
        displayIncrement =lines[5].toDouble()
        cueAngleTrim = lines[6].toDouble()
        return lines.subList(7, lines.size)
    }
}