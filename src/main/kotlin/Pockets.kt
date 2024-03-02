import org.openrndr.math.Vector2

const val SIDE_POCKET_LENGTH = 6 * BALL_RADIUS
const val CORNER_POCKET_LENGTH = 3 * BALL_RADIUS
const val CIRCLE_OFFSET = BALL_RADIUS / 2

class Circle(val x: Double, val y: Double, val radius: Double)
class HeadLeftCornerPocket {
    val sideLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, CORNER_POCKET_LENGTH))
    val headLine = LineSegment(Vector2(0.0, 0.0), Vector2(CORNER_POCKET_LENGTH, 0.0))
    val pocketCircle = Circle(0.0 - CIRCLE_OFFSET, 0.0 - CIRCLE_OFFSET, BALL_RADIUS * 2)
    fun headedTowardHead(position: Position, velocity: Velocity): Boolean {
        return (velocity.x < 0 && position.y < CORNER_POCKET_LENGTH - BALL_RADIUS)
    }

    fun headedTowardSide(position: Position, velocity: Velocity): Boolean {
        return (velocity.y < 0 && position.x < CORNER_POCKET_LENGTH - BALL_RADIUS)
    }
}

class HeadRightCornerPocket {
    val sideLine = LineSegment(Vector2(0.0, TABLE_SIZE.y), Vector2(CORNER_POCKET_LENGTH, TABLE_SIZE.y))
    val headLine = LineSegment(Vector2(0.0, TABLE_SIZE.y - CORNER_POCKET_LENGTH), Vector2(0.0, TABLE_SIZE.y))
    val pocketCircle = Circle(0.0 - CIRCLE_OFFSET, TABLE_SIZE.y + CIRCLE_OFFSET, BALL_RADIUS * 2)

    fun headedTowardHead(position: Position, velocity: Velocity): Boolean {
        return (velocity.x < 0 && position.y > TABLE_SIZE.y - CORNER_POCKET_LENGTH + BALL_RADIUS)
    }

    fun headedTowardSide(position: Position, velocity: Velocity): Boolean {
        return (velocity.y > 0 && position.x < CORNER_POCKET_LENGTH - BALL_RADIUS)
    }
}


class FootLeftCornerPocket {
    val sideLine = LineSegment(Vector2(TABLE_SIZE.x - CORNER_POCKET_LENGTH, 0.0), Vector2(TABLE_SIZE.x, 0.0))
    val footLine = LineSegment(Vector2(TABLE_SIZE.x, 0.0), Vector2(TABLE_SIZE.x, CORNER_POCKET_LENGTH))
    val pocketCircle = Circle(TABLE_SIZE.x + CIRCLE_OFFSET, 0.0 - CIRCLE_OFFSET, BALL_RADIUS * 2)

    fun headedTowardFoot(position: Position, velocity: Velocity): Boolean {
        return (velocity.x > 0 && position.y < CORNER_POCKET_LENGTH - BALL_RADIUS)
    }

    fun headedTowardSide(position: Position, velocity: Velocity): Boolean {
        return (velocity.y < 0 && position.x > TABLE_SIZE.x - CORNER_POCKET_LENGTH + BALL_RADIUS)
    }
}

class FootRightCornerPocket {
    val sideLine =
        LineSegment(Vector2(TABLE_SIZE.x - CORNER_POCKET_LENGTH, TABLE_SIZE.y), Vector2(TABLE_SIZE.x, TABLE_SIZE.y))
    val footLine =
        LineSegment(Vector2(TABLE_SIZE.x, TABLE_SIZE.y - CORNER_POCKET_LENGTH), Vector2(TABLE_SIZE.x, TABLE_SIZE.y))
    val pocketCircle = Circle(TABLE_SIZE.x + CIRCLE_OFFSET, TABLE_SIZE.y + CIRCLE_OFFSET, BALL_RADIUS * 2)
    fun headedTowardFoot(position: Position, velocity: Velocity): Boolean {
        return (velocity.x > 0 && position.y > TABLE_SIZE.y - CORNER_POCKET_LENGTH + BALL_RADIUS)
    }

    fun headedTowardSide(position: Position, velocity: Velocity): Boolean {
        return (velocity.y > 0 && position.x > TABLE_SIZE.x - CORNER_POCKET_LENGTH + BALL_RADIUS)
    }
}

