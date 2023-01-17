import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.color.presets.*
import org.openrndr.math.Vector2
import org.openrndr.panel.ControlManager
import org.openrndr.panel.elements.*
import org.openrndr.panel.layout
import org.openrndr.panel.style.Color
import org.openrndr.panel.style.background
import org.openrndr.panel.style.color
import org.openrndr.panel.style.has
import org.openrndr.panel.styleSheet

const val BALL_RADIUS = 10.00
val tableSize = Vector2(900.0, 450.0)

private const val NOT_ON_BALL = -1

fun main() = application {
// Define the initial velocity of the pool ball
    var balls: Array<Ball> = initialBalls()
    // var previousBalls = balls.map{it.copy()}
    @Suppress("LocalVariableName", "LocalVariableName", "LocalVariableName")
    val MINIMUM_RESISTANCE = 0.0
    val MAXIMUM_RESISTANCE = 0.05
    val MAXIMUM_FORCE = 100.0
    var cueAngle = 0.0
    var cueForce = 0.0
    var startingVelocity = Velocity(0.0, 0.0)
// Define the rolling resistance of the pool ball
    var rollingResistance = 0.000001
    val restitution = 0.95
    val cushionElasticity = 0.70
    var previousBalls: List<Ball> = balls.map { it.copy() }

    val colors = colorOfBalls()
    val tableUpperLeft = Vector2(20.0, 200.0)
    var segments = 100
    configure {
        width = 1000
        height = 700
        title = "Pool"
    }
    program {
        // Create a control manager to manage the user interface
        var moving = false
        var ballMoving = NOT_ON_BALL
        mouse.buttonDown.listen {
            //print( it.position.toString() + "\n")
            ballMoving = whichBallToMove(it.position, balls, tableUpperLeft)
            if (ballMoving != NOT_ON_BALL) {
                balls[ballMoving].velocity = Velocity(0.0, 0.0)
                balls[ballMoving].active = true
            }
        }
        mouse.buttonUp.listen {
            //print( it.position.toString() + "\n")
            ballMoving = NOT_ON_BALL
        }
        mouse.dragged.listen {
            if (ballMoving != NOT_ON_BALL)
                balls[ballMoving].position =
                    Position(it.position.x - tableUpperLeft.x, it.position.y - tableUpperLeft.y)
            //print(it.position.toString() + "\n")
        }
        extend(ControlManager()) {
            // Create a horizontal layout to hold the controls
            layout {
                styleSheet(has type "button") {
                    background = Color.RGBa(ColorRGBa.WHITE)
                    color = Color.RGBa(ColorRGBa.BLACK)
                }
                div {
                    button {
                        label = "Cue Stroke"
                        height = 40
                        width = 40
                        clicked {
                            moving = true
                            val segmentsPossibly = totalVelocity(startingVelocity) / 0.1
                            print(
                                " Possible segments $segmentsPossibly velocity $startingVelocity\n"
                            )
                            segments = 100
                            previousBalls = balls.map { it.copy() }
                            hitCue(balls, startingVelocity)
                        }
                    }
                    button {
                        label = "Replay"
                        // -- listen to the click event
                        height = 60
                        width = 40
                        backgroundColor = ColorRGBa.RED
                        clicked {
                            balls = restoreBalls(previousBalls)

                        }
                    }
                }
                slider {
                    backgroundColor = ColorRGBa.BLUE
                    height = 60
                    width = 200
                    label = "Rolling Resistance"
                    value = rollingResistance
                    range = Range(MINIMUM_RESISTANCE, MAXIMUM_RESISTANCE)
                    events.valueChanged.listen { rollingResistance = it.newValue }
                }
                slider {
                    backgroundColor = ColorRGBa.BLUE
                    height = 60
                    width = 200
                    label = "Force"
                    value = cueForce
                    range = Range(0.0, MAXIMUM_FORCE)
                    events.valueChanged.listen { cueForce = it.newValue }
                }
                slider {
                    backgroundColor = ColorRGBa.WHITE
                    height = 60
                    width = 200
                    label = "Angle"
                    value = cueAngle
                    range = Range(0.0, 360.0)
                    events.valueChanged.listen { cueAngle = it.newValue }
                }

            }

            extend {
                drawTable(tableUpperLeft, tableSize)
                drawBalls(balls, colors, tableUpperLeft)
                if (moving) {
                    moveBalls(balls, tableSize, cushionElasticity, restitution, rollingResistance, segments)
                    if (stoppedMoving(balls)) {
                        moving = false
                        stopAll(balls)
                    }
                } else {
                    val percentage: Percentage = xyFromAngle(cueAngle)
                    startingVelocity = Velocity(cueForce * percentage.x, cueForce * percentage.y)
                    drawCueLine(balls, tableUpperLeft, cueForce / MAXIMUM_FORCE, percentage, tableSize.x)
                }
            }
        }
    }
}

fun whichBallToMove(vector: Vector2, balls: Array<Ball>, tableUpperLeft: Vector2): Int {
    var whichBall = NOT_ON_BALL
    val position = Position(vector.x - tableUpperLeft.x, vector.y - tableUpperLeft.y)
    for (index in balls.indices) {
        if (computeDistanceSquared(position, balls[index].position) <
            ((2 * BALL_RADIUS) * (2 * BALL_RADIUS))
        ) {
            whichBall = index
            break
        }

    }

    return whichBall

}

fun restoreBalls(previousBalls: List<Ball>): Array<Ball> {
    val size = previousBalls.size
    val initBall = Ball(1, Position(0.0, 0.0), Velocity(0.0, 0.0), true)
    var balls = Array<Ball>(size) { i -> initBall }
    for ((index, ball) in previousBalls.withIndex()) {
        balls[index] = ball
    }
    return balls

}

