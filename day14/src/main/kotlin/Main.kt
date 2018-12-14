fun main() {
    val input = "236021"
    val inputAsInt = input.toInt()
    val inputAsList = input
            .toList()
            .map { (it - 48).toInt() }

    println("Part I: the solution is ${solvePartI(inputAsInt)}.")
    println("Part II: the solution is ${solvePartII(inputAsList)}.")
}

fun solvePartI(numberOfRecipes: Int): String {
    val scoreboard = mutableListOf(3, 7)

    var elf1Index = 0
    var elf2Index = 1

    var scoreboardLength = 2

    while (scoreboardLength < numberOfRecipes + 10) {
        val sumOfScoresAsString = (scoreboard[elf1Index] + scoreboard[elf2Index]).toString()

        sumOfScoresAsString.forEach { char ->
            scoreboard.add(char.toInt() - 48)
        }

        elf1Index = (elf1Index + 1 + scoreboard[elf1Index]) % scoreboard.size
        elf2Index = (elf2Index + 1 + scoreboard[elf2Index]) % scoreboard.size

        scoreboardLength = scoreboard.size
    }

    return (numberOfRecipes..numberOfRecipes + 9)
            .map { recipe -> scoreboard[recipe] }
            .joinToString("")
}

fun solvePartII(pattern: List<Int>): Int {
    val scoreboard = mutableListOf(3, 7)

    var elf1Index = 0
    var elf2Index = 1

    while (true) {
        val sumOfScoresAsString = (scoreboard[elf1Index] + scoreboard[elf2Index]).toString()

        sumOfScoresAsString.forEach { char ->
            scoreboard.add(char.toInt() - 48)
        }

        val tailMatchIndex = scoreboard
                .takeLast(pattern.size + 1)
                .windowed(pattern.size)
                .indexOfFirst { window -> window == pattern }

        if (tailMatchIndex != -1) {
            return scoreboard.size - (pattern.size + 1) + tailMatchIndex
        }

        elf1Index = (elf1Index + 1 + scoreboard[elf1Index]) % scoreboard.size
        elf2Index = (elf2Index + 1 + scoreboard[elf2Index]) % scoreboard.size
    }
}