class LeftSidePocket {
    val center = TABLE_SIZE.x / 2
    val half_width = SIDE_POCKET_LENGTH / 2
    val sideLine = LineSegment(
        Vector2(center - half_width, 0.0),
        Vector2(center + half_width, 0.0)
    )
    val pocketCircle = Circle(center, 0.0 - CIRCLE_OFFSET * 2, BALL_RADIUS * 2)

    fun headedTowardSide(position: Position, velocity: Velocity): Boolean {
        return (velocity.y < 0
                && (position.x > center - half_width + BALL_RADIUS)
                && (position.x < center + half_width - BALL_RADIUS))
    }
}

class RightSidePocket {
    val center = TABLE_SIZE.x / 2
    val half_width = SIDE_POCKET_LENGTH / 2
    val sideLine = LineSegment(
        Vector2(center - half_width, TABLE_SIZE.y),
        Vector2(center + half_width, TABLE_SIZE.y)
    )
    val pocketCircle = Circle(center, TABLE_SIZE.y + CIRCLE_OFFSET * 2, BALL_RADIUS * 2)
    fun headedTowardSide(position: Position, velocity: Velocity): Boolean {
        return (velocity.y > 0
                && (position.x > center - half_width + BALL_RADIUS)
                && (position.x < center + half_width - BALL_RADIUS))
    }
}

class Pockets {
    val headLeftCornerPocket = HeadLeftCornerPocket()
    val headRightCornerPocket = HeadRightCornerPocket()
    val footLeftCornerPocket = FootLeftCornerPocket()
    val footRightCornerPocket = FootRightCornerPocket()
    val rightSidePocket = RightSidePocket()
    val leftSidePocket = LeftSidePocket()
    val lines: Array<LineSegment> = arrayOf(
        headLeftCornerPocket.sideLine, headLeftCornerPocket.headLine,
        headRightCornerPocket.sideLine, headRightCornerPocket.headLine,
        footLeftCornerPocket.sideLine, footLeftCornerPocket.footLine,
        footRightCornerPocket.sideLine, footRightCornerPocket.footLine,
        rightSidePocket.sideLine, leftSidePocket.sideLine
    )
    val circles: Array<Circle> = arrayOf(
        headLeftCornerPocket.pocketCircle, headRightCornerPocket.pocketCircle,
        footLeftCornerPocket.pocketCircle, footRightCornerPocket.pocketCircle,
        rightSidePocket.pocketCircle, leftSidePocket.pocketCircle
    )

    private fun reboundSideRail(velocity: Velocity, cushionElasticity: Double): Velocity {
        return Velocity(velocity.x * cushionElasticity, -velocity.y * cushionElasticity)

    }

    private fun reboundHeadFootRail(velocity: Velocity, cushionElasticity: Double): Velocity {
        return Velocity(-velocity.x * cushionElasticity, velocity.y * cushionElasticity)
    }

    fun checkCushion(
// this shows four ways of writing an if
        position: Position, velocity: Velocity, tableSize: Vector2,
        cushionElasticity: Double,
    ): Velocity {
        if (atLeftSideRail(position, velocity)) {
//            println("Left Side Rail $position $velocity")
            if (headLeftCornerPocket.headedTowardSide(position, velocity) ||
                footLeftCornerPocket.headedTowardSide(position, velocity) ||
                leftSidePocket.headedTowardSide(position, velocity)
            )
                return velocity
            else
                return reboundSideRail(velocity, cushionElasticity)
        }
        if (atRightSideRail(position, velocity)) {
//            println("Right Side Rail $position $velocity")
            if (headRightCornerPocket.headedTowardSide(position, velocity)
                || footRightCornerPocket.headedTowardSide(position, velocity)
                || rightSidePocket.headedTowardSide(position, velocity)
            ) return velocity
            else return reboundSideRail(velocity, cushionElasticity)
        }
// kotlin style guideline for multi-line predicate
        if (atHeadRail(position, velocity)) {
//            println("At Head Rail $position $velocity")
            if (headLeftCornerPocket.headedTowardHead(position, velocity) ||
                headRightCornerPocket.headedTowardHead(position, velocity)
            ) {
                return velocity
            } else
                return reboundHeadFootRail(velocity, cushionElasticity)
        }
        if (atFootRail(position, velocity)) {
//            println("At Foot Rail $position $velocity")
            if (footLeftCornerPocket.headedTowardFoot(position, velocity) ||
                footRightCornerPocket.headedTowardFoot(position, velocity)
            ) {
                return velocity
            } else
                return reboundHeadFootRail(velocity, cushionElasticity)
        }
// Alternative way - using a method to hold predicate, and the return if
//        if (atFootRail(position, velocity)) {
//            println("At foot Rail")
//            return if (headingFootPocket(position, velocity)) velocity
//            else reboundHeadFootRail(velocity, cushionElasticity)
//        }
//        return velocity
    return velocity
    }

//    private fun headingFootPocket(position: Position, velocity: Velocity) =
//        footLeftCornerPocket.headedTowardFoot(position, velocity) ||
//                footRightCornerPocket.headedTowardFoot(position, velocity)


