import java.io.File

fun main() {
    val input = readInputFile()[0].split(" ").map(String::toInt)

    println("Part I: the solution is ${solvePartI(input)}.")
    println("Part II: the solution is ${solvePartII(input)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun solvePartI(input: List<Int>): Int {
    val mutableInput = input.toMutableList()
    var result = 0

    fun processNode() {
        val numberOfChildren = mutableInput[0]
        mutableInput.removeAt(0)
        val numberOfMetaDataEntries = mutableInput[0]
        mutableInput.removeAt(0)

        for (child in 0 until numberOfChildren) {
            processNode()
        }

        for (i in 0 until numberOfMetaDataEntries) {
            result += mutableInput[0]
            mutableInput.removeAt(0)
        }
    }

    processNode()

    return result
}

fun solvePartII(input: List<Int>): Int {
    val mutableInput = input.toMutableList()

    fun calculateNodeValue(): Int {
        var result = 0

        val numberOfChildren = mutableInput[0]
        mutableInput.removeAt(0)
        val numberOfMetaDataEntries = mutableInput[0]
        mutableInput.removeAt(0)

        val childrenValues = (0..(numberOfChildren - 1)).map { calculateNodeValue() }

        for (i in 0 until numberOfMetaDataEntries) {
            val metaData = mutableInput[0]

            if (numberOfChildren == 0) {
                result += metaData
            } else {
                if ((metaData > 0) and (metaData <= numberOfChildren)) {
                    result += childrenValues[metaData - 1]!!
                }
            }

            mutableInput.removeAt(0)
        }

        return result
    }

    return calculateNodeValue()
}
