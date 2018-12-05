import java.io.File
import java.lang.Double.POSITIVE_INFINITY

fun main() {
    val input = readInputFile()[0]

    println("Part I: the solution is ${solvePartI(input)}.")
    println("Part II: the solution is ${solvePartII(input)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun solvePartI(polymer: String): Int {
    return getReactedPolymerLength(polymer)
}

fun solvePartII(polymer: String): Int {
    var minLength = POSITIVE_INFINITY.toInt()

    for (letter in 'a'..'z') {
        val newLength = getReactedPolymerLength(removeLetter(polymer, letter))

        if (newLength < minLength) {
            minLength = newLength
        }
    }

    return minLength
}

fun getReactedPolymerLength(polymer: String): Int {
    val result = polymer.toMutableList()

    var previousLength = result.size
    var keepGoing = true

    while (keepGoing) {
        val iterator = result.listIterator()

        iterator.next()

        while (iterator.hasNext()) {
            val letter2Index = iterator.nextIndex()
            val letter1 = result[letter2Index - 1]
            val letter2 = result[letter2Index]

            if (Math.abs(letter1.toInt() - letter2.toInt()) == 32) {
                iterator.remove()
                iterator.next()
                iterator.remove()
            }

            if (iterator.hasNext()) {
                iterator.next()
            }
        }

        keepGoing = (result.size != previousLength)
        previousLength = result.size
    }

    return result.size
}

fun removeLetter(input: String, letter: Char): String {
    return input.replace(("[$letter${letter.toUpperCase()}]").toRegex(), "")
}
