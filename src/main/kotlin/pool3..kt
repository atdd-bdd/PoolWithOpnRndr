import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.color.presets.*
import org.openrndr.math.Vector2
import org.openrndr.panel.ControlManager
import org.openrndr.panel.elements.Range
import org.openrndr.panel.elements.button
import org.openrndr.panel.elements.clicked
import org.openrndr.panel.elements.slider
import org.openrndr.panel.layout
import kotlin.math.PI
import kotlin.math.abs

const val BALL_RADIUS = 10.0

fun main() = application {
// Define the initial velocity of the pool ball
    val balls = arrayOf(
        Ball(0, Position(100.0, 200.0), Velocity(0.0, 0.0), true),
        Ball(1, Position(150.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(2, Position(170.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(3, Position(190.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(4, Position(210.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(5, Position(230.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(6, Position(250.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(7, Position(150.0, 320.0), Velocity(0.0, 0.0), true),
        Ball(8, Position(150.0, 340.0), Velocity(0.0, 0.0), true),
        Ball(9, Position(150.0, 360.0), Velocity(0.0, 0.0), true),
        Ball(10, Position(150.0, 380.0), Velocity(0.0, 0.0), true),
        Ball(11, Position(150.0, 400.0), Velocity(0.0, 0.0), true),
        Ball(12, Position(150.0, 420.0), Velocity(0.0, 0.0), true),
        Ball(13, Position(150.0, 440.0), Velocity(0.0, 0.0), true),
        Ball(14, Position(150.0, 460.0), Velocity(0.0, 0.0), true),
        Ball(15, Position(150.0, 480.0), Velocity(0.0, 0.0), true),
    )
    // var previousBalls = balls.map{it.copy()}
    var angle = 0.0
    var force = 0.0

// Define the rolling resistance of the pool ball
    var rollingResistance = 0.000001

// Define the background color

    val colors = arrayOf(
        ColorRGBa.WHITE, ColorRGBa.BLUE, ColorRGBa.MAGENTA,
        ColorRGBa.CYAN, ColorRGBa.YELLOW, ColorRGBa.BLACK,
        ColorRGBa.GREEN, ColorRGBa.BROWN, ColorRGBa.ORANGE,
        ColorRGBa.DARK_GREEN, ColorRGBa.DARK_ORANGE, ColorRGBa.DARK_MAGENTA,
        ColorRGBa.DARK_BLUE, ColorRGBa.DARK_CYAN, ColorRGBa.DARK_BLUE,
        ColorRGBa.AQUA
    )
    val tableUpperLeft = Vector2(0.0, 150.0)
    val tableSize = Vector2(1000.0, 550.0)
    configure {
        width = 1000
        height = 700
    }
    program {
        // Create a control manager to manage the user interface
        var moving = false
        extend(ControlManager()) {
            // Create a horizontal layout to hold the controls
            layout {

                button {
                    if (!moving)
                        label = "Move"
                    else
                        label = "Stop"
                    // -- listen to the click event
                    height = 60
                    width = 40
                    backgroundColor = ColorRGBa.RED
                    clicked {
                        moving = !moving
                        // if (moving)
                        //previousBalls = balls.map{it.copy()}
                    }
                }

                slider {
                    backgroundColor = ColorRGBa.PINK
                    height = 60
                    width = 200
                    label = "rolling resistance"
                    value = rollingResistance
                    range = Range(0.0, 0.05)
                    events.valueChanged.listen { rollingResistance = it.newValue }
                }
                slider {
                    backgroundColor = ColorRGBa.BLUE
                    height = 60
                    width = 200
                    label = "force"
                    value = force
                    range = Range(0.0, 100.0)
                    events.valueChanged.listen { force = it.newValue }
                }
                slider {
                    backgroundColor = ColorRGBa.GREEN
                    height = 60
                    width = 200
                    label = "angle"
                    value = angle
                    range = Range(0.0, 360.0)
                    events.valueChanged.listen { angle = it.newValue }
                }
            }


            // Draw the cue ball
            extend {
                drawTable(tableUpperLeft, tableSize)
                for (index in balls.indices) {
                    drawBall(balls[index].position, BALL_RADIUS, colors[balls[index].symbol])
                }
                if (moving) {
                    // Compute the new position of the pool ball
                    for (index in balls.indices) {
                        balls[index].position = updatePosition(balls[index].position, balls[index].velocity)
                        balls[index].velocity =
                            checkCushion(balls[index].position, balls[index].velocity, tableUpperLeft, tableSize)
                        balls[index].velocity = rollResistance(balls[index].velocity, rollingResistance)
                    }
                    computeCollisions(balls)
                    if (stoppedMoving(balls)) {
                        moving = false
                    }
                } else {
                    val start = Vector2(balls[0].position.x, balls[0].position.y)
                    val percentage: Percentage = xyFromAngle(angle)
                    val xDiff = force * percentage.x * 100.0
                    val yDiff = force * percentage.y * 100.0
                    val end = Vector2(start.x + xDiff, start.y + yDiff)
                    balls[0].velocity.x = force * percentage.x
                    balls[0].velocity.y = force * percentage.y
                    drawer.lineSegment(start, end)
                }
                // Update the position of the pool ball
            }
        }
    }


}

fun computeCollisions(balls: Array<Ball>) {
    for (first in balls.indices) {
        for (second in first + 1 until balls.size) {
            val distance = computeDistance(balls[first].position, balls[second].position)
            val nextFirstPosition = updatePosition(balls[first].position, balls[first].velocity)
            val nextSecondPosition = updatePosition(balls[second].position, balls[second].velocity)
            val nextDistance = computeDistance(nextFirstPosition, nextSecondPosition)
            if ((distance <= 2 * BALL_RADIUS) || (nextDistance <= 2 * BALL_RADIUS)) {
                val velocities: TwoVelocities = computeCollisionVelocity(
                    balls[first].position,
                    balls[first].velocity, balls[second].position, balls[second].velocity
                )
                balls[first].velocity = velocities.velocity1
                balls[second].velocity = velocities.velocity2
            }
        }
    }
}

/*
fun computeOverlap(balls: Array<Ball>) {
    for (first in balls.indices){
        for (second in first + 1 until balls.size){
            val distance = computeDistance(balls[first].position, balls[second].position)
            if ((distance<= 2*BALL_RADIUS) /*&& (next_distance <= distance)*/) {
                val velocities: TwoVelocities = computeCollisionVelocity(
                    balls[first].position,
                    balls[first].velocity, balls[second].position, balls[second].velocity)
                balls[first].velocity = velocities.velocity1
                balls[second].velocity = velocities.velocity2
            }
        }
    }
}
*/
/*
fun computeTotalVelocity(velocity: Velocity): Double {
    return Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y)

}
*/
fun computeCollisionVelocity(
    position1: Position, velocity1: Velocity,
    position2: Position, velocity2: Velocity
):
        TwoVelocities {

    val R1 = 1.0
    val m1 = 1.0
    val m2 = 1.0
    val x1 = position1.x
    val y1 = position1.y
    val x2 = position2.x
    val y2 = position2.y
    var vx1 = velocity1.x
    var vy1 = velocity1.y
    var vx2 = velocity2.x
    var vy2 = velocity2.x

    val m21 = m2 / m1
    var x21 = x2 - x1
    val y21 = y2 - y1
    val vx21 = vx2 - vx1
    val vy21 = vy2 - vy1

    val vx_cm = (m1 * vx1 + m2 * vx2) / (m1 + m2)
    val vy_cm = (m1 * vy1 + m2 * vy2) / (m1 + m2)


//     *** return old velocities if balls are not approaching ***
    // if ( (vx21*x21 + vy21*y21) >= 0) return;


//     *** I have inserted the following statements to avoid a zero divide;
//         (for single precision calculations,
//          1.0E-12 should be replaced by a larger value). **************

    val fy21 = 1.0E-12 * abs(y21)

    var sign: Double
    if (abs(x21) < fy21) {
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
    vx1 = (vx1 - vx_cm) * R1 + vx_cm
    vy1 = (vy1 - vy_cm) * R1 + vy_cm
    vx2 = (vx2 - vx_cm) * R1 + vx_cm
    vy2 = (vy2 - vy_cm) * R1 + vy_cm
    val result = TwoVelocities(Velocity(vx1, vy1), Velocity(vx2, vy2))
    return result
}


fun computeDistance(position1: Position, position2: Position): Double {
    val distance_x = position1.x - position2.x
    val distance_y = position1.y - position2.y
    val distance = Math.sqrt(distance_x * distance_x + distance_y * distance_y)
    return distance
}


fun stoppedMoving(balls: Array<Ball>): Boolean {

    val STOPPED = .01
    var stopped = true
    var this_stopped: Boolean
    for (index in 0..balls.size - 1) {
        this_stopped = (Math.abs(balls[index].velocity.x) < STOPPED) && (Math.abs(balls[index].velocity.y) < STOPPED)
        if (!this_stopped) stopped = false
    }
    return stopped
}

fun rollResistance(velocity: Velocity, rollingResistance: Double): Velocity {
    val newVelocity = Velocity(velocity.x * (1.0 - rollingResistance), velocity.y * (1.0 - rollingResistance))
    return newVelocity
}

fun checkCushion(position: Position, velocity: Velocity, tableUpperLeft: Vector2, tableSize: Vector2): Velocity {
    var velocity_x = velocity.x
    var velocity_y = velocity.y
    if (((position.x <= tableUpperLeft.x + BALL_RADIUS) && velocity_x <= 0) ||
        ((position.x >= tableUpperLeft.x + tableSize.x - BALL_RADIUS) && velocity_x >= 0)
    ) {
        velocity_x = -velocity_x
    }
    if (((position.y <= tableUpperLeft.y + BALL_RADIUS) && velocity_y <= 0) ||
        ((position.y >= tableUpperLeft.y + tableSize.y - BALL_RADIUS) && velocity_y >= 0)
    ) {
        velocity_y = -velocity_y
    }
    return Velocity(velocity_x, velocity_y)
}


private fun Program.drawTable(tableUpperLeft: Vector2, tableSize: Vector2) {
    drawer.fill = ColorRGBa.PURPLE
    drawer.stroke = ColorRGBa.GREEN_YELLOW
    val tableLowerRight = Vector2(tableUpperLeft.x + tableSize.x, tableUpperLeft.y + tableSize.y)
    drawer.rectangle(tableUpperLeft.x, tableUpperLeft.y, tableLowerRight.x, tableLowerRight.y)
    val tableUpperRight = Vector2(tableLowerRight.x, tableUpperLeft.y)
    val tableLowerLeft = Vector2(tableUpperLeft.x, tableLowerRight.y)
    drawer.lineSegment(tableUpperRight, tableUpperLeft)
    drawer.lineSegment(tableUpperLeft, tableLowerLeft)
    drawer.lineSegment(tableLowerRight, tableLowerLeft)
    drawer.lineSegment(tableLowerLeft, tableUpperLeft)
}

private fun Program.drawBall(position: Position, radius: Double, color: ColorRGBa) {
    drawer.fill = color
    val positionV2 = position.toVector2()
    drawer.circle(positionV2, radius)

    // Draw the three circles marking the pool ball

    drawer.stroke = ColorRGBa.BLACK
    drawer.circle(positionV2, radius)
    drawer.circle(positionV2, radius / 2)
    drawer.circle(positionV2, radius / 4)
}

private fun updatePosition(start: Position, speed: Velocity): Position {
    val end = Position(0.0, 0.0)
    end.x = start.x + speed.x
    end.y = start.y + speed.y
    return end
}

private fun xyFromAngle(angle: Double): Percentage {
    val result = Percentage(0.0, 0.0)
    val radians = angle * PI / 180.0
    result.x = Math.sin(radians)
    result.y = Math.cos(radians)
    return result
}

