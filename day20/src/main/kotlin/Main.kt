import java.io.File
import java.lang.IllegalStateException
import java.util.*

fun main() {
    val regex = readInputFile()[0]

    val result = solveProblem(regex)

    println("Part I: the solution is ${result.largestNumberOfDoors}.")
    println("Part II: the solution is ${result.numberOfRoomsRequiring1000Doors}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun solveProblem(regex: String): Result {
    var current = Coordinate(0, 0)
    val numberOfDoors = mutableMapOf(current to 0)
    val queuedLocations = ArrayDeque<Coordinate>()

    for (char in regex) {
        when (char) {
            '(' -> queuedLocations.add(current)
            ')' -> current = queuedLocations.pollLast()
            '|' -> current = queuedLocations.peekLast()
            'N', 'E', 'S', 'W' -> {
                val next = current.advance(char)

                if (numberOfDoors[next] != null && (numberOfDoors[current]!! + 1) < numberOfDoors[next]!! ||
                        numberOfDoors[next] == null) {
                    numberOfDoors[next] = numberOfDoors[current]!! + 1
                }

                current = next
            }
        }
    }

    val largestNumberOfDoors = numberOfDoors
            .map { it.value }!!
            .max()!!

    val numberOfRoomsRequiring1000Doors = numberOfDoors
            .filter { it.value >= 1000 }
            .count()

    return Result(largestNumberOfDoors, numberOfRoomsRequiring1000Doors)
}

data class Coordinate(val x: Int, val y: Int) {

    fun advance(direction: Char): Coordinate {
        when (direction) {
            'N' -> return Coordinate(this.x, this.y + 1)
            'E' -> return Coordinate(this.x + 1, this.y)
            'S' -> return Coordinate(this.x, this.y - 1)
            'W' -> return Coordinate(this.x - 1, this.y)
        }

        throw IllegalStateException("This line should not be reached.")
    }
}

data class Result(val largestNumberOfDoors: Int, val numberOfRoomsRequiring1000Doors: Int)
