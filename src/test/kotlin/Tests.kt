import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt


class Tests {

//   @Test
//    fun testRepeatabilityOfMovement(){
//        var balls = initialBalls()
//        var balls1 = copyBalls(balls)
//        var balls2 = copyBalls(balls)
//        val pockets = Pockets()
//        var cueAngle = 39.060
//        var cueForce = 1419.0
//        val percentage: Percentage = xyFromAngle(cueAngle)
//        val startingVelocity = Velocity(cueForce * percentage.x, cueForce * percentage.y)
//        var rollingResistance = 0.01
//        var restitution = 0.95
//        var cushionElasticity = 0.70
//        var computationSegments = 100
//        var displayIncrement = 100
//        println("*********")
//        val originalTimes = initializeDeltaTimes()
//        hitCue(balls, startingVelocity)
//        var (startEnergy0, startMomentum0)= computeEnergyMomentum(balls)
//        println("Start e $startEnergy0 m $startMomentum0")
//        for (deltaTime in originalTimes){
//        moveBalls(
//            balls,
//            TABLE_SIZE,
//            cushionElasticity,
//            restitution,
//            rollingResistance,
//            computationSegments,
//            displayIncrement,
//            deltaTime,
//            pockets
//        )
//        val (startEnergy1, startMomentum1)= computeEnergyMomentum(balls)
//        if (startEnergy1 >= startEnergy0 || startMomentum1 >= startMomentum0)
//            println("Gaining Energy or Momentum")
//        startEnergy0 = startEnergy1
//        startMomentum0 = startMomentum1
//        }
//       println("End e $startEnergy0 m $startMomentum0")
//
//       printCueBall(balls)
//       println("End of first go around")
//       println("Next round")
//       val (size1, partTime1) = computePartTimes(originalTimes)
////   Hit a second time with a different order
//        hitCue(balls1, startingVelocity)
//        val sortedTimesList = originalTimes.sorted()
//        val sortedTimes = sortedTimesList.toTypedArray()
//
//        for (deltaTime in sortedTimes){
//            moveBalls(
//                balls1,
//                TABLE_SIZE,
//                cushionElasticity,
//                restitution,
//                rollingResistance,
//                computationSegments,
//                displayIncrement,
//                deltaTime,
//                pockets
//            )}
//       var (startEnergy2, startMomentum2) = computeEnergyMomentum(balls)
//       println("Ending  e $startEnergy2 m $startMomentum2")
//       printCueBall(balls1)
//
//       val (size, partTime) = computePartTimes(sortedTimes)
//        println("******")
//        hitCue(balls2, startingVelocity)
//        val (startEnergy, startMomentum) = computeEnergyMomentum(balls2)
//
//        for (i in 0 until size){
//            moveBalls(
//                balls2,
//                TABLE_SIZE,
//                cushionElasticity,
//                restitution,
//                rollingResistance,
//                computationSegments,
//                displayIncrement,
//                partTime,
//                pockets
//            )}
//
////        ballsString2 = getGameString(balls2);
////        println("Equal times  $ballsString2")
//            printCueBall(balls2)
//
//    }


//    private fun printCueBall(balls: Array<Ball>) {
//        val x = balls[0].position.x
//        val y = balls[0].position.y
//        println("Cue is $x $y")
//
//    }
//
//    private fun computePartTimes(sortedTimes: Array<Double>): Pair<Int, Double> {
//        val size = sortedTimes.size
//        var total = 0.0
//        for (deltaTime in sortedTimes)
//            total += deltaTime
//        val partTime = total / size
//        println("Total time $total size $size partTime $partTime")
//        return Pair(size, partTime)
//    }

