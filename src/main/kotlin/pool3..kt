@file:Suppress("LocalVariableName")

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import org.openrndr.*
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.extra.color.presets.*
import org.openrndr.math.Vector2
import org.openrndr.panel.ControlManager
import org.openrndr.panel.elements.*
import org.openrndr.panel.layout
import org.openrndr.panel.style.*
import org.openrndr.panel.style.Display

import org.openrndr.panel.styleSheet
import org.openrndr.shape.Rectangle
import kotlin.math.roundToInt

const val BALL_RADIUS = 10.00
const val BALL_DIAMETER_SQUARED = (BALL_RADIUS * 2) * (BALL_RADIUS * 2)
val TABLE_SIZE = Vector2(900.0, 450.0)

private const val MOUSE_NOT_ON_BALL = -1


fun main() = application {
    val client = HttpClient {
        install(WebSockets)
    }
    val fontFileName = turnResourceIntoFile("/data/fonts/default.otf")

    val MINIMUM_RESISTANCE = 0.0
    val MAXIMUM_RESISTANCE = 0.05
    val MAXIMUM_CUSHION_ELASTICITY = 1.0
    val MAXIMUM_RESTITUTION = 1.0
    val MAXIMUM_FORCE = 5000.0

    var balls: Array<Ball> = initialBalls()
    var previousBalls = initialBalls()
    val colors = colorOfBalls()
    val stripes = stripOnBalls()

    var configuration = Configuration()

    var startingVelocity = Velocity(0.0, 0.0)

    var computationSegments = 100
    var moveCount = 0
    var totalMoveTime = 0.0

    val tableUpperLeft = Vector2(250.0, 230.0)

    var previousMessageDateStamp = 0.0
    var yourID = ""
    var opponentID = ""
    var yourMessage = ""
    var opponentMessage = ""
    var switchedTurns = 0

    configure {
        width = 1200
        height = 750
        title = "Pool"
        minimumHeight = 700
        minimumWidth = 1200
        windowResizable = false
    }
    program {
        val messageIn = Message()
        val messageOut = Message()
        var your_turn = true
        var previousMessageSendDelta = program.seconds
        var previousTime = program.seconds
        var moving = false
        var startMoving = false
        var ballMoving = MOUSE_NOT_ON_BALL
        var startPosition = Position(0.0, 0.0)
        val pockets = Pockets()
        var errorMessage = ""
        var statusMessage: String
        var noMessageCount = 0
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
                val endPosition = mouseToPosition(it, tableUpperLeft)
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
                    paddingTop = 5.px
                    background = Color.RGBa(ColorRGBa.LIGHT_GREEN)
                    // ----------------------------------------------
                    // The next two lines produce a horizontal layout
                    // ----------------------------------------------
                    display = Display.FLEX
                    flexDirection = FlexDirection.Row
                    width = 95.percent
                    height = 60.px
                }
                styleSheet(has type "textfield") {
                    color = Color.RGBa(ColorRGBa.BLACK)
                    height = 40.px
                    fontSize = 18.px
                }

                styleSheet(has type "button") {
                    background = Color.RGBa(ColorRGBa.WHITE)
                    color = Color.RGBa(ColorRGBa.BLACK)
                    height = 40.px
                    fontSize = 18.px
                }
                styleSheet(has type "slider") {
                    color = Color.RGBa(ColorRGBa.BLACK)
                    background = Color.RGBa(ColorRGBa.DARK_BLUE)
                    height = 40.px
                    fontSize = 12.px

                }
                styleSheet(has class_ "side-bar") {
                    this.height = 380.px
                    this.width = 200.px
                    this.display = Display.FLEX
                    this.flexDirection = FlexDirection.Column
                    this.paddingLeft = 10.px
                    this.paddingRight = 10.px
                    this.background = Color.RGBa(ColorRGBa.LIGHT_GREEN)
                }
                div("horizontal") {
                    slider {
                        label = "Force"
                        value = configuration.cueForce
                        range = Range(0.0, MAXIMUM_FORCE)
                        events.valueChanged.listen { configuration.cueForce = it.newValue }
                    }
                }//div
                div("horizontal") {
                    slider {
                        label = "Angle"
                        value = configuration.cueAngle
                        range = Range(0.0, 360.0)
                        events.valueChanged.listen { configuration.cueAngle = it.newValue }
                    }
                }//div
                div("horizontal") {


                    button {
                        label = "Cue Stroke"
                        clicked {
                            startMoving = true

                        }
                    }
                    button {
                        label = "Replay"
                        clicked {
                            balls = copyBalls(previousBalls)
                        }
                    }
                    button {
                        label = "Stop"
                        clicked {
                            stopAll(balls)
                        }
                    }
                    button {
                        label = "Rack 'em"
                        clicked {
                            rackBalls(balls)
                        }
                    }
                    button {
                        label = "Load Game"
                        clicked {
                            balls = loadGameFromFile(balls)

                        }
                    }

                    button {
                        label = "Save Game"
                        clicked {
                            saveGameToFile(balls)
                        }
                    }
                    button {
                        label = "Load Config"
                        clicked {
                            configuration = loadConfigurationFromFile()
                        }

                    }
                    button {
                        label = "Save Config"
                        clicked {
                            saveConfigurationToFile(configuration)
                        }
                    }
                    slider {
                        style = styleSheet {
                            width = 180.px
                        }
                        label = "Trim Angle"
                        value = configuration.cueAngleTrim
                        range = Range(-2.0, 2.0)

                        events.valueChanged.listen { configuration.cueAngleTrim = it.newValue }
                    }

                }//div
                div("side-bar") {

                    slider {
                        style = styleSheet {
                            width = 180.px
                        }
                        label = "Restitution"
                        value = configuration.restitution
                        range = Range(0.0, MAXIMUM_RESTITUTION)

                        events.valueChanged.listen { configuration.restitution = it.newValue }
                    }
                    slider {
                        style = styleSheet {
                            width = 180.px
                        }
                        label = "Cushion Elasticity"
                        value = configuration.cushionElasticity
                        range = Range(0.0, MAXIMUM_CUSHION_ELASTICITY)
                        events.valueChanged.listen { configuration.cushionElasticity = it.newValue }
                    }
                    slider {
                        style = styleSheet {
                            width = 180.px
                        }
                        label = "Rolling Resistance"
                        value = configuration.rollingResistance
                        range = Range(MINIMUM_RESISTANCE, MAXIMUM_RESISTANCE)
                        events.valueChanged.listen { configuration.rollingResistance = it.newValue }
                    }
                    slider {
                        style = styleSheet {
                            width = 180.px
                        }

                        label = "Display Speed"
                        value = configuration.displayIncrement.toDouble()
                        range = Range(1.0, computationSegments.toDouble())
                        events.valueChanged.listen { configuration.displayIncrement = (it.newValue + .01).toInt() }
                    }
                    textfield {
                        label = "Your ID"
                        value = yourID
                        events.valueChanged.listen {
                            yourID = it.newValue
                        }
                    }
                    textfield {
                        label = "Opponent ID"
                        value = opponentID
                        events.valueChanged.listen {
                            opponentID = it.newValue
                        }
                    }
                    textfield {
                        style = styleSheet {
                        }
                        label = "Message to opponent"
                        value = yourMessage
                        events.valueChanged.listen {
                            yourMessage = it.newValue
                        }
                    }
                    button {
                        label = "Switch turn"
                        clicked {
                            your_turn = !your_turn
                            switchedTurns = 0
                        }
                    }


                }
            }
        }

        extend {
            var deltaTime = this.seconds - previousTime
            val timesPerSecond = 1.0 / deltaTime
            Debug.println("Delta Time is $deltaTime per second $timesPerSecond \n")
            previousTime = this.seconds
            val messageTime = this.seconds - previousMessageSendDelta
            val addressesSet = opponentID != "" && yourID != ""
            statusMessage = if (!addressesSet)
                "Playing Solo"
            else
                "Playing with $opponentID "
            if (((messageTime > 1.0 && !moving) || startMoving) && addressesSet) {
                messageOut.ballsAll = balls
                messageOut.configuration = configuration.copy()
                messageOut.header.opponentID = opponentID
                messageOut.header.yourID = yourID
                messageOut.header.opponentMessage = opponentMessage
                messageOut.header.yourMessage = yourMessage
                messageOut.header.startMoving = startMoving
                messageOut.header.yourTurn = your_turn
                messageOut.header.dateStamp = program.seconds
                Debug.println("Sending ${configuration.cueAngleTrim} $your_turn")

                val inputText = communication(client, messageOut.toString())
                val goodMessage = messageIn.fromString(inputText)
                errorMessage = "All OK"

                if (goodMessage) {
                    opponentMessage = messageIn.header.opponentMessage
                    if (previousMessageDateStamp == messageIn.header.dateStamp) {
                        noMessageCount++
                        Debug.println("No new message received")
                        if (noMessageCount > 2)
                            errorMessage = "Connection Lost "
                    } else
                        noMessageCount = 0
                } else
                    errorMessage = "Not connected"
                val listenToOpponent = !your_turn && goodMessage && previousMessageDateStamp !=
                        messageIn.header.dateStamp
                if (listenToOpponent) {
                    balls = messageIn.ballsAll
                    configuration = messageIn.configuration.copy()
                    Debug.println("Receiving ${configuration.cueAngleTrim}")
                    // opponentID = messageIn.header.opponentID
                    //yourID = messageIn.header.yourID
                    opponentMessage = messageIn.header.opponentMessage
                    startMoving = messageIn.header.startMoving
                    if (switchedTurns++ > 3)
                        your_turn = !messageIn.header.yourTurn
                    previousMessageDateStamp = messageIn.header.dateStamp
                    Debug.println("Received Your turn $your_turn  starting $startMoving")
                }
                previousMessageSendDelta = this.seconds

            }
            drawTable(tableUpperLeft, TABLE_SIZE, pockets)
            drawBalls(balls, colors, stripes, tableUpperLeft)
            drawChat(your_turn, opponentMessage, fontFileName, errorMessage, statusMessage)
            if (startMoving) {
                moveCount = 0
                totalMoveTime = 0.0
                moving = true
                val segmentsPossibly = totalVelocity(startingVelocity) / 0.1
                Debug.println("*************************Possible segments $segmentsPossibly\n")
                computationSegments = 100
                previousBalls = copyBalls(balls)

                hitCue(balls, startingVelocity)
                checkMomentum(
                    previousBalls[0].velocity, previousBalls[1].velocity,
                    balls[0].velocity, balls[1].velocity
                )
            }
            statusMessage = "Not moving"
            if (moving) {
                statusMessage = "Moving"
                deltaTime = .015    // ******************************  take out maybe******
                val SEGMENT_TIME = .00015
                computationSegments = (deltaTime / SEGMENT_TIME).roundToInt()
                val computationTime = SEGMENT_TIME * computationSegments
                startMoving = false
                moveBalls(
                    balls,
                    TABLE_SIZE,
                    configuration.cushionElasticity,
                    configuration.restitution,
                    configuration.rollingResistance,
                    computationSegments,
                    configuration.displayIncrement,
                    computationTime,
                    pockets
                )

                totalMoveTime += deltaTime
                Debug.println("Delta time $deltaTime move $moveCount total $totalMoveTime")

                moveCount++

                if (stoppedMoving(balls)) {
                    moving = false
                    stopAll(balls)
                }
            } else {
                val percentage: Percentage = xyFromAngle(configuration.cueAngle + configuration.cueAngleTrim)
                startingVelocity =
                    Velocity(configuration.cueForce * percentage.x, configuration.cueForce * percentage.y)
                drawCueLine(balls, tableUpperLeft, configuration.cueForce / MAXIMUM_FORCE, percentage, TABLE_SIZE.x)
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

//fun restoreBalls(previousBalls: List<Ball>): Array<Ball> {
//    val size = previousBalls.size
//    val initBall = Ball(1, Position(0.0, 0.0), Velocity(0.0, 0.0), true)
//    val balls = Array<Ball>(size) { _ -> initBall }
//    for ((index, ball) in previousBalls.withIndex()) {
//        balls[index] = ball
//    }
//    return balls
//}

private fun Program.drawBalls(
    balls: Array<Ball>, colors: Array<ColorRGBa>, hasStripe: Array<Boolean>, tableUpperLeft: Vector2,
) {
    for (index in balls.indices) {
        val ball = balls[index]
        val ballColorIndex = ball.symbol
        drawBall(
            ball.position, BALL_RADIUS, colors[ballColorIndex],
            hasStripe[ballColorIndex], tableUpperLeft
        )
    }
}

private fun colorOfBalls() = arrayOf(
    ColorRGBa.WHITE,

    ColorRGBa.YELLOW,
    ColorRGBa.BLUE,
    ColorRGBa.RED,
    ColorRGBa.MAGENTA,
    ColorRGBa.ORANGE,
    ColorRGBa.GREEN,
    ColorRGBa.DARK_MAGENTA,

    ColorRGBa.BLACK,

    ColorRGBa.YELLOW,
    ColorRGBa.BLUE,
    ColorRGBa.RED,
    ColorRGBa.MAGENTA,
    ColorRGBa.ORANGE,
    ColorRGBa.GREEN,
    ColorRGBa.DARK_MAGENTA,
)

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

private fun Program.drawTable(tableUpperLeft: Vector2, tableSize: Vector2, pockets: Pockets) {
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

    drawPockets(tableUpperLeft, pockets)
}

fun Program.drawChat(
    your_turn: Boolean,
    opponentMessage: String,
    fontFileName: String,
    errorMessage: String,
    statusMessage: String
) {
    drawer.fill = ColorRGBa.GRAY
    drawer.stroke = ColorRGBa.GRAY
    drawer.strokeWeight = 2.0
    val box1 = Rectangle(0.0, 550.0, 200.0, 200.0)
    drawer.rectangle(box1)
    val font = loadFont(fontFileName, 24.0)
    drawer.fontMap = font
    drawer.fill = ColorRGBa.PINK
    writer {
        box = Rectangle(10.0, 560.0, 180.0, 40.0)
        newLine()
        text(yourTurnLabel(your_turn))
    }
    val font1 = loadFont(fontFileName, 18.0)
    drawer.fontMap = font1
    drawer.fill = ColorRGBa.PINK
    writer {
        box = Rectangle(10.0, 600.0, 180.0, 200.0)
        newLine()
        text(opponentMessage)
        newLine()
        text(statusMessage)
        newLine()
        text(errorMessage)
    }
}

private fun Program.drawCushion(tableUpperLeft: Vector2) {
    val WIDTH_CUSHION = 25.0
    val HALF_WIDTH_CUSHION = WIDTH_CUSHION / 2.0
    drawer.fill = ColorRGBa.LIGHT_GREEN
    drawer.strokeWeight = WIDTH_CUSHION
    drawer.stroke = ColorRGBa.LIGHT_GREEN
    drawer.lineSegment(
        tableUpperLeft.x - WIDTH_CUSHION, tableUpperLeft.y - HALF_WIDTH_CUSHION,
        tableUpperLeft.x + TABLE_SIZE.x + WIDTH_CUSHION, tableUpperLeft.y - HALF_WIDTH_CUSHION
    )
    drawer.lineSegment(
        tableUpperLeft.x - WIDTH_CUSHION, tableUpperLeft.y + TABLE_SIZE.y + HALF_WIDTH_CUSHION,
        tableUpperLeft.x + TABLE_SIZE.x + WIDTH_CUSHION, tableUpperLeft.y + TABLE_SIZE.y + HALF_WIDTH_CUSHION
    )
    drawer.lineSegment(
        tableUpperLeft.x - HALF_WIDTH_CUSHION, tableUpperLeft.y - WIDTH_CUSHION,
        tableUpperLeft.x + -HALF_WIDTH_CUSHION, tableUpperLeft.y + TABLE_SIZE.y + WIDTH_CUSHION
    )
    drawer.lineSegment(
        tableUpperLeft.x + TABLE_SIZE.x + HALF_WIDTH_CUSHION, tableUpperLeft.y - WIDTH_CUSHION,
        tableUpperLeft.x + TABLE_SIZE.x + HALF_WIDTH_CUSHION, tableUpperLeft.y + TABLE_SIZE.y + WIDTH_CUSHION
    )
}

private fun Program.drawPockets(tableUpperLeft: Vector2, pockets: Pockets) {

    drawCushion(tableUpperLeft)
    drawer.stroke = ColorRGBa.GREEN_YELLOW
    drawer.strokeWeight = 2.0
    for (i in pockets.lines.indices) {
        drawPocketLine(pockets.lines[i], tableUpperLeft)
    }
    drawer.stroke = ColorRGBa.LIGHT_BLUE
    drawer.strokeWeight = 1.0
    drawer.fill = ColorRGBa.LIGHT_BLUE
    for (i in pockets.circles.indices) {
        drawPocketCircle(pockets.circles[i], tableUpperLeft)
    }
    /*   drawPocketLine(pockets.leftSidePocketLine, tableUpperLeft)
      drawPocketLine(pockets.rightSidePocketLine, tableUpperLeft)
      drawPocketLine(pockets.footLeftCornerPocketFootLine, tableUpperLeft)
      drawPocketLine(pockets.footLeftCornerPocketSideLine, tableUpperLeft)
      drawPocketLine(pockets.headLeftCornerPocketHeadLine, tableUpperLeft)
      drawPocketLine(pockets.headLeftCornerPocketSideLine, tableUpperLeft)
      drawPocketLine(pockets.headRightCornerPocketSideLine, tableUpperLeft)
      drawPocketLine(pockets.headRightCornerPocketHeadLine, tableUpperLeft)
      drawPocketLine(pockets.footRightCornerPocketFootLine, tableUpperLeft)
      drawPocketLine(pockets.footRightCornerPocketSideLine, tableUpperLeft)

     */
}

fun yourTurnLabel(your_turn: Boolean): String {
    val yourTurnLabel = "Turn:     Yours"
    val opponentTurnLabel = "Turn: Opponents"
    return if (your_turn) {
        yourTurnLabel
    } else
        opponentTurnLabel
}


fun Program.drawPocketCircle(circle: Circle, tableUpperLeft: Vector2) {
    drawer.fill = ColorRGBa.DARK_GREEN
    drawer.circle(tableUpperLeft.x + circle.x, tableUpperLeft.y + circle.y, circle.radius)

}

private fun Program.drawPocketLine(lineSegment: LineSegment, tableUpperLeft: Vector2) {
    drawer.lineSegment(lineSegment.start + tableUpperLeft, lineSegment.end + tableUpperLeft)
}

fun Program.drawBall(
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
    } else {
        drawer.fill = ColorRGBa.WHITE
        drawer.strokeWeight = 1.0
        drawer.stroke = ColorRGBa.BLACK
        drawer.circle(positionV2, radius)
        drawer.stroke = color
        drawer.strokeWeight = radius - 2.0
        val offset = radius - 2.0
        val startPosition = Vector2(positionV2.x - offset, positionV2.y)
        val endPosition = Vector2(positionV2.x + offset, positionV2.y)
        drawer.lineSegment(startPosition, endPosition)
    }
}

fun initialBalls(): Array<Ball> {
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

fun copyBalls(inValue: Array<Ball>): Array<Ball> {
    val balls = initialBalls()
    for (i in inValue.indices) {
        val inBall = inValue[i]
        balls[i].position = Position(inBall.position.x, inBall.position.y)
        balls[i].velocity = Velocity(inBall.velocity.x, inBall.velocity.y)
        balls[i].active = inBall.active
        if (balls[i].symbol != i)
            Debug.println("Symbol on copy is wrong ")
    }
    return balls
}