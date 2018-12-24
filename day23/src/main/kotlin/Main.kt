import java.io.File

fun main() {
    val nanobots = parseInput(readInputFile())

    println("Part I: the solution is ${solvePartI(nanobots)}.")
    println("Part II: not solved; no general algorithm apparently available.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun parseInput(input: List<String>): List<Nanobot> {
    val nanobotRegex = "pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)".toRegex()
    return input
            .map { instruction ->
                val (x, y, z, signalStrength) = nanobotRegex.matchEntire(instruction)!!.destructured
                Nanobot.fromInput(x.toInt(), y.toInt(), z.toInt(), signalStrength.toInt())
            }
}

fun solvePartI(nanobots: List<Nanobot>): Int {
    val nanobotWithLargestSignalRadius = nanobots.maxBy { nanobot -> nanobot.signalRadius }!!

    return nanobots
            .filter { nanobot -> nanobotWithLargestSignalRadius.signalRadius >= nanobotWithLargestSignalRadius.getManhattanDistance(nanobot) }
            .size
}

data class Nanobot(val position: Coordinate3d, val signalRadius: Int) {

    companion object {
        fun fromInput(x: Int, y: Int, z: Int, signalRadius: Int): Nanobot {
            return Nanobot(Coordinate3d(x, y, z), signalRadius)
        }
    }

    fun getManhattanDistance(other: Nanobot): Int {
        return this.position.getManhattanDistance(other.position)
    }
}

data class Coordinate3d(val x: Int, val y: Int, val z: Int) {
    fun getManhattanDistance(other: Coordinate3d): Int {
        return Math.abs(other.x - this.x) + Math.abs(other.y - this.y) + Math.abs(other.z - this.z)
    }
}



