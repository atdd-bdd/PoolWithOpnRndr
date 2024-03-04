data class Ball(val symbol: Int , var position: Position, var velocity: Velocity, var active:Boolean)

fun printBall(ball: Ball)
{
    if (!Debug.debugOn)
        return
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
@Suppress("unused", "unused")
fun printBalls(balls: Array<Ball>){
    Debug.debugOn = true
    for (ball in balls)
    {
        printBall(ball)
    }


    Debug.debugOn = false
}