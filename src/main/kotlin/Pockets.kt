import org.openrndr.math.Vector2

val SIDE_POCKET_LENGTH = 50.0
val CORNER_POCKET_LENGTH = 50.0

class HeadLeftCornerPocket {
    val sideLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, CORNER_POCKET_LENGTH))
    val headLine = LineSegment(Vector2(0.0, 0.0), Vector2(CORNER_POCKET_LENGTH, 0.0))
    val sideLineY = CORNER_POCKET_LENGTH
    val headLineX = CORNER_POCKET_LENGTH
    fun headedTowardHead(position: Position, velocity: Velocity): Boolean {
        return (velocity.x < 0 && position.x < headLineX - BALL_RADIUS)
    }

    fun headedTowardSide(position: Position, velocity: Velocity): Boolean {
        return (velocity.y < 0 && position.y < sideLineY - BALL_RADIUS)
    }
}
class HeadRightCornerPocket {
    val sideLine = LineSegment(Vector2(0.0, TABLE_SIZE.y), Vector2(CORNER_POCKET_LENGTH, TABLE_SIZE.y))
    val headLine = LineSegment(Vector2(0.0, TABLE_SIZE.y), Vector2(0.0, TABLE_SIZE.y -CORNER_POCKET_LENGTH,))
    val sideLineY = CORNER_POCKET_LENGTH
    val headLineX = CORNER_POCKET_LENGTH
    fun headedTowardHead(position: Position, velocity: Velocity): Boolean {
        return (velocity.x < 0 && position.x > TABLE_SIZE.x - headLineX + BALL_RADIUS)
    }

    fun headedTowardSide(position: Position, velocity: Velocity): Boolean {
        return (velocity.y > 0 && position.y > sideLineY - BALL_RADIUS)
    }
}
//   Complete the two below
class FootLeftCornerPocket {
    val sideLine = LineSegment(Vector2(TABLE_SIZE.x, 0.0), Vector2(TABLE_SIZE.x- CORNER_POCKET_LENGTH, 0.0))
    val footLine = LineSegment(Vector2(TABLE_SIZE.x, 0.0), Vector2(TABLE_SIZE.x, CORNER_POCKET_LENGTH))
    val sideLineY = CORNER_POCKET_LENGTH
    val footLineX = CORNER_POCKET_LENGTH
    fun headedTowardFoot(position: Position, velocity: Velocity): Boolean {
        return (velocity.x > 0 && position.x > TABLE_SIZE.x - footLineX + BALL_RADIUS)
    }

    fun headedTowardSide(position: Position, velocity: Velocity): Boolean {
        return (velocity.y < 0 && position.y < sideLineY - BALL_RADIUS)
    }
}
class FoodRightCornerPocket {
    val sideLine = LineSegment(Vector2(0.0, TABLE_SIZE.y), Vector2(CORNER_POCKET_LENGTH, TABLE_SIZE.y))
    val headLine = LineSegment(Vector2(0.0, TABLE_SIZE.y), Vector2(0.0, TABLE_SIZE.y -CORNER_POCKET_LENGTH,))
    val sideLineY = CORNER_POCKET_LENGTH
    val headLineX = CORNER_POCKET_LENGTH
    fun headedTowardFoot(position: Position, velocity: Velocity): Boolean {
        return (velocity.x < 0 && position.x > TABLE_SIZE.x - headLineX + BALL_RADIUS)
    }

    fun headedTowardSide(position: Position, velocity: Velocity): Boolean {
        return (velocity.y > 0 && position.y > sideLineY - BALL_RADIUS)
    }
}

class Pockets {
    val headLeftCornerPocket = HeadLeftCornerPocket()
    val headRightCornerPocket = HeadRightCornerPocket()
    private fun reboundSideRail(velocity: Velocity, cushionElasticity: Double): Velocity {
        return Velocity(velocity.x * cushionElasticity, -velocity.y * cushionElasticity)

    }

    private fun reboundHeadFootRail(velocity: Velocity, cushionElasticity: Double): Velocity {
        return Velocity(-velocity.x * cushionElasticity, velocity.y * cushionElasticity)
    }

    fun checkCushion(
        position: Position, velocity: Velocity, tableSize: Vector2,
        cushionElasticity: Double,
    ): Velocity {
        var newVelocity = velocity
        if (atLeftSideRail(position, velocity))
            if (!headLeftCornerPocket.headedTowardSide(position, velocity))
                newVelocity = reboundSideRail(velocity, cushionElasticity)
        if (atHeadRail(position, velocity))
            if (!headLeftCornerPocket.headedTowardHead(position,velocity ) &&
                !headRightCornerPocket.headedTowardHead(position, velocity))
                newVelocity = reboundHeadFootRail(velocity, cushionElasticity)
        return newVelocity
    }


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


    var leftSidePocket = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var rightSidePocket = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var footRightCornerPocketFootLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var footRightCornerPocketSideLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var headRightCornerPocketHeadLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var headRightCornerPocketSideLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var footLeftCornetPocketFootLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var footLeftConerPocketSideLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var headLeftCornerPocketHeadLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var headLeftCornerPocketSideLine = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))

    init {
        val centerX = TABLE_SIZE.x / 2
        leftSidePocket = LineSegment(
            Vector2(centerX - SIDE_POCKET_LENGTH / 2, 0.0), Vector2(centerX + SIDE_POCKET_LENGTH / 2, .0)
        )
        rightSidePocket = LineSegment(
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

        footLeftCornetPocketFootLine = LineSegment(
            Vector2(TABLE_SIZE.x, TABLE_SIZE.y - CORNER_POCKET_LENGTH), Vector2(TABLE_SIZE.x, TABLE_SIZE.y)
        )

        footLeftConerPocketSideLine = LineSegment(
            Vector2(TABLE_SIZE.x - CORNER_POCKET_LENGTH, 0.0), Vector2(TABLE_SIZE.x, 0.0)
        )

        footRightCornerPocketFootLine =
            LineSegment(Vector2(TABLE_SIZE.x - CORNER_POCKET_LENGTH, TABLE_SIZE.y), Vector2(TABLE_SIZE.x, TABLE_SIZE.y))
        footRightCornerPocketSideLine =
            LineSegment(Vector2(TABLE_SIZE.x, 0.0), Vector2(TABLE_SIZE.x, CORNER_POCKET_LENGTH))


    }

}