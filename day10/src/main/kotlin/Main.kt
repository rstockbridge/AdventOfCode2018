import java.io.File

fun main() {
    val points = parseInput(readInputFile())
    val board = Board(points)

    println("Part I: the solution is\n")

    val numberOfSeconds = solveProblem(board)

    println("Part II: the solution is $numberOfSeconds.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun parseInput(input: List<String>): List<Point> {
    return input.map { line ->
        val inputRegex = "position=<\\s*+(-?\\d+),\\s*+(-?\\d+)> velocity=<\\s*+(-?\\d+),\\s*+(-?\\d+)>".toRegex()
        val (positionX, positionY, velocityX, velocityY) = inputRegex.matchEntire(line)!!.destructured
        Point(Coordinate(positionX.toInt(), positionY.toInt()), Coordinate(velocityX.toInt(), velocityY.toInt()))
    }
}

fun solveProblem(board: Board): Int {
    var currentBoard = board
    var nextBoard = board.next()
    var i = 0

    while (currentBoard.getArea() > nextBoard.getArea()) {
        currentBoard = nextBoard
        nextBoard = nextBoard.next()
        i++
    }

    currentBoard.print()

    return i
}

class Board(private val points: List<Point>) {
    fun next(): Board {
        return Board(points.map { it.next() })
    }

    fun print() {
        for (y in getMinY()..getMaxY()) {
            for (x in getMinX()..getMaxX()) {
                if (hasPoint(x, y)) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
        println()
    }

    private fun hasPoint(x: Int, y: Int): Boolean {
        for (point in points) {
            if (point.position == Coordinate(x, y)) {
                return true
            }
        }

        return false
    }

    private fun getMinX(): Int {
        return points
                .map { it -> it.position.x }
                .min()!!
    }

    private fun getMaxX(): Int {
        return points
                .map { it -> it.position.x }
                .max()!!
    }

    private fun getMinY(): Int {
        return points
                .map { it -> it.position.y }
                .min()!!
    }

    private fun getMaxY(): Int {
        return points
                .map { it -> it.position.y }
                .max()!!
    }

    fun getArea(): Long {
        return (getMaxX().toLong() - getMinX().toLong()) * (getMaxY().toLong() - getMinY().toLong())
    }
}

data class Point(val position: Coordinate, val velocity: Coordinate) {
    fun next(): Point {
        val newPosition = Coordinate(position.x + velocity.x, position.y + velocity.y)
        return Point(newPosition, velocity)
    }
}

data class Coordinate(val x: Int, val y: Int)
