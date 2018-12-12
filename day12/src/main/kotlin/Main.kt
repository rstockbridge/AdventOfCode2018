import java.io.File

fun main() {
    val rawInput = readInputFile()
    val initialState = getInitialState(rawInput)
    val plantGeneratingPatterns = getPlantGeneratingPatterns(rawInput)

    println("Part I: the solution is ${solvePartI(initialState, plantGeneratingPatterns)}.")
    println("Part II: the solution is ${solvePartII(initialState, plantGeneratingPatterns)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun getInitialState(input: List<String>): String {
    return input[0].replace("initial state: ".toRegex(), "")
}

fun getPlantGeneratingPatterns(input: List<String>): Set<String> {
    val shortenedInput = input.toMutableList()

    // remove first two lines
    shortenedInput.removeAt(0)
    shortenedInput.removeAt(0)

    val result = mutableSetOf<String>()

    for (line in shortenedInput) {
        val noteRegex = "([.#]{5}) => ([.#])".toRegex()
        val (noteLeft, noteRight) = noteRegex.matchEntire(line)!!.destructured

        if (noteRight == "#") {
            result.add(noteLeft)
        }
    }

    return result
}

fun solvePartI(initialState: String, plantGeneratingPatterns: Set<String>): Int {
    var currentState = initialState
    var indexOfLeftPot = 0

    for (generation in 1..20) {
        // add only necessary "." at the front
        when (getFirstPlantIndex(currentState)) {
            0 -> {
                currentState = "....$currentState"
                indexOfLeftPot -= 4
            }
            1 -> {
                currentState = "...$currentState"
                indexOfLeftPot -= 3
            }
            2 -> {
                currentState = "..$currentState"
                indexOfLeftPot -= 2
            }
            3 -> {
                currentState = ".$currentState"
                indexOfLeftPot -= 1
            }
        }

        // add only necessary "." at the end
        when (getLastPlantIndex(currentState)) {
            currentState.length - 1 -> currentState = "$currentState...."
            currentState.length - 2 -> currentState = "$currentState..."
            currentState.length - 3 -> currentState = "$currentState.."
            currentState.length - 4 -> currentState = "$currentState."
        }

        var nextState = currentState
        var neighborsOfCurrentPot = currentState.substring(0, 5)

        for (currentPot in 2 until currentState.length - 2) {
            if (currentPot > 2) {
                neighborsOfCurrentPot = neighborsOfCurrentPot.substring(1) + currentState[currentPot + 2]
            }

            nextState = if (neighborsOfCurrentPot in plantGeneratingPatterns) {
                nextState.replaceRange(currentPot..currentPot, "#")
            } else {
                nextState.replaceRange(currentPot..currentPot, ".")
            }
        }

        currentState = nextState
    }

    return calculateSumOfPotNumbers(currentState, indexOfLeftPot)
}

fun solvePartII(initialState: String, plantGeneratingPatterns: Set<String>): Long {
    val generationsToReachSteadyState = 125 // determined by inspection

    var currentState = initialState
    var indexOfLeftPot = 0

    for (generation in 1..generationsToReachSteadyState) {
        // add only necessary "." at the front
        when (getFirstPlantIndex(currentState)) {
            0 -> {
                currentState = "....$currentState"
                indexOfLeftPot -= 4
            }
            1 -> {
                currentState = "...$currentState"
                indexOfLeftPot -= 3
            }
            2 -> {
                currentState = "..$currentState"
                indexOfLeftPot -= 2
            }
            3 -> {
                currentState = ".$currentState"
                indexOfLeftPot -= 1
            }
        }

        // add only necessary "." at the end
        when (getLastPlantIndex(currentState)) {
            currentState.length - 1 -> currentState = "$currentState...."
            currentState.length - 2 -> currentState = "$currentState..."
            currentState.length - 3 -> currentState = "$currentState.."
            currentState.length - 4 -> currentState = "$currentState."
        }

        var nextState = currentState
        var neighborsOfCurrentPot = currentState.substring(0, 5)

        for (currentPot in 2 until currentState.length - 2) {
            if (currentPot > 2) {
                neighborsOfCurrentPot = neighborsOfCurrentPot.substring(1) + currentState[currentPot + 2]
            }

            nextState = if (plantGeneratingPatterns.contains(neighborsOfCurrentPot)) {
                nextState.replaceRange(currentPot..currentPot, "#")
            } else {
                nextState.replaceRange(currentPot..currentPot, ".")
            }
        }

        currentState = nextState
    }

    // after 125 generations, the sum of pot numbers increases by fixed amount of 109 each generation
    // (determined by inspection)
    return calculateSumOfPotNumbers(currentState, indexOfLeftPot) + (50000000000 - generationsToReachSteadyState) * 109
}

fun getFirstPlantIndex(state: String): Int {
    var result = 0

    while (state[result] != '#') {
        result++
    }

    return result
}

fun getLastPlantIndex(state: String): Int {
    var result = state.length - 1

    while (state[result] != '#') {
        result--
    }

    return result
}

fun calculateSumOfPotNumbers(state: String, offset: Int): Int {
    var result = 0

    for (pot in 0 until state.length) {
        if (state[pot] == '#') {
            result += pot + offset
        }
    }

    return result
}
