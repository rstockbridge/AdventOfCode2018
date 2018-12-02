import java.io.File

fun main() {
    val inputAsInts = readInputFile().map(String::toInt)

    println("Part I: the solution is ${solvePartI(inputAsInts)}.")
    println("Part II: the solution is ${solvePartII(inputAsInts)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun solvePartI(frequencyChanges: List<Int>): Int {
    return frequencyChanges.sum()
}

fun solvePartII(frequencyChanges: List<Int>): Int {
    var sum = 0
    val observed = mutableSetOf(sum)

    outer@ while (true) {
        for (change in frequencyChanges) {
            sum += change

            if (!observed.add(sum)) {
                break@outer
            }
        }
    }

    return sum
}
