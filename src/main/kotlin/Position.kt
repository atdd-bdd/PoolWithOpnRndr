import org.openrndr.math.Vector2
data class Position(val x:Double,val y:Double){
    fun toVector2(): Vector2 {
        return Vector2(this.x,this.y)
    }

    override fun toString(): String {
        return "Position($x,$y)"
    }
}
