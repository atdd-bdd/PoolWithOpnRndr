import org.openrndr.math.Vector2
import kotlin.math.*

fun moveBalls(
    balls: Array<Ball>,
    tableSize: Vector2,
    cushionElasticity: Double,
    restitution: Double,
    rollingResistance: Double,
    segments: Int,
    displayIncrement: Int,
    deltaTime: Double,
    pockets: Pockets
) {
//    print (" DeltaTime " +  deltaTime.toString() )
//    print (" Segments " + segments.toString() +
//            " displayIncremenat "  + displayIncrement.toString())
//    print ("Velocity " + balls[0].velocity.x + " " + balls[0].velocity.y)
//        print("\n")

    for (loop in 0 until displayIncrement) {
        // Compute the new position of the pool ball
        for (index in balls.indices) {
            val ball = balls[index]
            if (!ball.active) continue
            ball.velocity =
                checkCushion(balls[index].position, ball.velocity, tableSize, cushionElasticity, pockets)
        }
        computeCollisions(balls, restitution)
        for (index in balls.indices) {
            val ball = balls[index]
            if (!ball.active) continue
            ball.velocity = rollResistance(ball.velocity, rollingResistance, segments,deltaTime)
            ball.position = updatePosition(ball.position, ball.velocity, segments, deltaTime)
            checkInPocket(ball)
        }
    }
}

fun computeCollisions(balls: Array<Ball>, restitution: Double) {
    val collided = BooleanArray(16) { false }
    for (first in balls.indices) {
        val firstBall = balls[first]
        if (!firstBall.active) continue
        if (collided[first]) continue
        for (second in first + 1 until balls.size) {
            val secondBall = balls[second]
            if (!secondBall.active) continue
            if (collided[second]) continue
            if (colliding(firstBall.position, secondBall.position)) {
                val velocities: TwoVelocities = computeCollisionVelocity(
                    firstBall.position,
                    firstBall.velocity, secondBall.position, secondBall.velocity,
                    restitution
                )
                firstBall.velocity = velocities.velocity1
                secondBall.velocity = velocities.velocity2
                collided[first] = true
                collided[second] = true
            }
        }
    }
}


fun checkInPocket(ball: Ball) {
    if (ball.position.x < 0.0 - BALL_RADIUS || ball.position.x > TABLE_SIZE.x + BALL_RADIUS)
        ball.active = false
    if (ball.position.y < 0.0 - BALL_RADIUS || ball.position.y > TABLE_SIZE.y + BALL_RADIUS)
        ball.active = false
    if (!ball.active)
        ball.velocity = Velocity(0.0, 0.0)
}


@Suppress("BooleanMethodIsAlwaysInverted")
fun colliding(position1: Position, position2: Position): Boolean {
    val distance = computeDistanceSquared(position1, position2)
    return distance <= BALL_DIAMETER_SQUARED
}


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

    val checkColliding = (vx21 * x21 + vy21 * y21)
    if (checkColliding >= 0) {
        if (checkColliding > 0) {
//            print("Not colliding " + checkColliding.toString())
        }
        return TwoVelocities(velocity1, velocity2)
    }
//     *** I have inserted the following statements to avoid a zero divide;
//         (for single precision calculations,
//          1.0E-12 should be replaced by a larger value). **************

    val fy21 = 1.0E-12 * abs(y21)
    if (abs(x21) < fy21) x21 = fy21 * getSign(x21)
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

private fun getSign(x21: Double): Double {
    val sign = if (x21 < 0.0) {
        -1.0
    } else {
        1.0
    }
    return sign
}








fun updatePosition(start: Position, speed: Velocity, segments: Int, deltaTime: Double): Position {
    return Position(start.x + speed.x * deltaTime / segments, start.y + speed.y * deltaTime / segments)
}

fun rollResistance(velocity: Velocity, rollingResistance: Double, segments: Int, deltaTime: Double): Velocity {
    val SCALE = 30
    return Velocity(
        velocity.x * (1.0 - rollingResistance*deltaTime * SCALE / segments),
        velocity.y * (1.0 - rollingResistance*deltaTime * SCALE / segments)
    )
}

fun checkCushion(
    position: Position, velocity: Velocity, tableSize: Vector2,
    cushionElasticity: Double, pockets: Pockets
): Velocity {
    return pockets.checkCushion(position, velocity, tableSize, cushionElasticity)
    /*
    var velocityX = velocity.x
    var velocityY = velocity.y
    if (atHead(position, velocityX) ||
        atFoot(position, tableSize, velocityX)
        
    ) {
        if (checkForNotPocketY(position, pockets, tableSize)) {
            velocityX = -velocityX
            velocityX *= cushionElasticity
            velocityY *= cushionElasticity
        }
    }
    if (atLeftSide(position, velocityY) ||
        atRightSide(position, tableSize, velocityY)
    ) {
        if (checkForNotApproachingSidePockets(position, pockets, tableSize)) {
            velocityY = -velocityY
            velocityX *= cushionElasticity
            velocityY *= cushionElasticity
        }
    }
    return Velocity(velocityX, velocityY)

     */
}

