import org.openrndr.math.Vector2

class Pockets {
    var topSidePocket = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var bottomSidePocket  = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var topRightPocketTop = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var topRightPocketRight = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var topLeftPocketTop = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var topLeftPocketLeft = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var bottomLeftPocketBottom = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var bottomLeftPocketLeft = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var bottomRightPocketRight = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))
    var bottomRightPocketBottom = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, 0.0))

    private val sIDE_POCKET_LENGTH = 50.0
    private val cORNER_POCKET_LENGTH = 50.0
    init{
        val centerX= TABLE_SIZE.x / 2

        topSidePocket = LineSegment(Vector2(centerX - sIDE_POCKET_LENGTH / 2, 0.0),
            Vector2(centerX + sIDE_POCKET_LENGTH/ 2, .0))
        bottomSidePocket = LineSegment(Vector2(centerX - sIDE_POCKET_LENGTH / 2, TABLE_SIZE.y),
            Vector2(centerX + sIDE_POCKET_LENGTH/ 2, TABLE_SIZE.y))

        topLeftPocketLeft = LineSegment(Vector2(0.0, 0.0), Vector2(cORNER_POCKET_LENGTH, 0.0))
        topLeftPocketTop = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, cORNER_POCKET_LENGTH))

        topRightPocketRight = LineSegment(Vector2(TABLE_SIZE.x, 0.0), Vector2(TABLE_SIZE.x, cORNER_POCKET_LENGTH))
         bottomLeftPocketLeft = LineSegment(Vector2(0.0, TABLE_SIZE.y - cORNER_POCKET_LENGTH),
             Vector2(0.0, TABLE_SIZE.y))
        bottomLeftPocketBottom = LineSegment(Vector2(0.0, TABLE_SIZE.y), Vector2(cORNER_POCKET_LENGTH, TABLE_SIZE.y))
        bottomRightPocketRight = LineSegment(Vector2(TABLE_SIZE.x , TABLE_SIZE.y - cORNER_POCKET_LENGTH),
            Vector2(TABLE_SIZE.x, TABLE_SIZE.y))

                topRightPocketTop = LineSegment(
                Vector2(TABLE_SIZE.x - cORNER_POCKET_LENGTH, 0.0),
            Vector2(TABLE_SIZE.x, 0.0)
        )

        bottomRightPocketBottom = LineSegment(Vector2(TABLE_SIZE.x - cORNER_POCKET_LENGTH, TABLE_SIZE.y), Vector2(TABLE_SIZE.x, TABLE_SIZE.y))
    }

}