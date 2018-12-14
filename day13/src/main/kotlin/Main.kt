import java.io.File

fun main() {
    val input = readInputFile()

    println("Part I: the solution is ${runPartI(input)}.")
    println("Part II: the solution is ${runPartII(input)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun runPartI(input: List<String>): Coordinate {
    val track = Track(input)
    return track.runPartI()
}

fun runPartII(input: List<String>): Coordinate {
    val track = Track(input)
    return track.runPartII()
}

class Track(input: List<String>) {
    private val numberOfRows: Int = input.size
    private val numberOfCols: Int = input
            .map { row -> row.length }
            .max()!!

    private val paths: Array<CharArray>
    private var carts: MutableList<Cart>

    init {
        paths = Array(numberOfCols) { CharArray(numberOfRows) }
        carts = mutableListOf()

        for (y in 0 until numberOfRows) {
            val line = input[y]

            for (x in 0 until numberOfCols) {
                if (x >= line.length) {
                    paths[x][y] = ' '
                } else {
                    val symbol = line[x]

                    when {
                        " |-/+\\".contains(symbol) -> paths[x][y] = line[x]

                        // fill in path character that is covered up by a cart
                        symbol == '>' || symbol == '<' -> {
                            paths[x][y] = '-'

                            if (symbol == '>') {
                                carts.add(Cart(Direction.RIGHT, Coordinate(x, y)))
                            } else if (symbol == '<') {
                                carts.add(Cart(Direction.LEFT, Coordinate(x, y)))
                            }
                        }

                        symbol == '^' || symbol == 'v' -> {
                            paths[x][y] = '|'

                            if (symbol == '^') {
                                carts.add(Cart(Direction.UP, Coordinate(x, y)))
                            } else if (symbol == 'v') {
                                carts.add(Cart(Direction.DOWN, Coordinate(x, y)))
                            }
                        }
                    }
                }
            }
        }
    }

    fun runPartI(): Coordinate {
        while (true) {
            getSortedAliveCarts().forEach { cart ->
                cart.advance(getNextPath(cart))

                val crashLocation = getCrashLocation()
                if (crashLocation != null) {
                    return crashLocation
                }
            }
        }
    }

    fun runPartII(): Coordinate {
        while (true) {
            getSortedAliveCarts().forEach { cart ->
                cart.advance(getNextPath(cart))

                val crashLocation = getCrashLocation()
                if (crashLocation != null) {
                    processCrash(crashLocation)
                }
            }

            val aliveCarts = getSortedAliveCarts()
            if (aliveCarts.size == 1) {
                return aliveCarts[0].location
            }
        }
    }

    private fun getSortedAliveCarts(): List<Cart> {
        return carts
                .filter { it.isAlive }
                .sortedWith(compareBy({ it.location.y }, { it.location.x }))
                .toMutableList()
    }

    private fun getNextPath(cart: Cart): Char {
        return when (cart.direction) {
            Direction.RIGHT -> paths[cart.location.x + 1][cart.location.y]
            Direction.LEFT -> paths[cart.location.x - 1][cart.location.y]
            Direction.UP -> paths[cart.location.x][cart.location.y - 1]
            Direction.DOWN -> paths[cart.location.x][cart.location.y + 1]
        }
    }

    private fun processCrash(crashLocation: Coordinate) {
        carts.filter { it.isAlive && it.location == crashLocation }.forEach { cart ->
            cart.isAlive = false
        }
    }

    private fun getCrashLocation(): Coordinate? {
        val locationCounts = getAliveCartLocations()
                .groupingBy { it }
                .eachCount()

        for ((location, count) in locationCounts) {
            // only one location will appear more than once
            if (count == 2) {
                return location
            }
        }

        return null
    }

    private fun getAliveCartLocations(): List<Coordinate> {
        return getSortedAliveCarts().map { it -> it.location }
    }

    private fun print() {
        println()
        for (y in 0 until numberOfRows) {
            for (x in 0 until numberOfCols) {
                val location = Coordinate(x, y)

                if (location in getAliveCartLocations()) {
                    for (cart in carts.filter { it.location == location }) {
                        if (cart.isAlive) {
                            print(cart.direction)
                        } else {
                            print(paths[x][y])
                        }
                    }
                } else {
                    print(paths[x][y])
                }
            }
            println()
        }
        println()
    }
}

data class Cart(var direction: Direction, var location: Coordinate, var isAlive: Boolean = true) {
    private var numberOfIntersections = 0

    fun advance(nextPath: Char) {
        location = when (direction) {
            Direction.RIGHT -> Coordinate(location.x + 1, location.y)
            Direction.LEFT -> Coordinate(location.x - 1, location.y)
            Direction.UP -> Coordinate(location.x, location.y - 1)
            Direction.DOWN -> Coordinate(location.x, location.y + 1)
        }

        // change direction if necessary
        when (nextPath) {
            '/' -> direction = when (direction) {
                Direction.RIGHT, Direction.LEFT -> direction.turnLeft()
                else -> direction.turnRight()
            }

            '\\' -> direction = when (direction) {
                Direction.RIGHT, Direction.LEFT -> direction.turnRight()
                Direction.UP, Direction.DOWN -> direction.turnLeft()
            }

            '+' -> {
                when (IntersectionCommand.values()[numberOfIntersections % IntersectionCommand.values().size]) {
                    IntersectionCommand.TURN_LEFT -> direction = direction.turnLeft()
                    IntersectionCommand.STRAIGHT -> {
                    }
                    IntersectionCommand.TURN_RIGHT -> direction = direction.turnRight()
                }

                numberOfIntersections++
            }
        }
    }
}

data class Coordinate(val x: Int, val y: Int) {
    override fun toString(): String {
        return "$x,$y"
    }
}
