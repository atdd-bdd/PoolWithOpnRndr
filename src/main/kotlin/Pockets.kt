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
    @Suppress("PropertyName", "PropertyName")
    private val SIDE_POCKET_LENGTH = 50.0
    private val CORNER_POCKET_LENGTH = 50.0
    init{
        val centerX= tableSize.x / 2

        topSidePocket = LineSegment(Vector2(centerX - SIDE_POCKET_LENGTH / 2, 0.0),
            Vector2(centerX + SIDE_POCKET_LENGTH/ 2, .0))
        bottomSidePocket = LineSegment(Vector2(centerX - SIDE_POCKET_LENGTH / 2, tableSize.y),
            Vector2(centerX + SIDE_POCKET_LENGTH/ 2, tableSize.y))

        topLeftPocketLeft = LineSegment(Vector2(0.0, 0.0), Vector2(CORNER_POCKET_LENGTH, 0.0))
        topLeftPocketTop = LineSegment(Vector2(0.0, 0.0), Vector2(0.0, CORNER_POCKET_LENGTH))

        topRightPocketRight = LineSegment(Vector2(tableSize.x, 0.0), Vector2(tableSize.x, CORNER_POCKET_LENGTH))
         bottomLeftPocketLeft = LineSegment(Vector2(0.0, tableSize.y - CORNER_POCKET_LENGTH),
             Vector2(0.0, tableSize.y))
        bottomLeftPocketBottom = LineSegment(Vector2(0.0, tableSize.y), Vector2(CORNER_POCKET_LENGTH, tableSize.y))
        bottomRightPocketRight = LineSegment(Vector2(tableSize.x , tableSize.y - CORNER_POCKET_LENGTH),
            Vector2(tableSize.x, tableSize.y))

                topRightPocketTop = LineSegment(
                Vector2(tableSize.x - CORNER_POCKET_LENGTH, 0.0),
            Vector2(tableSize.x, 0.0)
        )

        bottomRightPocketBottom = LineSegment(Vector2(tableSize.x - CORNER_POCKET_LENGTH, tableSize.y), Vector2(tableSize.x, tableSize.y))
    }

}