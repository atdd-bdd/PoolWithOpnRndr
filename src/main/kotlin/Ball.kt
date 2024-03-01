data class Ball(val symbol: Int , var position: Position, var velocity: Velocity, var active:Boolean)

fun printBall(ball: Ball)
{
    print("Ball ")
    print (ball.symbol)
    print(" Position(")
    print(ball.position.x)
    print(",")
    print(ball.position.y)
    print("). Velocity(")
    print(ball.velocity.x)
    print(",")
    print(ball.velocity.y)
    println(")")


}
