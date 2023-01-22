import org.openrndr.MouseEvent
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.color.presets.*
import org.openrndr.math.Vector2
import org.openrndr.panel.ControlManager
import org.openrndr.panel.elements.*
import org.openrndr.panel.layout
import org.openrndr.panel.style.*
import org.openrndr.panel.styleSheet

const val BALL_RADIUS = 10.00
const val BALL_DIAMETER_SQUARED = (BALL_RADIUS * 2) * (BALL_RADIUS * 2)
val TABLE_SIZE = Vector2(900.0, 450.0)

private const val MOUSE_NOT_ON_BALL = -1

fun main() = application {
    val mINIMUM_RESISTANCE = 0.0
    val mAXIMUM_RESISTANCE = 0.05
    val mAXIMUM_CUSHION_ELASTICITY = 1.0
    val mAXIMUM_RESTITUTION = 1.0
    val mAXIMUM_FORCE = 100.0

    var cueAngle = 0.0
    var cueForce = 0.0

    var startingVelocity = Velocity(0.0, 0.0)

    var rollingResistance = 0.02
    var restitution = 0.95
    var cushionElasticity = 0.70

    var balls: Array<Ball> = initialBalls()
    var previousBalls: List<Ball> = balls.map { it.copy() }

    val colors = colorOfBalls()
    val stripes = stripOnBalls()
    val tableUpperLeft = Vector2(50.0, 200.0)
    var computationSegments = 100
    configure {
        width = 1000
        height = 700
        title = "Pool"
    }
    program {
        var moving = false
        var ballMoving = MOUSE_NOT_ON_BALL
        var startPosition = Position(0.0, 0.0)

        mouse.buttonDown.listen {
            //print( it.position.toString() + "\n")
            val currentPosition = mouseToPosition(it, tableUpperLeft)
            ballMoving = whichBallToMove(currentPosition, balls)
            if (ballMoving != MOUSE_NOT_ON_BALL) {
                startPosition = currentPosition
                balls[ballMoving].velocity = Velocity(0.0, 0.0)
                balls[ballMoving].active = true
            }
        }
        mouse.buttonUp.listen {
            //print( it.position.toString() + "\n")
            if (ballMoving != MOUSE_NOT_ON_BALL) {
                val endPosition =  mouseToPosition(it, tableUpperLeft)
                balls[ballMoving].position =
                    checkForDropLocation(ballMoving, startPosition, endPosition, balls)
            }
            ballMoving = MOUSE_NOT_ON_BALL
        }
        mouse.dragged.listen {
            if (ballMoving != MOUSE_NOT_ON_BALL) balls[ballMoving].position =
                mouseToPosition(it, tableUpperLeft)
        }
        extend(ControlManager()) {
            layout {
                backgroundColor = ColorRGBa.WHITE
                styleSheet(has class_ "horizontal") {
                    paddingLeft = 10.px
                    paddingTop = 10.px
                    background = Color.RGBa(ColorRGBa.LIGHT_GREEN)
                    // ----------------------------------------------
                    // The next two lines produce a horizontal layout
                    // ----------------------------------------------
                    display = Display.FLEX
                    flexDirection = FlexDirection.Row
                    width = 95.percent
                }

                styleSheet(has type "button") {
                    background = Color.RGBa(ColorRGBa.WHITE)
                    color = Color.RGBa(ColorRGBa.BLACK)
                    height = 40.px
                }
                styleSheet(has type "slider") {
                    color = Color.RGBa(ColorRGBa.YELLOW)
                    background = Color.RGBa(ColorRGBa.DARK_BLUE)
                    height = 40.px
                }
                div("horizontal") {
                    button {
                         label = "Cue Stroke"
                        clicked {
                            moving = true
                            val segmentsPossibly = totalVelocity(startingVelocity) / 0.1
                            print("Possible segments $segmentsPossibly\n")
                            computationSegments = 100
                            previousBalls = balls.map { it.copy() }
                            hitCue(balls, startingVelocity)
                        }
                    }
                    button {
                        label = "Replay"
                        clicked {
                            balls = restoreBalls(previousBalls)
                        }
                    }
                    button {
                        label = "Stop"
                        clicked {
                            stopAll(balls)
                        }
                    }
                    button {
                        label = "Load"
                        clicked {
                            print("Load file")
                        }
                    }
                    button {
                        label = "Store"
                        clicked {
                            print("Store file")
                        }
                    }
                    slider {
                        style = styleSheet {
                            width = 100.px
                        }
                        label = "Restitution"
                        value = restitution
                        range = Range(0.0, mAXIMUM_RESTITUTION)
                        events.valueChanged.listen { restitution = it.newValue }
                    }
                    slider {
                        style = styleSheet {
                            width = 100.px
                        }
                        label = "Cushion Elasticity"
                        value = cushionElasticity
                        range = Range(0.0, mAXIMUM_CUSHION_ELASTICITY)
                        events.valueChanged.listen { cushionElasticity = it.newValue }
                    }
                    slider {
                        style = styleSheet {
                            width = 100.px
                        }
                        label = "Rolling Resistance"
                        value = rollingResistance
                        range = Range(mINIMUM_RESISTANCE, mAXIMUM_RESISTANCE)
                        events.valueChanged.listen { rollingResistance = it.newValue }
                    }
                }//div
                div("horizontal") {
                    slider {
                        label = "Force"
                        value = cueForce
                        range = Range(0.0, mAXIMUM_FORCE)
                        events.valueChanged.listen { cueForce = it.newValue }
                    }
                }//div
                div("horizontal") {
                    slider {
                        label = "Angle"
                        value = cueAngle
                        range = Range(0.0, 360.0)
                        events.valueChanged.listen { cueAngle = it.newValue }
                    }
                }//div
            }

            extend {
                drawTable(tableUpperLeft, TABLE_SIZE)
                drawBalls(balls, colors, stripes  ,tableUpperLeft)
                if (moving) {
                    moveBalls(balls, TABLE_SIZE, cushionElasticity, restitution, rollingResistance, computationSegments)
                    if (stoppedMoving(balls)) {
                        moving = false
                        stopAll(balls)
                    }
                } else {
                    val percentage: Percentage = xyFromAngle(cueAngle)
                    startingVelocity = Velocity(cueForce * percentage.x, cueForce * percentage.y)
                    drawCueLine(balls, tableUpperLeft, cueForce / mAXIMUM_FORCE, percentage, TABLE_SIZE.x)
                }
            }
        }
    }
}

