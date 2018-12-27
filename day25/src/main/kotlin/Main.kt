import java.io.File


fun main() {
    val points = parseInput(readInputFile())

    println("The solution is ${solveProblem(points)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun parseInput(input: List<String>): List<Coordinate4d> {
    val fixedPointRegex = "(-?\\d+),(-?\\d+),(-?\\d+),(-?\\d+)".toRegex()
    return input
            .map { instruction ->
                val ints = fixedPointRegex.matchEntire(instruction)!!.destructured.toList().map(String::toInt)
                Coordinate4d.fromInput(ints)
            }
}

fun solveProblem(points: List<Coordinate4d>): Int {
    val constellations = mutableListOf<MutableList<Coordinate4d>>()

    points.forEach { point ->
        val newConstellation = mutableListOf<Coordinate4d>()
        newConstellation.add(point)

        val iterator = constellations.listIterator()

        while (iterator.hasNext()) {
            val constellation = iterator.next()

            var i = 0
            var keepGoing = true

            while (keepGoing) {
                val constellationPoint = constellation[i]

                when {
                    point.taxicabDistanceTo(constellationPoint) <= 3 -> {
                        newConstellation.addAll(constellation)
                        iterator.remove()
                        keepGoing = false
                    }
                    i == constellation.size - 1 -> keepGoing = false
                    else -> i++
                }
            }
        }

        iterator.add(newConstellation)
    }

    return constellations.size
}

data class Coordinate4d(var x: Int, var y: Int, var z: Int, var t: Int) {
    companion object {
        fun fromInput(ints: List<Int>): Coordinate4d {
            return Coordinate4d(ints[0], ints[1], ints[2], ints[3])
        }
    }

    fun taxicabDistanceTo(other: Coordinate4d): Int {
        return Math.abs(other.x - this.x) + Math.abs(other.y - this.y) + Math.abs(other.z - this.z) + Math.abs(other.t - this.t)
    }
}

