import java.io.File

fun main() {
    val claims = parseInput(readInputFile())

    println("Part I: the solution is ${solvePartI(claims)}.")
    println("Part II: the solution is ${solvePartII(claims)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun parseInput(input: List<String>): List<Claim> {
    val regex = "#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)".toRegex()

    return input
            .map { line ->
                val (id, dim1, dim2, dim3, dim4) = regex.matchEntire(line)!!.destructured
                Claim(id.toInt(), dim1.toInt(), dim2.toInt(), dim3.toInt(), dim4.toInt())
            }
}

fun solvePartI(claims: List<Claim>): Int {
    return calculateCoordinateClaimCount(claims)
            .filter { coordinateClaimCount ->
                coordinateClaimCount.value >= 2
            }
            .size
}

fun solvePartII(claims: List<Claim>): Int {
    val claimCount = calculateCoordinateClaimCount(claims)

    // only one claim will satisfy the criterion
    return claims
            .first { claim ->
                claim.coordinates.sumBy { coordinate -> claimCount[coordinate]!! } == claim.coordinates.size
            }
            .id
}

fun calculateCoordinateClaimCount(claims: List<Claim>): Map<Coordinate, Int> {
    return claims
            .flatMap { claim -> claim.coordinates }
            .groupingBy { it }
            .eachCount()
}

data class Claim(val id: Int, val numberOfInchesFromLeftEdge: Int, val numberOfInchesFromTopEdge: Int, var width: Int, var height: Int) {
    val coordinates: MutableSet<Coordinate> = mutableSetOf()

    init {
        for (i in numberOfInchesFromLeftEdge until numberOfInchesFromLeftEdge + width) {
            for (j in numberOfInchesFromTopEdge until numberOfInchesFromTopEdge + height) {
                coordinates.add(Coordinate(i, j))
            }
        }
    }
}

data class Coordinate(val inchesFromLeftEdge: Int, val inchesFromTopEdge: Int)