private fun mouseToPosition(it: MouseEvent, tableUpperLeft: Vector2) =
    Position(it.position.x - tableUpperLeft.x, it.position.y - tableUpperLeft.y)

fun whichBallToMove(position: Position, balls: Array<Ball>): Int {
    var whichBall = MOUSE_NOT_ON_BALL
    for (index in balls.indices) {
        if (computeDistanceSquared(position, balls[index].position) < BALL_DIAMETER_SQUARED) {
            whichBall = index
            break
        }

    }

    return whichBall

}

fun restoreBalls(previousBalls: List<Ball>): Array<Ball> {
    val size = previousBalls.size
    val initBall = Ball(1, Position(0.0, 0.0), Velocity(0.0, 0.0), true)
    val balls = Array<Ball>(size) { _ -> initBall }
    for ((index, ball) in previousBalls.withIndex()) {
        balls[index] = ball
    }
    return balls
}

private fun Program.drawBalls(
    balls: Array<Ball>, colors: Array<ColorRGBa>, hasStripe: Array<Boolean>, tableUpperLeft: Vector2,
) {
    for (index in balls.indices) {
        val ball = balls[index]
        val ballColorIndex = ball.symbol
        drawBall(ball.position, BALL_RADIUS, colors[ballColorIndex],
            hasStripe[ballColorIndex], tableUpperLeft)
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

    ColorRGBa.YELLOW,
    ColorRGBa.BLUE,
    ColorRGBa.RED,
    ColorRGBa.PURPLE,
    ColorRGBa.ORANGE,
    ColorRGBa.GREEN,
    ColorRGBa.DARK_MAGENTA,)

private fun stripOnBalls() = arrayOf(
    false,

    false,
    false,
    false,
    false,
    false,
    false,
    false,

    false,

    true,
    true,
    true,
    true,
    true,
    true,
    true,
 )

private fun Program.drawCueLine(
    balls: Array<Ball>, tableUpperLeft: Vector2, cueForce: Double, percentage: Percentage, length: Double,
) {
    val start = Vector2(balls[0].position.x + tableUpperLeft.x, balls[0].position.y + tableUpperLeft.y)
    val xDiff = length * percentage.x
    val yDiff = length * percentage.y
    val end = Vector2(start.x + xDiff, start.y + yDiff)
    drawer.strokeWeight = 1.0
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
    position: Position, radius: Double, color: ColorRGBa, hasStripe: Boolean, tableUpperLeft: Vector2,
) {
    val positionV1 = position.toVector2()
    val positionV2 = Vector2(
        positionV1.x + tableUpperLeft.x, positionV1.y + tableUpperLeft.y
    )
    if (!hasStripe) {
        drawer.fill = color
        drawer.strokeWeight = 1.0
        drawer.stroke = ColorRGBa.BLACK
        drawer.circle(positionV2, radius)
    }
    else {
        drawer.fill = ColorRGBa.WHITE
        drawer.strokeWeight = 1.0
        drawer.stroke = ColorRGBa.BLACK
        drawer.circle(positionV2, radius)
        drawer.stroke = color
        drawer.strokeWeight = radius - 2.0
        val offset = radius - 2.0
        val startPosition = Vector2(positionV2.x - offset, positionV2.y)
        val endPosition = Vector2(positionV2.x + offset, positionV2.y )
        drawer.lineSegment(startPosition, endPosition  )
    }
}

private fun initialBalls(): Array<Ball> {
    return arrayOf(
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
}
