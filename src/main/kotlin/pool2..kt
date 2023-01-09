


import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.color.presets.WHEAT
import org.openrndr.math.Vector2
import org.openrndr.panel.ControlManager
import org.openrndr.panel.elements.Range
import org.openrndr.panel.elements.button
import org.openrndr.panel.elements.clicked
import org.openrndr.panel.elements.slider
import org.openrndr.panel.layout
import kotlin.math.PI


fun main() = application {
// Define the initial velocity of the pool ball
    var V = 1.0
    var velocity = Velocity(0.0, 0.0)
// Define the radius of the pool ball
    val RADIUS = 10.0

// Define the initial position of the pool ball
    var POSITION = Position(100.0, 100.0)
    var angle = 0.0
    var force = 0.0
// Define the rolling resiDstance of the pool ball
    var R = 0.3

// Define the background color
    val BACKGROUND_COLOR = ColorRGBa.GREEN
    configure {
        width = 1000
        height = 700
    }
    program {
        // Create a control manager to manage the user interface
        var moving = false;
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
                    }
                }
                slider {
                    backgroundColor = ColorRGBa.PINK
                    height = 60
                    width = 200
                    label = "rolling resistance"
                    value = R
                    range = Range(0.0, 1.0)
                    events.valueChanged.listen { R = it.newValue }
                }
                slider {
                    backgroundColor = ColorRGBa.BLUE
                    height = 60
                    width = 200
                    label = "force"
                    value = force
                    range = Range(0.0, 1.0)
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


            // Draw the pool ball
            extend {
                drawer.fill = ColorRGBa.BLUE
                drawBall(POSITION, RADIUS)
                if (moving) {
                    // Compute the new position of the pool ball
                    POSITION = updatePosition(POSITION, velocity)
                }
                else
                {
                    val start: Vector2 = Vector2(POSITION.x, POSITION.y)
                    val percentage: Percentage = xyFromAngle(angle)
                    val x_diff = force * percentage.x * 100.0
                    val y_diff = force * percentage.y * 100.0
                    var end: Vector2 = Vector2(start.x + x_diff, start.y + y_diff)
                    velocity.x = force * percentage.x
                    velocity.y = force * percentage.y
                    drawer.lineSegment(start, end)
                }
                // Update the position of the pool ball
            }
        }
    }


}

private fun Program.drawBall(POSITION: Position, RADIUS: Double) {
    val positionV2 = POSITION.toVector2()
    drawer.circle(positionV2, RADIUS)

    // Draw the three circles marking the pool ball
    drawer.fill = ColorRGBa.WHEAT
    drawer.stroke = ColorRGBa.WHITE
    drawer.circle(positionV2, RADIUS)
    drawer.circle(positionV2, RADIUS / 2)
    drawer.circle(positionV2, RADIUS / 4)
}

fun updatePosition(start : Position, speed: Velocity): Position{
    var end: Position = Position (0.0,0.0)
    end.x = start.x + speed.x;
    end.y = start.y + speed.y;
    return end;
}

fun xyFromAngle( angle : Double) : Percentage {
    var  result = Percentage(0.0, 0.0)
    val radians = angle * PI / 180.0
    result.x = Math.sin(radians)
    result.y = Math.cos(radians)
    return result
}