private fun Program.drawBalls(
    balls: Array<Ball>, colors: Array<ColorRGBa>, tableUpperLeft: Vector2
) {
    for (index in balls.indices) {
        drawBall(balls[index].position, BALL_RADIUS, colors[balls[index].symbol], tableUpperLeft)
    }
}

private fun colorOfBalls() = arrayOf(
    ColorRGBa.WHITE,

    ColorRGBa.YELLOW,
    ColorRGBa.BLUE,
    ColorRGBa.RED,
    ColorRGBa.PURPLE,
    ColorRGBa.ORANGE,
    ColorRGBa.GREEN,
    ColorRGBa.DARK_MAGENTA,

    ColorRGBa.BLACK,

    ColorRGBa.LIGHT_YELLOW,
    ColorRGBa.LIGHT_BLUE,
    ColorRGBa.LIGHT_PINK,
    ColorRGBa.PALE_TURQUOISE,
    ColorRGBa.ORANGE_RED,
    ColorRGBa.DARK_GREEN,
    ColorRGBa.DARK_BLUE
)


private fun Program.drawCueLine(
    balls: Array<Ball>, tableUpperLeft: Vector2, cueForce: Double, percentage: Percentage, length: Double
) {
    val start = Vector2(balls[0].position.x + tableUpperLeft.x, balls[0].position.y + tableUpperLeft.y)
    val xDiff = length * percentage.x
    val yDiff = length * percentage.y
    val end = Vector2(start.x + xDiff, start.y + yDiff)
    drawer.fill = ColorRGBa(.5, .5, cueForce)
    drawer.lineSegment(start, end)
}


fun hitCue(balls: Array<Ball>, startingVelocity: Velocity) {
    balls[0].velocity = startingVelocity
}

private fun Program.drawTable(tableUpperLeft: Vector2, tableSize: Vector2) {
    val pockets = Pockets()
    drawer.fill = ColorRGBa.LIGHT_BLUE
    drawer.stroke = ColorRGBa.GREEN_YELLOW
    drawer.strokeWeight = 2.0
    val tableLowerRight = Vector2(tableUpperLeft.x + tableSize.x, tableUpperLeft.y + tableSize.y)
    drawer.rectangle(tableUpperLeft.x, tableUpperLeft.y, tableSize.x, tableSize.y)
    val tableUpperRight = Vector2(tableLowerRight.x, tableUpperLeft.y)
    val tableLowerLeft = Vector2(tableUpperLeft.x, tableLowerRight.y)
    drawer.lineSegment(tableUpperRight, tableUpperLeft)
    drawer.lineSegment(tableUpperLeft, tableLowerLeft)
    drawer.lineSegment(tableLowerRight, tableLowerLeft)
    drawer.lineSegment(tableLowerLeft, tableUpperLeft)
    // pocket
    drawer.stroke = ColorRGBa.PINK
    drawer.strokeWeight = 4.0

    drawPocketLine(pockets.topSidePocket, tableUpperLeft)
    drawPocketLine(pockets.bottomSidePocket, tableUpperLeft)
    drawPocketLine(pockets.bottomLeftPocketBottom, tableUpperLeft)
    drawPocketLine(pockets.bottomLeftPocketLeft, tableUpperLeft)
    drawPocketLine(pockets.bottomRightPocketRight, tableUpperLeft)
    drawPocketLine(pockets.bottomRightPocketBottom, tableUpperLeft)
    drawPocketLine(pockets.topLeftPocketLeft, tableUpperLeft)
    drawPocketLine(pockets.topLeftPocketTop, tableUpperLeft)
    drawPocketLine(pockets.topRightPocketTop, tableUpperLeft)
    drawPocketLine(pockets.topRightPocketRight, tableUpperLeft)

}

private fun Program.drawPocketLine(lineSegment: LineSegment, tableUpperLeft: Vector2) {
    drawer.lineSegment(lineSegment.start + tableUpperLeft, lineSegment.end + tableUpperLeft)
}

private fun Program.drawBall(
    position: Position, radius: Double, color: ColorRGBa, tableUpperLeft: Vector2
) {
    drawer.fill = color
    val positionV1 = position.toVector2()
    val positionV2 = Vector2(
        positionV1.x + tableUpperLeft.x, positionV1.y + tableUpperLeft.y
    )
    drawer.circle(positionV2, radius)

    // Draw the three circles marking the pool ball

    drawer.stroke = ColorRGBa.BLACK
    drawer.strokeWeight = 1.0
    drawer.circle(positionV2, radius)
    drawer.circle(positionV2, radius / 2)
    drawer.circle(positionV2, radius / 4)
}

private fun initialBalls(): Array<Ball> {
    val balls = arrayOf(
        Ball(0, Position(160.0, 200.0), Velocity(0.0, 0.0), true),
        Ball(1, Position(200.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(2, Position(221.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(3, Position(242.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(4, Position(263.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(5, Position(284.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(6, Position(305.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(7, Position(150.0, 321.0), Velocity(0.0, 0.0), true),
        Ball(8, Position(150.0, 342.0), Velocity(0.0, 0.0), true),
        Ball(9, Position(150.0, 363.0), Velocity(0.0, 0.0), true),
        Ball(10, Position(150.0, 384.0), Velocity(0.0, 0.0), true),
        Ball(11, Position(150.0, 405.0), Velocity(0.0, 0.0), true),
        Ball(12, Position(150.0, 300.0), Velocity(0.0, 0.0), true),
        Ball(13, Position(400.0, 322.0), Velocity(0.0, 0.0), true),
        Ball(14, Position(400.0, 344.0), Velocity(0.0, 0.0), true),
        Ball(15, Position(400.0, 366.0), Velocity(0.0, 0.0), true),
    )
    return balls
}
