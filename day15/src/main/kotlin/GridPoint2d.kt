@file:Suppress("unused", "MemberVisibilityCanBePrivate")

import kotlin.math.abs

data class GridPoint2d(val x: Int, val y: Int) {

    companion object {
        val origin = GridPoint2d(0, 0)
    }

    fun adjacentPoints(): Set<GridPoint2d> = setOf(
            GridPoint2d(x + 1, y),
            GridPoint2d(x - 1, y),
            GridPoint2d(x, y + 1),
            GridPoint2d(x, y - 1)
    )

    fun shiftBy(dx: Int = 0, dy: Int = 0): GridPoint2d {
        return copy(x = x + dx, y = y + dy)
    }

    fun l1DistanceTo(other: GridPoint2d) = abs(x - other.x) + abs(y - other.y)
}
