import java.io.File

fun main() {
    val input = readInputFile()

    println("Part I: the solution is ${solvePartI(input, 10)}.")

    /* Determined by inspection that after 1000 iterations (probably sooner), the process repeats itself with a period
       of 28 iterations. (1000000000 - 1000) % 28 = 0, so the solution for 1000000000 is the same as the solution for 1000 */
    println("Part II: the solution is ${solvePartI(input, 1000)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun solvePartI(inputLumberArea: List<String>, numberOfIterations: Int): Int {
    val height = inputLumberArea.size
    val width = inputLumberArea[0].length

    var lumberArea = inputLumberArea

    for (i in 1..numberOfIterations) {
        val newLumberArea = lumberArea.toMutableList()

        for (x in 0 until width) {
            for (y in 0 until height) {
                val adjacentCoordinates = getAdjacentCoordinates(Coordinate(x, y), height, width)
                val adjacentAcres = adjacentCoordinates.map { lumberArea[it.y][it.x] }

                if (lumberArea[y][x] == '.' && adjacentAcres.filter { it == '|' }.size >= 3) {
                    newLumberArea[y] = newLumberArea[y].replaceRange(x, x + 1, "|")
                } else if (lumberArea[y][x] == '|' && adjacentAcres.filter { it == '#' }.size >= 3) {
                    newLumberArea[y] = newLumberArea[y].replaceRange(x, x + 1, "#")
                } else if (lumberArea[y][x] == '#' && (adjacentAcres.none { it == '#' } || adjacentAcres.none { it == '|' })) {
                    newLumberArea[y] = newLumberArea[y].replaceRange(x, x + 1, ".")
                }
            }
        }

        lumberArea = newLumberArea
    }

    val numberOfWoodedAcres = lumberArea
            .flatMap { it ->
                it.toList()
            }
            .filter { it == '|' }
            .size

    val numberOfLumberyards = lumberArea
            .flatMap { it ->
                it.toList()
            }
            .filter { it == '#' }
            .size

    return numberOfWoodedAcres * numberOfLumberyards
}

fun getAdjacentCoordinates(coordinate: Coordinate, height: Int, width: Int): List<Coordinate> {
    val result = mutableListOf<Coordinate>()

    for (x in (coordinate.x - 1)..(coordinate.x + 1)) {
        for (y in (coordinate.y - 1)..(coordinate.y + 1)) {
            val potentialAdjacentCoordinate = Coordinate(x, y)
            if (x in (0 until width) && y in (0 until height) && potentialAdjacentCoordinate != coordinate) {
                result.add(potentialAdjacentCoordinate)
            }
        }
    }

    return result
}

data class Coordinate(val x: Int, val y: Int)
