import org.openrndr.math.Vector2
import kotlin.math.*

fun moveBalls(
    balls: Array<Ball>,
    tableSize: Vector2,
    cushionElasticity: Double,
    restitution: Double,
    rollingResistance: Double,
    segments: Int
) {
    for (loop in 0 until segments) {
        // Compute the new position of the pool ball
        for (index in balls.indices) {
            if (!balls[index].active) continue
            balls[index].velocity =
                checkCushion(balls[index].position, balls[index].velocity, tableSize, cushionElasticity)
        }
        computeCollisions(balls, restitution, segments)
        for (index in balls.indices) {
            if (!balls[index].active) continue
            balls[index].velocity = rollResistance(balls[index].velocity, rollingResistance, segments)
            balls[index].position = updatePosition(balls[index].position, balls[index].velocity, segments)
            checkInPocket(balls[index])
        }
    }
}

fun computeCollisions(balls: Array<Ball>, restitution: Double, segments: Int) {
    for (first in balls.indices) {
        for (second in first + 1 until balls.size) {
            val firstBall = balls[first]
            val secondBall = balls[second]
            if (!firstBall.active) continue
            if (!secondBall.active) continue
            if (colliding(firstBall.position, secondBall.position)) {
                val velocities: TwoVelocities = computeCollisionVelocity(
                    firstBall.position,
                    firstBall.velocity, secondBall.position, secondBall.velocity,
                    restitution
                )
                firstBall.velocity = velocities.velocity1
                secondBall.velocity = velocities.velocity2
                // One way to check to eliminate "double collision"
  /*
                val resultPositions = checkNextSegment(
                    firstBall.position, secondBall.position,
                    firstBall.velocity, secondBall.velocity, segments
                )
                firstBall.position = resultPositions.position1
                secondBall.position = resultPositions.position2
  */
            }
        }
    }
}


fun checkInPocket(ball: Ball) {
    if (ball.position.x < 0.0 - BALL_RADIUS || ball.position.x > tableSize.x + BALL_RADIUS)
        ball.active = false
    if (ball.position.y < 0.0 - BALL_RADIUS || ball.position.y > tableSize.y + BALL_RADIUS)
        ball.active = false
}

// --Commented out by Inspection START (1/16/2023 7:21 PM):
//fun checkNextSegment(
//    position1: Position, position2: Position,
//    velocity1: Velocity, velocity2: Velocity, segments: Int
//): TwoPositions {
//    val thisPosition1 = position1
//    val thisPosition2 = position2
//    for (count in 0..10) {
//        var newPosition1: Position = updatePosition(thisPosition1, velocity1, segments)
//        var newPosition2: Position = updatePosition(thisPosition2, velocity2, segments)
//        if (colliding(newPosition1, newPosition2)) {
//            print("*** Still in collision range after collision " + count.toString() + "\n")
//            val distance = computeDistance(newPosition1, newPosition2)
//            print("\n Distance " + distance.toString())
//            print("p1 p2 " + newPosition1.toString() + " " + newPosition2.toString() + "\n")
//            print("v1 v2 " + velocity1 + " " + velocity2 + "\n")
//            //           thisPosition1 = newPosition1
//            //           thisPosition2 = newPosition2
//            break
//
//        }
//    }
//    return TwoPositions(Position(thisPosition1.x, thisPosition1.y), Position(thisPosition2.x, thisPosition2.y))
//}
// --Commented out by Inspection STOP (1/16/2023 7:21 PM)

@Suppress("BooleanMethodIsAlwaysInverted")
fun colliding(position1: Position, position2: Position): Boolean {
    val distance = computeDistanceSquared(position1, position2)
    return distance <= (2 * BALL_RADIUS) * (2 * BALL_RADIUS)
}

@Suppress("ReplaceWithOperatorAssignment", "LocalVariableName")
fun computeCollisionVelocity(
    position1: Position, velocity1: Velocity,
    position2: Position, velocity2: Velocity, restitution: Double
):
        TwoVelocities {
    // Adopted from https://www.plasmaphysics.org.uk/programs/coll2d_cpp.htm
    // That was a C program.
    val m1 = 1.0
    val m2 = 1.0
    val x1 = position1.x
    val y1 = position1.y
    val x2 = position2.x
    val y2 = position2.y
    var vx1 = velocity1.x
    var vy1 = velocity1.y
    var vx2 = velocity2.x
    var vy2 = velocity2.y

    val m21 = m2 / m1
    var x21 = x2 - x1
    val y21 = y2 - y1
    val vx21 = vx2 - vx1
    val vy21 = vy2 - vy1

    val vx_cm = (m1 * vx1 + m2 * vx2) / (m1 + m2)
    val vy_cm = (m1 * vy1 + m2 * vy2) / (m1 + m2)


    if ((vx21 * x21 + vy21 * y21) >= 0) {
        print("*** not colliding \n")
        return TwoVelocities(velocity1, velocity2)
    }


//     *** I have inserted the following statements to avoid a zero divide;
//         (for single precision calculations,
//          1.0E-12 should be replaced by a larger value). **************

    val fy21 = 1.0E-12 * abs(y21)

    @Suppress("CanBeVal") var sign: Double
    if (abs(x21) < fy21) {
        @Suppress("LiftReturnOrAssignment")
        if (x21 < 0.0) {
            sign = -1.0
        } else {
            sign = 1.0
        }
        x21 = fy21 * sign
    }

//     ***  update velocities ***
    val a = y21 / x21
    val dvx2 = -2 * (vx21 + a * vy21) / ((1 + a * a) * (1 + m21))
    vx2 = vx2 + dvx2
    vy2 = vy2 + a * dvx2
    vx1 = vx1 - m21 * dvx2
    vy1 = vy1 - a * m21 * dvx2

//     ***  velocity correction for inelastic collisions ***
    vx1 = (vx1 - vx_cm) * restitution + vx_cm
    vy1 = (vy1 - vy_cm) * restitution + vy_cm
    vx2 = (vx2 - vx_cm) * restitution + vx_cm
    vy2 = (vy2 - vy_cm) * restitution + vy_cm
    return TwoVelocities(Velocity(vx1, vy1), Velocity(vx2, vy2))
}

