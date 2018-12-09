import java.util.*

fun main() {
    println("Part I: the solution is ${solveProblem(471, 72026)}.")
    println("Part II: the solution is ${solveProblem(471, 7202600)}.")
}

fun solveProblem(numberOfPlayers: Int, marbleMaxValue: Int): Long {
    val playerScores = LongArray(numberOfPlayers)

    val circle = Circle()
    circle.addFirst(0)

    for (marble in 1..marbleMaxValue) {
        if (marble % 23 != 0) {
            circle.rotate(2)
            circle.addLast(marble)
        } else {
            circle.rotate(-7)
            playerScores[marble % numberOfPlayers] += marble + circle.pop().toLong()
        }
    }

    return playerScores.max()!!
}

class Circle : ArrayDeque<Int>() {
    fun rotate(numberOfSteps: Int) {
        if (numberOfSteps >= 0) {
            for (i in 0 until numberOfSteps) {
                addFirst(removeLast())
            }
        } else {
            for (i in 0 until (Math.abs(numberOfSteps) - 1)) {
                addLast(remove())
            }
        }
    }
}
