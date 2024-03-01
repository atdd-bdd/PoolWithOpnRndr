import kotlin.math.sqrt

fun computeEnergyMomentum(balls: Array<Ball>): Pair<Double, Double> {
    var totalEnergy = 0.0
    var totalMomentum = 0.0
    for (ball in balls){
        totalEnergy += computeEnergy(ball.velocity)
        totalMomentum += computeMomentum(ball.velocity)
    }
    return Pair(totalEnergy, totalMomentum )

}
 fun computeTotalEnergyTV(velocities: TwoVelocities): Double {
     return computeTotalEnergy(velocities.velocity1, velocities.velocity2)
 }

 fun computeTotalEnergy(velocity1: Velocity, velocity2: Velocity): Double {
     return computeEnergy(velocity1) + computeEnergy(velocity2)
 }

 fun computeTotalMomentum(velocity1: Velocity, velocity2: Velocity): Double {
     return computeMomentum(velocity1) + computeMomentum(velocity2)
 }

 fun computeTotalMomentumTV(velocities: TwoVelocities): Double {
     return computeTotalMomentum(velocities.velocity1, velocities.velocity2)
 }

 fun computeEnergy(velocity: Velocity): Double {
     // mass is 1.0
     return velocity.x * velocity.x + velocity.y * velocity.y
 }

 fun computeMomentum(velocity: Velocity): Double {
     return sqrt(velocity.x * velocity.x + velocity.y * velocity.y)
 }

fun checkMomentum(velocity1: Velocity, velocity2: Velocity, velocity11: Velocity, velocity21: Velocity) {
    val xStart = velocity1.x + velocity2.x
    val yStart = velocity1.y + velocity2.y
    val xEnd = velocity11.x + velocity21.x
    val yEnd = velocity11.y + velocity21.y
    if (xEnd -.0001 > xStart)
        println("**** X Momentum increased $xEnd from $xStart ")
    if (yEnd - .0001 > yStart)
        println("***Y Momentum increased $yEnd from $yStart")
}