    private fun atLeftSideRail(position: Position, velocity: Velocity): Boolean {
        return ((position.y <= 0.0 + BALL_RADIUS) && velocity.y < 0)
    }

    private fun atRightSideRail(position: Position, velocity: Velocity): Boolean {
        return ((position.y >= TABLE_SIZE.y - BALL_RADIUS) && velocity.y > 0)
    }

    private fun atHeadRail(position: Position, velocity: Velocity): Boolean {
        return ((position.x <= 0.0 + BALL_RADIUS) && velocity.x < 0)
    }

    private fun atFootRail(position: Position, velocity: Velocity): Boolean {
        return ((position.x >= TABLE_SIZE.x - BALL_RADIUS) && velocity.x > 0)
    }


    var leftSidePocketLine: LineSegment
    var rightSidePocketLine: LineSegment
    var footRightCornerPocketFootLine: LineSegment
    var footRightCornerPocketSideLine: LineSegment
    var headRightCornerPocketHeadLine: LineSegment
    var headRightCornerPocketSideLine: LineSegment
    var footLeftCornerPocketFootLine: LineSegment
    var footLeftCornerPocketSideLine: LineSegment
    var headLeftCornerPocketHeadLine: LineSegment
    var headLeftCornerPocketSideLine: LineSegment

    init {
        val centerX = TABLE_SIZE.x / 2
        leftSidePocketLine = LineSegment(
            Vector2(centerX - SIDE_POCKET_LENGTH / 2, 0.0), Vector2(centerX + SIDE_POCKET_LENGTH / 2, .0)
        )
        rightSidePocketLine = LineSegment(
            Vector2(centerX - SIDE_POCKET_LENGTH / 2, TABLE_SIZE.y),
            Vector2(centerX + SIDE_POCKET_LENGTH / 2, TABLE_SIZE.y)
        )

        headLeftCornerPocketSideLine = LineSegment(Vector2(0.0, 0.0), Vector2(CORNER_POCKET_LENGTH, 0.0))
        headLeftCornerPocketHeadLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, CORNER_POCKET_LENGTH))

        headRightCornerPocketSideLine =
            LineSegment(Vector2(0.0, TABLE_SIZE.y), Vector2(CORNER_POCKET_LENGTH, TABLE_SIZE.y))
        headRightCornerPocketHeadLine = LineSegment(
            Vector2(0.0, TABLE_SIZE.y - CORNER_POCKET_LENGTH), Vector2(0.0, TABLE_SIZE.y)
        )

        footLeftCornerPocketFootLine = LineSegment(
            Vector2(TABLE_SIZE.x, TABLE_SIZE.y - CORNER_POCKET_LENGTH), Vector2(TABLE_SIZE.x, TABLE_SIZE.y)
        )

        footLeftCornerPocketSideLine = LineSegment(
            Vector2(TABLE_SIZE.x - CORNER_POCKET_LENGTH, 0.0), Vector2(TABLE_SIZE.x, 0.0)
        )

        footRightCornerPocketFootLine =
            LineSegment(
                Vector2(TABLE_SIZE.x, TABLE_SIZE.y - CORNER_POCKET_LENGTH),
                Vector2(TABLE_SIZE.x, CORNER_POCKET_LENGTH)
            )
        footRightCornerPocketSideLine =
            LineSegment(Vector2(TABLE_SIZE.x - CORNER_POCKET_LENGTH, TABLE_SIZE.y), Vector2(TABLE_SIZE.x, TABLE_SIZE.y))


    }

}