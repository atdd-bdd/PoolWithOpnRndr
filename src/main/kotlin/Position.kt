import org.openrndr.math.Vector2
data class Position(var x:Double,var y:Double){
    fun toVector2(): Vector2 {
        var x = this.x
       var y = this.y
        return Vector2(x,y)
    }
}
