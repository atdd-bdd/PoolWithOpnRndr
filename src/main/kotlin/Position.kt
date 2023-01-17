import org.openrndr.math.Vector2
data class Position(var x:Double,var y:Double){
    fun toVector2(): Vector2 {
        return Vector2(this.x,this.y)
    }

    override fun toString(): String {
        return "Position("+x.toString() + "," + y.toString()+")"
    }
}