private fun atRightSide(position: Position, tableSize: Vector2, velocityY: Double) =
    ((position.y >= tableSize.y - BALL_RADIUS) && velocityY > 0)

private fun atLeftSide(position: Position, velocityY: Double) = ((position.y <= 0.0 + BALL_RADIUS) && velocityY < 0)

private fun atFoot(position: Position, tableSize: Vector2, velocityX: Double) =
    ((position.x >= tableSize.x - BALL_RADIUS) && velocityX > 0)

private fun atHead(position: Position, velocityX: Double) = ((position.x <= 0.0 + BALL_RADIUS) && velocityX < 0)

private fun checkForNotApproachingSidePockets(position: Position, pockets: Pockets, tableSize:Vector2): Boolean {
    var notInPocket = true
    if (position.x > pockets.leftSidePocketLine.start.x + BALL_RADIUS && position.x < pockets.leftSidePocketLine.end.x - BALL_RADIUS )
        notInPocket = false
    if (position.x < pockets.headLeftCornerPocketSideLine.end.x- BALL_RADIUS)
        notInPocket = false
    if (position.x > pockets.footLeftCornerPocketSideLine.start.x + BALL_RADIUS )
        notInPocket = false
    if (position.y < 0.0 || position.y > tableSize.y)
        notInPocket = false
    return notInPocket
}

private fun checkForNotPocketY(position: Position, pockets: Pockets, tableSize:Vector2): Boolean {
    var notInPocket = true

    if (position.y < pockets.headLeftCornerPocketHeadLine.end.y-BALL_RADIUS)
        notInPocket = false
    if (position.y > pockets.headRightCornerPocketHeadLine.start.y+BALL_RADIUS)
        notInPocket = false
    if (position.x < 0.0 || position.x > tableSize.x)
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

    val STOPPED_SPEED = 2.5
    var stopped = true
    var thisStopped: Boolean
    for (index in balls.indices) {
        val ball = balls[index]
        if (!ball.active) continue
        thisStopped = (abs(ball.velocity.x) < STOPPED_SPEED) && (abs(ball.velocity.y) < STOPPED_SPEED)
        if (!thisStopped) stopped = false
    }
    return stopped
}

fun stopAll(balls: Array<Ball>) {
    for (index in balls.indices) {
        balls[index].velocity = Velocity(0.0, 0.0)
    }

}



fun computeDistanceSquared(position1: Position, position2: Position): Double {
    val distanceX = position1.x - position2.x
    val distanceY = position1.y - position2.y
    return distanceX * distanceX + distanceY * distanceY
}

fun totalVelocity(velocity: Velocity): Double {
    return sqrt(velocity.x * velocity.x + velocity.y * velocity.y)
}

fun checkForDropLocation(whichBall:Int, startPosition: Position, endMousePosition: Position, balls: Array<Ball>): Position {
    var endPosition = endMousePosition
    val numberSegementsToCompute = 1000
    val deltaX = (endPosition.x - startPosition.x)/numberSegementsToCompute
    val deltaY = (endPosition.y - startPosition.y) / numberSegementsToCompute
    var moved = true
    var numberTimesToCheckForOverlap = 15
    while (moved && numberTimesToCheckForOverlap-- > 0 ) {
        moved = false
        for (index in balls.indices) {
            if (index == whichBall) continue
            for (number in 0..numberSegementsToCompute) {
                if (colliding(endPosition, balls[index].position)) {
                    endPosition = Position(endPosition.x - deltaX, endPosition.y - deltaY)
                    moved = true
                }
            }
        }
    }
    return endPosition
}


fun rackBalls(balls: Array<Ball>) {
    val headBallPosition = Position(3 * TABLE_SIZE.x / 4, TABLE_SIZE.y / 2)
    val diameter = BALL_RADIUS*2
    val rowDistance = Math.sqrt(diameter*diameter - BALL_RADIUS*BALL_RADIUS)
    val cueBallPosition = (Position(TABLE_SIZE.x / 4, TABLE_SIZE.y / 2))
    val positions = arrayOf<Position>(
        Position(0.0, 0.0),

        Position(rowDistance, BALL_RADIUS),
        Position(rowDistance, -BALL_RADIUS),

        Position(rowDistance * 2, diameter),
        Position(rowDistance * 2, 0.0),
        Position(rowDistance * 2, -diameter),

        Position(rowDistance * 3, BALL_RADIUS + diameter),
        Position(rowDistance * 3, BALL_RADIUS),
        Position(rowDistance * 3, -BALL_RADIUS),
        Position(rowDistance * 3, -(BALL_RADIUS + diameter)),

        Position(rowDistance * 4, diameter * 2),
        Position(rowDistance * 4, diameter),
        Position(rowDistance * 4, 0.0),
        Position(rowDistance * 4, -diameter),
        Position(rowDistance * 4, -diameter * 2),
    )
    balls[0].position = cueBallPosition
    balls[0].active = true
    balls[0].velocity = Velocity(0.0, 0.0)
    for (index in positions.indices)
    {
        balls[index + 1].position = Position(positions[index].x + headBallPosition.x,
            positions[index].y + headBallPosition.y)
        balls[index + 1].active = true
        balls[index+1].velocity = Velocity(0.0, 0.0)
    }
}