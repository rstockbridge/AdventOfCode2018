import java.io.File
import java.lang.IllegalStateException

fun main() {
    val device = parseInput(readInputFile())

    println("Part I: the solution is ${solvePartI(device)}.")
    println("Part II: the solution is ${solvePartII(device)}.")
}

fun readInputFile(): List<String> {
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
    // instruction 28 translates to if(r[5] == r[0]) then goto 31 (quit), so the program quits the first time r[0] = r[5]

    // quit once program hits instruction 28
    while (device.instructionPointer != 28) {
        val instruction = device.instructions[device.instructionPointer]

        device.registers[device.boundRegister] = device.instructionPointer
        instruction.opcode.applyTo(device.registers, instruction.a, instruction.b, instruction.c)
        device.instructionPointer = device.registers[device.boundRegister]
        device.instructionPointer++
    }

    // so return the value of r[5] the first time the program hits instruction 28
    return device.registers[5]
}

fun solvePartII(device: Device): Int {
    // return the last non-repeated value of r[5] when the program hits instruction 28

    val register5Values = mutableSetOf<Int>()
    var previousValue = -1

    while (true) {
        if (device.instructionPointer == 28) {
            if (device.registers[5] !in register5Values) {
                previousValue = device.registers[5]
                register5Values.add(previousValue)
            } else {
                return previousValue
            }
        }

        val instruction = device.instructions[device.instructionPointer]

        device.registers[device.boundRegister] = device.instructionPointer
        instruction.opcode.applyTo(device.registers, instruction.a, instruction.b, instruction.c)
        device.instructionPointer = device.registers[device.boundRegister]
        device.instructionPointer++
    }
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
