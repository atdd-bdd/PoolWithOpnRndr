import java.io.File
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

fun computeEnergyMomentum(balls: Array<Ball>): Pair<Double, Double> {
    var totalEnergy = 0.0
    var momentumX = 0.0
    var momentumY = 0.0
    for (ball in balls){
        totalEnergy += computeEnergy(ball.velocity)
        momentumX += ball.velocity.x
        momentumY += ball.velocity.y
     }
    val totalMomentum = sqrt(momentumX*momentumX + momentumY*momentumY)
    return Pair(totalEnergy, totalMomentum )

}
 fun computeTotalEnergyTV(velocities: TwoVelocities): Double {
     return computeTotalEnergy(velocities.velocity1, velocities.velocity2)
 }

 fun computeTotalEnergy(velocity1: Velocity, velocity2: Velocity): Double {
     return computeEnergy(velocity1) + computeEnergy(velocity2)
 }

fun computeTotalMomentum(velocity1: Velocity, velocity2: Velocity): Double {
    val momentumX = velocity1.x + velocity2.x
    val momentumY = velocity1.y + velocity2.y
    return sqrt(momentumX * momentumX + momentumY * momentumY)
}

 fun computeTotalMomentumTV(velocities: TwoVelocities): Double {
     return computeTotalMomentum(velocities.velocity1, velocities.velocity2)
 }

 fun computeEnergy(velocity: Velocity): Double {
     // mass is 1.0
     return velocity.x * velocity.x + velocity.y * velocity.y
 }

fun checkMomentum(velocity1: Velocity, velocity2: Velocity, velocity11: Velocity, velocity21: Velocity) {
    val xStart = velocity1.x + velocity2.x
    val yStart = velocity1.y + velocity2.y
    val xEnd = velocity11.x + velocity21.x
    val yEnd = velocity11.y + velocity21.y
    if (xEnd -.0001 > xStart)
       Debug.println("**** X Momentum increased $xEnd from $xStart ")
    if (yEnd - .0001 > yStart)
       Debug.println("***Y Momentum increased $yEnd from $yStart")
}

fun printBallsDifference(previousBalls: Array<Ball>, currentBalls: Array<Ball>){
    if (!Debug.debugOn)
        return
    println("Ball differences ")
  //  println("Previous " + getGameString())
    for (i in 0 until previousBalls.size)
    {
        val balls1 = previousBalls[i]
        val balls2 = currentBalls[i]
        val ball1x = balls1.velocity.x
        val ball2x = balls2.velocity.x
        if ((abs(ball1x) -1.0 > abs(ball2x)) ||
                abs(ball2x) -1.0 > abs(ball1x) || sign(ball1x) != sign(ball2x))
            println(" For $i  X Velocity previous $ball1x != new $ball2x ")
        val ball1y = balls1.velocity.y
        val ball2y = balls2.velocity.y
        if ((abs(ball1y) -1.0 > abs(ball2y)) ||
            abs(ball2y) -1.0 > abs(ball1y) || sign(ball1y) != sign(ball2y))
            println(" For $i Y Velocity $ball1y != $ball2y ")
    }
}

fun turnResourceIntoFile(fileName: String):String{
    val stream =object {}.javaClass.getResourceAsStream(fileName)
    val outfile = "temp.otf"
    val file = File(outfile)
    val bytes = stream?.readBytes()
    if (bytes != null)
        file.writeBytes(bytes)
    else
        println("Resource $fileName not found")
    return outfile
}