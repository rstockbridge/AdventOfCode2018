import java.io.File
import java.lang.Double.POSITIVE_INFINITY
import kotlin.math.abs

fun main() {
    val input = parseInput(readInputFile())

    println("Part I: the solution is ${solvePartI(input)}.")
    println("Part II: the solution is ${solvePartII(input)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun parseInput(input: List<String>): List<Coordinate> {
    return input
            .map { line -> line.replace(" ", "").split(",") }
            .map { splitLine -> Coordinate(splitLine[0].toInt(), splitLine[1].toInt()) }
}

fun solvePartI(coordinates: List<Coordinate>): Int? {
    val coordinatesWithFiniteArea = getCoordinatesWithFiniteArea(coordinates)
    val area = coordinatesWithFiniteArea.associateTo(mutableMapOf()) { it to 0 }

    for (x in getMinX(coordinates)..getMaxX(coordinates)) {
        for (y in getMinY(coordinates)..getMaxY(coordinates)) {
            val closestCoordinate = getClosestCoordinate(Coordinate(x, y), coordinates)

            if (closestCoordinate != null && closestCoordinate in coordinatesWithFiniteArea) {
                area[closestCoordinate] = area[closestCoordinate]!! + 1
            }
        }
    }

    return area.values.max()
}

fun solvePartII(coordinates: List<Coordinate>): Int {
    var result = 0

    for (x in getMinX(coordinates)..getMaxX(coordinates)) {
        for (y in getMinY(coordinates)..getMaxY(coordinates)) {
            if (coordinates.sumBy { calculateTaxicabDistance(Coordinate(x, y), it) } < 10000) {
                result++
            }
        }
    }

    return result
}

fun getCoordinatesWithFiniteArea(coordinates: List<Coordinate>): List<Coordinate> {
    val result = coordinates.toMutableList()

    for (x in getMinX(coordinates)..getMaxX(coordinates)) {
        val closestCoordinateToTopBorder = getClosestCoordinate(Coordinate(x, getMinY(coordinates)), coordinates)
        if (closestCoordinateToTopBorder in result) {
            result.remove(closestCoordinateToTopBorder)
        }

        val closestCoordinateToBottomBorder = getClosestCoordinate(Coordinate(x, getMaxY(coordinates)), coordinates)
        if (closestCoordinateToBottomBorder in result) {
            result.remove(closestCoordinateToBottomBorder)
        }
    }

    for (y in (getMinY(coordinates) + 1)..(getMaxY(coordinates) - 1)) {
        val closestCoordinateToLeftBorder = getClosestCoordinate(Coordinate(getMinX(coordinates), y), coordinates)
        if (closestCoordinateToLeftBorder in result) {
            result.remove(closestCoordinateToLeftBorder)
        }

        val closestCoordinateToRightBorder = getClosestCoordinate(Coordinate(getMaxX(coordinates), y), coordinates)
        if (closestCoordinateToRightBorder in result) {
            result.remove(closestCoordinateToRightBorder)
        }
    }

    return result
}

fun getMinX(coordinates: List<Coordinate>): Int {
    return coordinates
            .map { it.x }
            .min()!!
}

fun getMaxX(coordinates: List<Coordinate>): Int {
    return coordinates
            .map { it.x }
            .max()!!
}

fun getMinY(coordinates: List<Coordinate>): Int {
    return coordinates
            .map { it.y }
            .min()!!
}

fun getMaxY(coordinates: List<Coordinate>): Int {
    return coordinates
            .map { it.y }
            .max()!!
}

fun getClosestCoordinate(location: Coordinate, coordinates: List<Coordinate>): Coordinate? {
    var minDistance = POSITIVE_INFINITY.toInt()
    var result: Coordinate? = null

    coordinates.forEach { coordinate ->
        val taxicabDistance = calculateTaxicabDistance(location, coordinate)

        if (taxicabDistance == minDistance) {
            result = null
            minDistance = taxicabDistance
        } else if (taxicabDistance < minDistance) {
            minDistance = taxicabDistance
            result = coordinate
        }
    }

    return result
}

fun calculateTaxicabDistance(coordinate1: Coordinate, coordinate2: Coordinate): Int {
    return abs(coordinate1.x - coordinate2.x) + abs(coordinate1.y - coordinate2.y)
}

data class Coordinate(val x: Int, val y: Int)

