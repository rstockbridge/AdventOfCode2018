import java.io.File
import java.lang.IllegalStateException

fun main() {
    val input = readInputFile()

    println("Part I: the solution is ${solvePartI(input)}.")
    println("Part II: the solution is ${solvePartII(input)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun solvePartI(ids: List<String>): Int {
    val numberOfIdsBySizeOfRepeat = ids.flatMap { id -> id.groupingBy { it }.eachCount().values.toSet() }
            .groupingBy { it }
            .eachCount()

    return numberOfIdsBySizeOfRepeat[2]!! * numberOfIdsBySizeOfRepeat[3]!!
}

fun solvePartII(ids: List<String>): String {
    val idLength = ids[0].length

    for (i in 0 until ids.size - 2) {
        for (j in (i + 1) until (ids.size - 1)) {
            val sharedLetters = getSharedLetters(ids[i], ids[j])

            if (sharedLetters.length == idLength - 1) {
                return sharedLetters
            }
        }
    }

    throw IllegalStateException("This line should not be reached.")
}

fun getSharedLetters(id1: String, id2: String): String {
    var result = ""

    for (i in 0..(id1.length - 1)) {
        if (id1[i] == id2[i]) {
            result += id1[i]
        }
    }

    return result
}
