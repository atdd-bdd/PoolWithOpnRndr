import org.openrndr.MouseEvent
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.getDefaultPathForContext
import org.openrndr.dialogs.openFileDialog
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.dialogs.setDefaultPathForContext
import org.openrndr.extra.color.presets.*
import org.openrndr.math.Vector2
import org.openrndr.panel.ControlManager
import org.openrndr.panel.elements.*
import org.openrndr.panel.layout
import org.openrndr.panel.style.*

import org.openrndr.panel.styleSheet
import java.io.File

const val BALL_RADIUS = 10.00
const val BALL_DIAMETER_SQUARED = (BALL_RADIUS * 2) * (BALL_RADIUS * 2)
val TABLE_SIZE = Vector2(900.0, 450.0)

private const val MOUSE_NOT_ON_BALL = -1

var balls: Array<Ball> = initialBalls()

fun main() = application {
    val MINIMUM_RESISTANCE = 0.0
    val MAXIMUM_RESISTANCE = 0.05
    val MAXIMUM_CUSHION_ELASTICITY = 1.0
    val MAXIMUM_RESTITUTION = 1.0
    val MAXIMUM_FORCE = 5000.0

    var cueAngle = 0.0
    var cueForce = 0.0
    var startingVelocity = Velocity(0.0, 0.0)

    var rollingResistance = 0.01
    var restitution = 0.95
    var cushionElasticity = 0.70

    var displayIncrement = 100
    var previousBalls: List<Ball> = balls.map { it.copy() }

    val colors = colorOfBalls()
    val stripes = stripOnBalls()

    val tableUpperLeft = Vector2(250.0, 210.0)

    var computationSegments = 100
    var yourID =""
    var opponentID =""
    configure {
        width = 1200
        height = 700
        title = "Pool"
    }
    program {

        var previousTime = program.seconds
        var moving = false
        var ballMoving = MOUSE_NOT_ON_BALL
        var startPosition = Position(0.0, 0.0)
        val pockets = Pockets()
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
                styleSheet(has class_ "side-bar") {
                    this.height = 100.percent
                    this.width = 200.px
                    this.display = Display.FLEX
                    this.flexDirection = FlexDirection.Column
                    this.paddingLeft = 10.px
                    this.paddingRight = 10.px
                    this.background = Color.RGBa(ColorRGBa.LIGHT_GREEN)
                }
                div("horizontal") {
                    button {
                         label = "Cue Stroke"
                        clicked {
                            moving = true
                            val segmentsPossibly = totalVelocity(startingVelocity) / 0.1
                            print("*************************Possible segments $segmentsPossibly\n")
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
                        label = "Rack 'em"
                        clicked {
                            rackBalls(balls)
                        }
                    }
                    slider {
                        style = styleSheet {
                            width = 100.px
                        }
                        label = "Display Speed"
                        value = displayIncrement.toDouble()
                        range = Range(1.0, computationSegments.toDouble())
                        events.valueChanged.listen { displayIncrement = (it.newValue + .01).toInt() }
                    }
                }//div
                div("horizontal") {
                    slider {
                        label = "Force"
                        value = cueForce
                        range = Range(0.0, MAXIMUM_FORCE)
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
                div("side-bar") {
                    button {
                        label = "Load Game"
                        clicked {
                            openFileDialog(supportedExtensions = listOf("pool"), contextID = "pool"){loadGame(it)}
                            print("Load file")
                        }
                    }
                    button {
                        label = "Load Config"
                        clicked {
                            openFileDialog(supportedExtensions = listOf("poolc"), contextID = "pool") {
                                print(it)
                            }
                            print("Load file")
                        }
                    }

                    button {
                        label = "Save"
                        clicked {
                            val defaultPath = getDefaultPathForContext(contextID = "pool")
                            val defaultSaveFolder = "pool"
                            if (defaultPath == null) {
                                val local = File(".")
                                val parameters = File(local, defaultSaveFolder)
                                if (parameters.exists() && parameters.isDirectory) {
                                    setDefaultPathForContext(contextID = "pool", file = parameters)
                                } else {
                                    if (parameters.mkdirs()) {
                                        setDefaultPathForContext(
                                            contextID = "pool",
                                            file = parameters
                                        )
                                    } else {
                                        print ("Could not create directory ${parameters.absolutePath}" )
                                    }
                                }
                            }

                            saveFileDialog(
                                suggestedFilename = "game1.pool",
                                contextID = "pool",
                                supportedExtensions = listOf("pool")){
                                    saveGame(it)
                            }
                        }
                    }

                    slider {
                        style = styleSheet {
                            width = 100.px
                        }
                        label = "Restitution"
                        value = restitution
                        range = Range(0.0, MAXIMUM_RESTITUTION)
                        events.valueChanged.listen { restitution = it.newValue }
                    }
                    slider {
                        style = styleSheet {
                            width = 100.px
                        }
                        label = "Cushion Elasticity"
                        value = cushionElasticity
                        range = Range(0.0, MAXIMUM_CUSHION_ELASTICITY)
                        events.valueChanged.listen { cushionElasticity = it.newValue }
                    }
                    slider {
                        style = styleSheet {
                            width = 100.px
                        }
                        label = "Rolling Resistance"
                        value = rollingResistance
                        range = Range(MINIMUM_RESISTANCE, MAXIMUM_RESISTANCE)
                        events.valueChanged.listen { rollingResistance = it.newValue }
                    }
                    textfield() {
                        style = styleSheet {
                            width = 100.px
                        }
                        label = "Your ID"
                        value = yourID
                        events.valueChanged.listen { yourID = it.newValue
                          }
                    }
                    textfield() {
                        style = styleSheet {
                            width = 100.px
                        }
                        label = "Opponent ID"
                        value = opponentID
                        width = 15
                        events.valueChanged.listen {opponentID = it.newValue
                        }
                    }

                }
            }

            extend {
                val deltaTime = this.seconds - previousTime
               // val timesPerSecond = 1.0 / deltaTime
               // print("Delta Time is $deltaTime per second $timesPerSecond \n" )
                previousTime =this.seconds

                drawTable(tableUpperLeft, TABLE_SIZE, pockets)
                drawBalls(balls, colors, stripes  ,tableUpperLeft)
                if (moving) {
                    moveBalls(
                        balls,
                        TABLE_SIZE,
                        cushionElasticity,
                        restitution,
                        rollingResistance,
                        computationSegments,
                        displayIncrement,
                        deltaTime,
                        pockets
                    )
                    if (stoppedMoving(balls)) {
                        moving = false
                        stopAll(balls)
                    }
                } else {
                    val percentage: Percentage = xyFromAngle(cueAngle)
                    startingVelocity = Velocity(cueForce * percentage.x, cueForce * percentage.y)
                    drawCueLine(balls, tableUpperLeft, cueForce / MAXIMUM_FORCE, percentage, TABLE_SIZE.x)
                }
            }
        }
    }
}

fun Program.loadGame(it: File){
    print("Loading game from $it")
    val text =  it.readText()
     balls= readGameString(text, balls)
    return
}

fun readGameString(text: String, balls: Array<Ball>): Array<Ball> {
    val ballsOut = balls
    val lines = text.split("\n")
    var firstLine = true
    var index = 0
    for (line in lines) {
        if (firstLine) {
            print(line)
            firstLine = false
            continue
        }
        print(line + "\n")
        val fields = line.split(',')
        print(fields + "\n")
        try {
            val ball = Ball(
                fields[0].toInt(), Position(
                    fields[1].toDouble(),
                    fields[2].toDouble()
                ), Velocity(fields[3].toDouble(), fields[4].toDouble()),
                fields[5].toBoolean())
            ballsOut[index] = ball
                        index++

        }
        catch(e: NumberFormatException){
            print("Number format exception")
            }
        val size = ballsOut.size
        if (index >= size)
            break
    }
    return ballsOut
}

fun Program.saveGame(it: File) {
    print("Storing game into $it")
    val text = getGameString(balls)
    it.writeText(text)
}

fun getGameString(balls: Array<Ball>): String {
    val result = StringBuilder()
    result.append("Symbol,PositionX,PositionY,VelocityX,VelocityY,Active\n")
    for (i in balls.indices){
        val ball =  balls[i]
        val line = String.format("${ball.symbol},${ball.position.x},${ball.position.y}," +
                "${ball.velocity.x},${ball.velocity.y},${ball.active}\n")
        result.append(line)
    }
    return result.toString()
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

private fun Program.drawCushion(tableUpperLeft: Vector2) {
    val WIDTH_CUSHION = 25.0
    val HALF_WIDTH_CUSHION = WIDTH_CUSHION / 2.0
    drawer.fill = ColorRGBa.LIGHT_GREEN
    drawer.strokeWeight =WIDTH_CUSHION
    drawer.stroke = ColorRGBa.LIGHT_GREEN
    drawer.lineSegment(tableUpperLeft.x - WIDTH_CUSHION, tableUpperLeft.y -HALF_WIDTH_CUSHION,
        tableUpperLeft.x + TABLE_SIZE.x + WIDTH_CUSHION, tableUpperLeft.y - HALF_WIDTH_CUSHION )
    drawer.lineSegment(tableUpperLeft.x - WIDTH_CUSHION, tableUpperLeft.y + TABLE_SIZE.y + HALF_WIDTH_CUSHION,
        tableUpperLeft.x + TABLE_SIZE.x + WIDTH_CUSHION, tableUpperLeft.y + TABLE_SIZE.y + HALF_WIDTH_CUSHION )
    drawer.lineSegment(tableUpperLeft.x - HALF_WIDTH_CUSHION, tableUpperLeft.y -WIDTH_CUSHION,
        tableUpperLeft.x + - HALF_WIDTH_CUSHION, tableUpperLeft.y + TABLE_SIZE.y + WIDTH_CUSHION)
    drawer.lineSegment(tableUpperLeft.x + TABLE_SIZE.x + HALF_WIDTH_CUSHION, tableUpperLeft.y -WIDTH_CUSHION,
        tableUpperLeft.x + TABLE_SIZE.x + HALF_WIDTH_CUSHION, tableUpperLeft.y + TABLE_SIZE.y + WIDTH_CUSHION )
}

private fun Program.drawPockets(tableUpperLeft: Vector2, pockets: Pockets) {

    drawCushion(tableUpperLeft)
    drawer.stroke = ColorRGBa.GREEN_YELLOW
    drawer.strokeWeight = 2.0
    for (i in pockets.lines.indices){
        drawPocketLine(pockets.lines[i], tableUpperLeft)
    }
    drawer.stroke = ColorRGBa.LIGHT_BLUE
    drawer.strokeWeight = 1.0
    drawer.fill = ColorRGBa.LIGHT_BLUE
    for (i in pockets.circles.indices){
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

fun Program.drawPocketCircle(circle: Circle, tableUpperLeft: Vector2) {
    drawer.fill = ColorRGBa.DARK_GREEN
   drawer.circle(tableUpperLeft.x + circle.x, tableUpperLeft.y + circle.y, circle.radius)

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
