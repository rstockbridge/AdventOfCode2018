import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import java.io.File
import java.util.*

fun main() {
    println("Part I: the solution is ${solvePartI(makeGraph(readInputFile(), true))}.")
    println("Part II: the solution is ${solvePartII(makeGraph(readInputFile(), false))}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun solvePartI(graph: MutableGraph<Chain>): String {
    var result = ""

    while (graph.nodes().size > 0) {
        val sortedAvailableChains = getAvailableChains(graph).sortedBy { it.name }
        result += sortedAvailableChains[0].name
        graph.removeNode(sortedAvailableChains[0])
    }

    return result
}

fun solvePartII(graph: MutableGraph<Chain>): Int {
    var result = 0

    val workerCurrentChain = (0 until Constants.PART_II_TOTAL_WORKERS)
            .associate { it -> Pair(it, null) }
            .toMutableMap<Int, Chain?>()

    while (getTotalSizeOfGraph(graph) > 0) {
        val sortedAvailableChainsNotStarted = getAvailableChains(graph).sortedBy { it.name }
        val queueOfAvailableChainsNotStarted = ArrayDeque<Chain>(sortedAvailableChainsNotStarted)

        for (worker in 0 until Constants.PART_II_TOTAL_WORKERS) {
            var chainToUpdate: Chain? = null

            // assign chain to worker, if available
            // if worker has already started chain they must finish
            // otherwise look for an available chain that hasn't been started
            if (workerCurrentChain[worker] != null) {
                chainToUpdate = workerCurrentChain[worker]!!
            } else if (queueOfAvailableChainsNotStarted.isNotEmpty()) {
                chainToUpdate = queueOfAvailableChainsNotStarted.first
                workerCurrentChain[worker] = chainToUpdate
                queueOfAvailableChainsNotStarted.remove()
            }

            if (chainToUpdate != null) {
                chainToUpdate.removeNode()

                // if chain empty after removing node, delete chain
                if (chainToUpdate.getSize() == 0) {
                    graph.removeNode(chainToUpdate)
                    workerCurrentChain[worker] = null
                }
            }

            // since workers work simultaneously, only count time for one worker
            if (worker == 0) {
                result++
            }
        }
    }

    return result
}

fun makeGraph(input: List<String>, isPartI: Boolean): MutableGraph<Chain> {
    val result = GraphBuilder.directed().allowsSelfLoops(false).build<Chain>()

    input.forEach { line ->
        val leftChainName = line[5]
        val rightChainName = line[36]

        val leftChainSize: Int
        val rightChainSize: Int

        if (isPartI) {
            leftChainSize = 1
            rightChainSize = 1
        } else {
            // 'A' -> 61 seconds, 'B' -> 62 seconds, 'C' -> 63 seconds, etc
            leftChainSize = leftChainName.toInt() - 4
            rightChainSize = rightChainName.toInt() - 4
        }

        val leftChain = Chain(leftChainName, leftChainSize)
        val rightChain = Chain(rightChainName, rightChainSize)

        result.putEdge(leftChain, rightChain)
    }

    return result
}

fun getTotalSizeOfGraph(graph: MutableGraph<Chain>): Int {

    return graph.nodes()
            .sumBy { chain -> chain.getSize() }
}

fun getAvailableChains(graph: MutableGraph<Chain>): List<Chain> {

    return graph.nodes()
            .filter { chain -> graph.inDegree(chain) == 0 && !chain.started }
            .map { chain -> chain }
}

data class Node(val name: String)

data class Chain(val name: Char, val initialSize: Int) {
    private val graph = GraphBuilder.directed().allowsSelfLoops(false).build<Node>()
    var started: Boolean = false

    init {
        var leftNode = Node("$name$initialSize")
        graph.addNode(leftNode)

        for (i in 1..(initialSize - 1)) {
            val rightNode = Node("$name${initialSize - i}")
            graph.putEdge(leftNode, rightNode)
            leftNode = rightNode
        }
    }

    fun getSize(): Int {
        return graph.nodes().size
    }

    fun removeNode() {
        val leftNode = Node("$name${getSize()}")
        graph.removeNode(leftNode)
        started = true
    }
}

object Constants {
    const val PART_II_TOTAL_WORKERS = 5
}