    @Test
    fun testBallsToString()
    {
        val balls = initialBalls()
        val anotherBalls = initialBalls()
        rackBalls(anotherBalls)
        val string = getGameString(balls)
        println(string)
        val ballsOut = readGameString(string, anotherBalls)
        assertArrayEquals(balls, ballsOut)
    }
    @Test
    fun testCheckCollision() {
        val position1 = Position(100.0, 100.0)
        val position2 = Position(100.0 + 2 * BALL_RADIUS, 100.0)
        val velocity1 = Velocity(100.0, 0.0)
        val velocity2 = Velocity(-100.0, 0.0)
        val velocities: TwoVelocities = twoVelocities(velocity1, velocity2, position1, position2)
        val expectedVelocity1 = Velocity(-95.0, 0.0)
        val expectedVelocity2 = Velocity( 95.0, 0.0)
        assertEquals(expectedVelocity1.x, velocities.velocity1.x,0.0001)
        assertEquals(expectedVelocity1.y, velocities.velocity1.y,0.0001)
        assertEquals(expectedVelocity2.x, velocities.velocity2.x,0.0001)
        assertEquals(expectedVelocity2.y, velocities.velocity2.y,0.0001)

    }
    @Test
    fun testCheckCollisionZ() {
        val position1 = Position(100.0, 100.0)
        val position2 = Position(100.0 , 100.0+ 2 * BALL_RADIUS)
        val velocity1 = Velocity( 0.0,100.0)
        val velocity2 = Velocity(0.0, -100.0)
        val velocities: TwoVelocities = twoVelocities(velocity1, velocity2, position1, position2)
        val expectedVelocity1 = Velocity(0.0,-95.0)
        val expectedVelocity2 = Velocity(  0.0, 95.0,)
        assertEquals(expectedVelocity1.x, velocities.velocity1.x,0.0001)
        assertEquals(expectedVelocity1.y, velocities.velocity1.y,0.0001)
        assertEquals(expectedVelocity2.x, velocities.velocity2.x,0.0001)
        assertEquals(expectedVelocity2.y, velocities.velocity2.y,0.0001)

    }
    @Test
    fun testCheckCollisionY() {
        val offset = sqrt(BALL_RADIUS*BALL_RADIUS/2) * 2
        val position1 = Position(100.0, 100.0)
        val position2 = Position(100.0 +offset, 100.0+ offset)
        val velocity1 = Velocity( 50.0,50.0,)
        val velocity2 = Velocity(-50.0, -50.0, )
        val velocities: TwoVelocities = twoVelocities(velocity1, velocity2, position1, position2)
        val expectedVelocity1 = Velocity(-47.5,-47.5)
        val expectedVelocity2 = Velocity(  47.5, 47.5,)
        assertEquals(expectedVelocity1.x, velocities.velocity1.x,0.0001)
        assertEquals(expectedVelocity1.y, velocities.velocity1.y,0.0001)
        assertEquals(expectedVelocity2.x, velocities.velocity2.x,0.0001)
        assertEquals(expectedVelocity2.y, velocities.velocity2.y,0.0001)

    }

    @Test
    fun testCheckCollisionYY() {
        val position1 = Position(0.0, 0.0)
        val position2 = Position(10.0 , 0.0)
        val velocity1 = Velocity( 2.0,0.0,)
        val velocity2 = Velocity(-1.0, 0.0, )
        val velocities: TwoVelocities = twoVelocities(velocity1, velocity2, position1, position2)
        val expectedVelocity1 = Velocity(-.925, 0.0)
        val expectedVelocity2 = Velocity(  1.925, 0.0,)
        assertEquals(expectedVelocity1.x, velocities.velocity1.x,0.0001)
        assertEquals(expectedVelocity1.y, velocities.velocity1.y,0.0001)
        assertEquals(expectedVelocity2.x, velocities.velocity2.x,0.0001)
        assertEquals(expectedVelocity2.y, velocities.velocity2.y,0.0001)

    }
@Test
fun testCheckCollisionFromTests() {
        val position1 = Position(225.39096570503023,280.5784377627948)
        val position2 = Position(221.0,300.0)
        val velocity1 = Velocity(874.5418780665148,1077.6598499954475)
        val velocity2 = Velocity(0.0,0.0)

        val velocities: TwoVelocities = twoVelocities(velocity1, velocity2, position1, position2)
        println(" $velocities.velocity1  $velocities.velocity2}")
        checkMomentum(velocity1, velocity2, velocities.velocity1, velocities.velocity2)

    }



    @Test
    fun testCheckCollisionFromTests1() {
        val position1 = Position(232.60023688754868,282.36196452014565)
        val position2 = Position(242.0,300.0)
        val velocity1 = Velocity(1030.320041747319,254.89447082351938)
        val velocity2 = Velocity(0.0,0.0)
        val velocities: TwoVelocities = twoVelocities(velocity1, velocity2, position1, position2)
        val expectedVelocity1 = Velocity(687.5568260421572, -346.3172668555393)
        val expectedVelocity2 = Velocity(  342.7632157051619, 601.2117376790586,)
        assertEquals(expectedVelocity1.x, velocities.velocity1.x,0.0001)
        assertEquals(expectedVelocity1.y, velocities.velocity1.y,0.0001)
        assertEquals(expectedVelocity2.x, velocities.velocity2.x,0.0001)
        assertEquals(expectedVelocity2.y, velocities.velocity2.y,0.0001)

    }

    private fun twoVelocities(
        velocity1: Velocity,
        velocity2: Velocity,
        position1: Position,
        position2: Position
    ): TwoVelocities {
        val startEnergy = computeTotalEnergy(velocity1, velocity2)
        val startMomentum = computeTotalMomentum(velocity1, velocity2)
        val velocities: TwoVelocities = computeCollisionVelocity(
            position1, velocity1, position2, velocity2, .95
        )
        val endEnergy = computeTotalEnergyTV(velocities)
        val endMomentum = computeTotalMomentumTV(velocities)
        println("Start $startMomentum and $startEnergy  end $endMomentum and $endEnergy")
        if (!(endEnergy <= startEnergy && endMomentum <= startMomentum)
        )
            println("*** Energy or Momentum Increased ")
        return velocities
    }

}

// This was used to check times
//fun initializeDeltaTimes() : Array<Double> {
//    return arrayOf(
//        0.015799899999997535,
//        0.013361299999999687,
//        0.0105762000000027,
//    )
//}