// --Commented out by Inspection START (1/16/2023 7:21 PM):
//fun computeTotalEnergy(velocities: TwoVelocities): Double {
//    return computeTotalEnergy(velocities.velocity1, velocities.velocity2)
//}
// --Commented out by Inspection STOP (1/16/2023 7:21 PM)

fun computeTotalEnergy(velocity1: Velocity, velocity2: Velocity): Double {
    return computeEnergy(velocity1) + computeEnergy(velocity2)
}

fun computeTotalMomentum(velocity1: Velocity, velocity2: Velocity): Double {
    return computeMomentum(velocity1) + computeMomentum(velocity2)
}

@Suppress("unused")
fun computeTotalMomentum(velocities: TwoVelocities): Double {
    return computeTotalMomentum(velocities.velocity1, velocities.velocity2)
}

fun computeEnergy(velocity: Velocity): Double {
    // mass is 1.0
    return velocity.x * velocity.x + velocity.y * velocity.y
}

fun computeMomentum(velocity: Velocity): Double {
    return sqrt(velocity.x * velocity.x + velocity.y + velocity.y)
}

fun updatePosition(start: Position, speed: Velocity, segments: Int): Position {
    val end = Position(0.0, 0.0)
    end.x = start.x + speed.x / segments
    end.y = start.y + speed.y / segments
    return end
}

fun rollResistance(velocity: Velocity, rollingResistance: Double, segments: Int): Velocity {
    return Velocity(
        velocity.x * (1.0 - rollingResistance / segments),
        velocity.y * (1.0 - rollingResistance / segments)
    )
}

fun checkCushion(
    position: Position, velocity: Velocity, tableSize: Vector2,
    cushionElasticity: Double
): Velocity {
    val pockets = Pockets()
    var velocityX = velocity.x
    var velocityY = velocity.y
    if (((position.x <= 0.0 + BALL_RADIUS) && velocityX < 0) ||
        ((position.x >= tableSize.x - BALL_RADIUS) && velocityX > 0)
    ) {
        if (checkForNotPocketY(position, pockets)) {
            velocityX = -velocityX
            velocityX *= cushionElasticity
            velocityY *= cushionElasticity
        }
    }
    if (((position.y <= 0.0 + BALL_RADIUS) && velocityY < 0) ||
        ((position.y >= tableSize.y - BALL_RADIUS) && velocityY > 0)
    ) {
        if (checkForNotPocketX(position, pockets)) {
            velocityY = -velocityY
            velocityX *= cushionElasticity
            velocityY *= cushionElasticity
        }
    }
    return Velocity(velocityX, velocityY)
}

private fun checkForNotPocketX(position: Position, pockets: Pockets): Boolean {
    var notInPocket = true
    if (position.x > pockets.topSidePocket.start.x && position.x < pockets.topSidePocket.end.x)
        notInPocket = false
    if (position.x > pockets.topRightPocketTop.start.x)
        notInPocket = false
    if (position.x < pockets.topLeftPocketLeft.end.x)
        notInPocket = false

    return notInPocket
}

private fun checkForNotPocketY(position: Position, pockets: Pockets): Boolean {
    var notInPocket = true

    if (position.y < pockets.topLeftPocketTop.end.y)
        notInPocket = false
    if (position.y > pockets.bottomLeftPocketLeft.start.y)
        notInPocket = false

    return notInPocket
}

fun xyFromAngle(angle: Double): Percentage {
    val result = Percentage(0.0, 0.0)
    val radians = angle * PI / 180.0
    result.x = sin(radians)
    result.y = cos(radians)
    return result
}

fun stoppedMoving(balls: Array<Ball>): Boolean {

    @Suppress("LocalVariableName") val STOPPED_SPEED = .1
    var stopped = true
    var thisStopped: Boolean
    for (index in balls.indices) {
        if (!balls[index].active) continue
        thisStopped = (abs(balls[index].velocity.x) < STOPPED_SPEED) && (abs(balls[index].velocity.y) < STOPPED_SPEED)
        if (!thisStopped) stopped = false
    }
    return stopped
}

fun stopAll(balls: Array<Ball>) {
    for (index in balls.indices) {
        balls[index].velocity = Velocity(0.0, 0.0)

    }

}
/*
fun computeTotalVelocity(velocity: Velocity): Double {
    return Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y)

}
*/

fun computeDistance(position1: Position, position2: Position): Double {
    val distanceX = position1.x - position2.x
    val distanceY = position1.y - position2.y
    return sqrt(distanceX * distanceX + distanceY * distanceY)
}

fun computeDistanceSquared(position1: Position, position2: Position): Double {
    val distanceX = position1.x - position2.x
    val distanceY = position1.y - position2.y
    return distanceX * distanceX + distanceY * distanceY
}

fun totalVelocity(velocity: Velocity): Double {
    return sqrt(velocity.x * velocity.x + velocity.y * velocity.y)
}