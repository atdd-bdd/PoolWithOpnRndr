import org.junit.Test
import kotlin.math.sqrt


class Tests {
    @Test
    fun aTest() {
       assert(1==1)
        var x = Velocity(1.0, 2.0)

    }
    @Test
    fun testCheckCollision() {
        var position1 = Position (100.0, 100.0 )
        var position2 = Position (120.0, 100.0)
        var velocity1 = Velocity (100.0,-50.0 )
        var velocity2 = Velocity ( 100.0, 50.0)
        position1 = Position(150.0,321.0)
        velocity1 = Velocity(0.0,0.0)
        position2 = Position(147.92657613090037,340.8882668086036)
        velocity2 = Velocity(-1.2642818166217242,-0.6778855398378826)
        for (i in 0..1) {
            val startEnergy = computeTotalEnergy(velocity1, velocity2)
            val startMomentum = computeTotalMomentum(velocity1, velocity2)
            print("Start $velocity1   $velocity2   \n")
            val velocities: TwoVelocities = computeCollisionVelocity(
                position1, velocity1, position2, velocity2, 1.0
            )
            val endEnergy =   computeTotalEnergyTV(velocities)
            val endMomentum = computeTotalMomentumTV(velocities)
           // assert(endEnergy <= startEnergy && endMomentum <= startMomentum)
            print("Start $velocity1   $velocity2   \n")
            print("$startEnergy $startMomentum\n")
            print("End " + velocities.velocity1.toString() + "   " +
                    velocities.velocity2.toString() + "   " + "\n")
            print("$endEnergy $endMomentum\n")
        }
    }
}
fun computeTotalEnergyTV(velocities: TwoVelocities) : Double {
    return computeTotalEnergy(velocities.velocity1, velocities.velocity2)
}
fun computeTotalEnergy(velocity1 : Velocity, velocity2: Velocity) : Double {
    return computeEnergy(velocity1) + computeEnergy(velocity2)
}
fun computeTotalMomentum(velocity1: Velocity, velocity2: Velocity): Double
{
    return computeMomentum(velocity1) + computeMomentum(velocity2)
}
fun computeTotalMomentumTV(velocities: TwoVelocities) :Double {
    return computeTotalMomentum(velocities.velocity1, velocities.velocity2)
}
fun computeEnergy(velocity: Velocity) : Double {
    // mass is 1.0
    val totalVelocity = velocity.x * velocity.x + velocity.y + velocity.y
    return totalVelocity
}
fun computeMomentum(velocity: Velocity) : Double {
    val totalMomentum = sqrt (velocity.x * velocity.x + velocity.y + velocity.y)
    return totalMomentum
}
