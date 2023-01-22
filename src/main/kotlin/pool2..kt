


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
import kotlin.math.cos
import kotlin.math.sin


fun main() = application {
// Define the initial velocity of the pool ball
     var velocity = Velocity(0.0, 0.0)
// Define the radius of the pool ball
    val rADIUS = 10.0

// Define the initial position of the pool ball
    var position = Position(100.0, 100.0)
    var angle = 0.0
    var force = 0.0
// Define the rolling resiDstance of the pool ball
    var rR = 0.3

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
                    }
                }
                slider {
                    backgroundColor = ColorRGBa.PINK
                    height = 60
                    width = 200
                    label = "rolling resistance"
                    value = rR
                    range = Range(0.0, 1.0)
                    events.valueChanged.listen { rR = it.newValue }
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
                drawBall(position, rADIUS)
                if (moving) {
                    // Compute the new position of the pool ball
                    position = updatePosition(position, velocity)
                }
                else
                {
                    val start = Vector2(position.x, position.y)
                    val percentage: Percentage = xyFromAngle2(angle)
                    val xDiff = force * percentage.x * 100.0
                    val yDiff = force * percentage.y * 100.0
                    val end = Vector2(start.x + xDiff, start.y + yDiff)
                    velocity = Velocity(force * percentage.x, force * percentage.y)
                    drawer.lineSegment(start, end)
                }
                // Update the position of the pool ball
            }
        }
    }


}


private fun Program.drawBall(position: Position, radius: Double) {
    val positionV2 = position.toVector2()
    drawer.circle(positionV2, radius)

    // Draw the three circles marking the pool ball
    drawer.fill = ColorRGBa.WHEAT
    drawer.stroke = ColorRGBa.WHITE
    drawer.circle(positionV2, radius)
    drawer.circle(positionV2, radius / 2)
    drawer.circle(positionV2, radius / 4)
}

private fun updatePosition(start : Position, speed: Velocity): Position{
    return Position (start.x + speed.x, start.y + speed.y)
}

private fun xyFromAngle2( angle : Double) : Percentage {
    val result = Percentage(0.0, 0.0)
    val radians = angle * PI / 180.0
    result.x = sin(radians)
    result.y = cos(radians)
    return result
}

