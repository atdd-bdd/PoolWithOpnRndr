


    import org.openrndr.application
    import org.openrndr.color.ColorRGBa
    import org.openrndr.math.Vector2
    import org.openrndr.panel.ControlManager
    import org.openrndr.panel.elements.Range
    import org.openrndr.panel.elements.button
    import org.openrndr.panel.elements.clicked
    import org.openrndr.panel.elements.slider
    import org.openrndr.panel.layout

    // Define the initial velocity of the pool ball
    var V = 1.0

    // Define the radius of the pool ball
    val RADIUS = 10.0

    // Define the initial position of the pool ball
    var POSITION = Vector2(100.0, 100.0)

    // Define the rolling resiDstance of the pool ball
    val R = 0.3

    // Define the background color
    val BACKGROUND_COLOR = ColorRGBa.GREEN

    fun main() = application {
        configure {
            width = 500
            height = 500
        }
        program {
            // Create a control manager to manage the user interface
            var moving = false
            extend(ControlManager())  {
                // Create a horizontal layout to hold the controls
                layout {

                    // Add a label and a slider to control the initial velocity of the pool ball
                    button {
                        label = "click me"
                        // -- listen to the click event
                        height = 60
                        width = 40
                        backgroundColor = ColorRGBa.RED
                        clicked {
                            moving = true
                        }
                    }
                        slider {
                            backgroundColor = ColorRGBa.PINK
                            height = 60
                            width = 200
                        label = "velocity"
                        value = V
                        range = Range(0.0, 1.0)
                            events.valueChanged.listen { V = it.newValue }
                        }
                    }
                }


            // Draw the pool ball
            extend {
                drawer.fill = ColorRGBa.BLUE
                drawer.circle(POSITION, RADIUS)

                // Draw the three circles marking the pool ball
                drawer.fill = null
                drawer.stroke = ColorRGBa.WHITE
                drawer.circle(POSITION, RADIUS)
                drawer.circle(POSITION, RADIUS / 2)
                drawer.circle(POSITION, RADIUS / 4)
                if (moving) {
                    // Compute the new position of the pool ball
                    val newPosition = POSITION + Vector2(V, 0.0)
                    POSITION = newPosition
                }
                // Update the position of the pool ball
                  }
        }
    }

