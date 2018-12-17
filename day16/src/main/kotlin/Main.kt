import java.io.File

fun main() {
    val samples = parseInputPartI(readInputFilePartI())
    println("Part I: the solution is ${solvePartI(samples)}.")

    val instructions = parseInputPartII(readInputFilePartII())
    println("Part II: the solution is ${solvePartII(samples, instructions)}.")
}

fun readInputFilePartI(): List<String> {
    return File(ClassLoader.getSystemResource("inputPartI.txt").file).readLines()
}

fun parseInputPartI(input: List<String>): List<Sample> {
    val beforeRegex = "Before:\\s+\\[(\\d), (\\d), (\\d), (\\d)]".toRegex()
    val instructionRegex = "(\\d+) (\\d) (\\d) (\\d)".toRegex()
    val afterRegex = "After:\\s+\\[(\\d), (\\d), (\\d), (\\d)]".toRegex()

    return input
            .chunked(4)
            .map { sampleInput ->
                val before = beforeRegex.matchEntire(sampleInput[0])!!.destructured
                        .toList()
                        .map(String::toInt)
                val instruction = Instruction.fromList((instructionRegex.matchEntire(sampleInput[1])!!
                        .destructured
                        .toList()
                        .map(String::toInt)))
                val after = afterRegex.matchEntire(sampleInput[2])!!
                        .destructured
                        .toList()
                        .map(String::toInt)
                Sample(before, instruction, after)
            }
}

fun solvePartI(samples: List<Sample>): Int {
    return samples
            .count { sample ->
                Opcode.values()
                        .count { opcode ->
                            opcode.isConsistent(sample)
                        } >= 3
            }

}

fun readInputFilePartII(): List<String> {
    return File(ClassLoader.getSystemResource("inputPartII.txt").file).readLines()
}

fun parseInputPartII(input: List<String>): List<Instruction> {
    val instructionRegex = "(\\d+) (\\d) (\\d) (\\d)".toRegex()

    return input.map { line ->
        Instruction.fromList(
                instructionRegex.matchEntire(line)!!
                        .destructured
                        .toList()
                        .map(String::toInt)
        )
    }
}

fun solvePartII(samples: List<Sample>, instructions: List<Instruction>): Int {
    val mapFromOpcodeIdToOpcode = matchOpcodesToOpcodeIds(calculatePossibleOpcodes(samples))

    var registers = listOf(0, 0, 0, 0)
    instructions.forEach { instruction ->
        val opcode = mapFromOpcodeIdToOpcode[instruction.opcodeId]
        registers = opcode!!.applyTo(registers, instruction.a, instruction.b, instruction.c)
    }

    return registers[0]
}

fun calculatePossibleOpcodes(samples: List<Sample>): Map<Int, Set<Opcode>> {
    val result = mutableMapOf<Int, Set<Opcode>>()

    for (opcodeId in 0..15) {
        val possibleOpcodes = mutableSetOf<Opcode>()

        samples
                .filter { sample -> sample.instruction.opcodeId == opcodeId }
                .forEach { sample ->
                    Opcode.values()
                            .forEach { opcode ->
                                if (opcode.isConsistent(sample)) {
                                    possibleOpcodes.add(opcode)
                                }
                            }
                }

        result[opcodeId] = possibleOpcodes
    }

    return result
}

fun matchOpcodesToOpcodeIds(possibleOpcodes: Map<Int, Set<Opcode>>): MutableMap<Int, Opcode> {
    val mutablePossibleOpcodes = mutableMapOf<Int, MutableSet<Opcode>>()
    possibleOpcodes.forEach { mutablePossibleOpcodes[it.key] = it.value.toMutableSet() }

    val result = mutableMapOf<Int, Opcode>()

    var keepGoing = true
    while (keepGoing) {
        for (opcodeId in 0..15) {
            if (mutablePossibleOpcodes[opcodeId]!!.size == 1) {
                val opcode = mutablePossibleOpcodes[opcodeId]!!.first()
                result[opcodeId] = opcode

                for (opCodeId in 0..15) {
                    mutablePossibleOpcodes[opCodeId]!!.remove(opcode)
                }
            }
        }

        keepGoing = mutablePossibleOpcodes.any { it.value.isNotEmpty() }
    }

    return result
}

data class Sample(val before: List<Int>, val instruction: Instruction, val after: List<Int>)

data class Instruction(val opcodeId: Int, val a: Int, val b: Int, val c: Int) {

    companion object {
        fun fromList(ints: List<Int>): Instruction {
            return Instruction(
                    opcodeId = ints[0],
                    a = ints[1],
                    b = ints[2],
                    c = ints[3]
            )
        }
    }
}
