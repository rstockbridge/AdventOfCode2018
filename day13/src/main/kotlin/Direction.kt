enum class Direction(val symbol: Char) {
    RIGHT('>'),
    LEFT('<'),
    UP('^'),
    DOWN('v');

    fun turnRight(): Direction {
        return when (this) {
            RIGHT -> DOWN
            LEFT -> UP
            UP -> RIGHT
            DOWN -> LEFT
        }
    }

    fun turnLeft(): Direction {
        return when (this) {
            RIGHT -> UP
            LEFT -> DOWN
            UP -> LEFT
            DOWN -> RIGHT
        }
    }

    override fun toString(): String {
        return symbol.toString()
    }
}
