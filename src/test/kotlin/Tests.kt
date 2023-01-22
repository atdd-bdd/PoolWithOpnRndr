import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt


class Tests {

    @Test
    fun testCheckCollision() {
        val position1 = Position(100.0, 100.0)
        val position2 = Position(100.0 + 2 * BALL_RADIUS, 100.0)
        val velocity1 = Velocity(100.0, 0.0)
        val velocity2 = Velocity(-100.0, 0.0)
        val startEnergy = computeTotalEnergy(velocity1, velocity2)
        val startMomentum = computeTotalMomentum(velocity1, velocity2)
        val velocities: TwoVelocities = computeCollisionVelocity(
            position1, velocity1, position2, velocity2, 1.0
        )
        val endEnergy = computeTotalEnergyTV(velocities)
        val endMomentum = computeTotalMomentumTV(velocities)
        assert( endEnergy <= startEnergy && endMomentum <= startMomentum)
        val expectedVelocity1 = Velocity(-100.0, 0.0)
        val expectedVelocity2 = Velocity( 100.0, 0.0)
        assertEquals(expectedVelocity1.x, velocities.velocity1.x,0.0001)
        assertEquals(expectedVelocity1.y, velocities.velocity1.y,0.0001)
        assertEquals(expectedVelocity2.x, velocities.velocity2.x,0.0001)
        assertEquals(expectedVelocity2.y, velocities.velocity2.y,0.0001)

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
        return velocity.x * velocity.x + velocity.y + velocity.y
    }

    fun computeMomentum(velocity: Velocity): Double {
        return sqrt(velocity.x * velocity.x + velocity.y + velocity.y)
    }
}