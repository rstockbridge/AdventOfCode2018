import java.io.File
import java.lang.IllegalStateException

fun main() {
    val partIDevice = parseInput(readInputFilePartI())

    println("Part I: the solution is ${solvePartI(partIDevice)}.")
    println("Part II: the solution is ${solvePartII(10551339)}.")
}

fun readInputFilePartI(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun parseInput(input: List<String>): Device {
    val boundRegisterRegex = "#ip (\\d)".toRegex()
    val (boundRegister) = boundRegisterRegex.matchEntire(input[0])!!.destructured.toList().map(String::toInt)

    val instructionInput = input.toMutableList()
    instructionInput.removeAt(0)

    val instructionRegex = "([a-z]+) (\\d+) (\\d+) (\\d+)".toRegex()
    val instructions = instructionInput
            .map { instruction ->
                val (opcodeAsString, a, b, c) = instructionRegex.matchEntire(instruction)!!.destructured
                Instruction.fromInput(opcodeAsString, a.toInt(), b.toInt(), c.toInt())
            }

    return Device(boundRegister = boundRegister, instructions = instructions)
}

fun solvePartI(device: Device): Int {
    while (device.instructionPointer < device.instructions.size) {
        val instruction = device.instructions[device.instructionPointer]

        device.registers[device.boundRegister] = device.instructionPointer
        instruction.opcode.applyTo(device.registers, instruction.a, instruction.b, instruction.c)
        device.instructionPointer = device.registers[device.boundRegister]
        device.instructionPointer++
    }

    return device.registers[0]
}

fun solvePartII(number: Int): Int {
    // device calculates the sum of all the factors of the input number

    var result = 0

    for (i in 1..number) {
        if (number % i == 0) {
            result += i
        }
    }

    return result
}

data class Device(var registers: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0, 0),
                  val boundRegister: Int,
                  var instructionPointer: Int = 0,
                  val instructions: List<Instruction>)

data class Instruction(val opcode: Opcode, val a: Int, val b: Int, val c: Int) {

    companion object {
        fun fromInput(opcodeString: String, a: Int, b: Int, c: Int): Instruction {
            val opcode = when (opcodeString) {
                "addr" -> Opcode.ADDR
                "addi" -> Opcode.ADDI
                "mulr" -> Opcode.MULR
                "muli" -> Opcode.MULI
                "banr" -> Opcode.BANR
                "bani" -> Opcode.BANI
                "borr" -> Opcode.BORR
                "bori" -> Opcode.BORI
                "setr" -> Opcode.SETR
                "seti" -> Opcode.SETI
                "gtir" -> Opcode.GTIR
                "gtri" -> Opcode.GTRI
                "gtrr" -> Opcode.GTRR
                "eqir" -> Opcode.EQIR
                "eqri" -> Opcode.EQRI
                "eqrr" -> Opcode.EQRR

                else -> {
                    throw IllegalStateException("This line should not be reached.")
                }
            }

            return Instruction(opcode, a, b, c)
        }
    }
}
