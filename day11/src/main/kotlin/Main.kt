import java.lang.Double.NEGATIVE_INFINITY

fun main() {
    val powerLevelGrid = fillPowerLevelGrid(3214)

    println("Part I: the solution is ${solvePartI(powerLevelGrid)}.")
    println("Part II: the solution is ${solvePartII(powerLevelGrid)}.")
}

fun solvePartI(powerLevelGrid: Array<IntArray>): String {
    val size = 3
    var maxTotalPower = -10000
    var maxCoordinate = ""

    for (topLeftX in 1..(301 - size)) {
        for (topLeftY in 1..(301 - size)) {
            var totalPower = 0

            for (x in topLeftX..(topLeftX + size - 1)) {
                for (y in topLeftY..(topLeftY + size - 1)) {
                    totalPower += powerLevelGrid[x - 1][y - 1]
                }
            }

            if (totalPower > maxTotalPower) {
                maxTotalPower = totalPower
                maxCoordinate = "$topLeftX,$topLeftY"
            }
        }
    }

    return maxCoordinate
}

fun solvePartII(powerLevelGrid: Array<IntArray>): String {
    var maxTotalPower = NEGATIVE_INFINITY.toInt()
    var maxCoordinateAndSize = ""

    for (size in 1..300) {
        for (topLeftX in 1..(301 - size)) {
            for (topLeftY in 1..(301 - size)) {
                var totalPower = 0

                for (x in topLeftX..(topLeftX + size - 1)) {
                    for (y in topLeftY..(topLeftY + size - 1)) {
                        totalPower += powerLevelGrid[x - 1][y - 1]
                    }
                }

                if (totalPower > maxTotalPower) {
                    maxTotalPower = totalPower
                    maxCoordinateAndSize = "$topLeftX,$topLeftY,$size"
                }
            }
        }
    }

    return maxCoordinateAndSize
}

fun fillPowerLevelGrid(gridSerialNumber: Int): Array<IntArray> {
    val result = Array(300) { IntArray(300) }

    for (x in 1..300) {
        for (y in 1..300) {
            result[x - 1][y - 1] = calculatePowerLevel(x, y, gridSerialNumber)
        }
    }

    return result
}

fun calculatePowerLevel(x: Int, y: Int, gridSerialNumber: Int): Int {
    val rackId = x + 10
    var result = rackId * y
    result += gridSerialNumber
    result *= rackId
    result = getHundredsDigit(result)
    result -= 5

    return result
}

fun getHundredsDigit(input: Int): Int {
    return (input / 100) % 10
}
