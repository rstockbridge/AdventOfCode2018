import Constants.Companion.MINUTES_IN_AN_HOUR
import java.io.File
import java.lang.Double.NEGATIVE_INFINITY

fun main() {
    val guardData = extractGuardData(readInputFile())

    println("Part I: the solution is ${solvePartI(guardData)}.")
    println("Part II: the solution is ${solvePartII(guardData)}.")
}

fun readInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("input.txt").file).readLines()
}

fun extractGuardData(input: List<String>): Map<Int, List<BooleanArray>> {
    val result = HashMap<Int, MutableList<BooleanArray>>()
    val sortedEntries = extractAndSortEntries(input)
    var currentGuardFirstRow = 0

    while (currentGuardFirstRow < sortedEntries.size) {
        val guardId = extractGuardId(sortedEntries[currentGuardFirstRow].text)
        val nextGuardFirstRow = calculateNextGuardFirstRow(currentGuardFirstRow, sortedEntries)
        val isSleepingDataForShift = calculateIsSleepingDataForShift(currentGuardFirstRow, nextGuardFirstRow, sortedEntries)

        if (guardId !in result) {
            result[guardId] = mutableListOf(isSleepingDataForShift)
        } else {
            result[guardId]!!.add(isSleepingDataForShift)
        }

        currentGuardFirstRow = nextGuardFirstRow
    }

    return result
}

fun extractAndSortEntries(input: List<String>): List<Entry> {
    val entries = mutableListOf<Entry>()

    input.forEach { line ->
        val regex = "\\[(.+)] (.+)".toRegex()
        val (dateAsString, text) = regex.matchEntire(line)!!.destructured
        entries.add(Entry(dateAsString, text))
    }

    return entries.sortedWith(compareBy { it.dateAsString })
}

fun calculateNextGuardFirstRow(rowWithGuardId: Int, sortedEntries: List<Entry>): Int {
    var result = rowWithGuardId + 1

    while ((result < sortedEntries.size - 1) and ("#" !in sortedEntries[result].text)) {
        result++
    }

    if (result == sortedEntries.size - 1) {
        result++
    }

    return result
}

fun extractGuardId(text: String): Int {
    return text.split(" ")[1].replace("#".toRegex(), "").toInt()
}

fun calculateIsSleepingDataForShift(
        currentGuardFirstRow: Int,
        nextGuardFirstRow: Int,
        sortedEntries: List<Entry>): BooleanArray {

    val result = BooleanArray(MINUTES_IN_AN_HOUR)

    var previousMinuteSleepStatusUpdates = 0
    var isAsleep = false

    for (row in (currentGuardFirstRow + 1) until nextGuardFirstRow) {
        val entry = sortedEntries[row]
        val nextMinuteSleepStatusUpdates =
                entry.dateAsString.substring(entry.dateAsString.length - 2, entry.dateAsString.length).toInt()

        for (minute in previousMinuteSleepStatusUpdates until nextMinuteSleepStatusUpdates) {
            result[minute] = isAsleep
        }

        isAsleep = !isAsleep
        previousMinuteSleepStatusUpdates = nextMinuteSleepStatusUpdates
    }

    for (minute in previousMinuteSleepStatusUpdates until MINUTES_IN_AN_HOUR) {
        result[minute] = isAsleep
    }

    return result
}

fun solvePartI(guardData: Map<Int, List<BooleanArray>>): Int {
    val guardId = calculateMostAsleepGuardId(guardData)
    val minute = calculateDataForMostShiftsAsleepForGuard(guardData[guardId]!!).minute

    return guardId * minute
}

fun calculateMostAsleepGuardId(guardData: Map<Int, List<BooleanArray>>): Int {
    var maxNumberOfMinutesAsleep = 0
    var result = 0

    guardData.forEach { (guardId, isSleepingDataAllShifts) ->
        var numberOfMinutesAsleep = 0

        isSleepingDataAllShifts.forEach { isSleepingDataForShift ->
            numberOfMinutesAsleep += isSleepingDataForShift.map(Boolean::toInt).sum()
        }

        if (numberOfMinutesAsleep > maxNumberOfMinutesAsleep) {
            result = guardId
            maxNumberOfMinutesAsleep = numberOfMinutesAsleep
        }
    }

    return result
}

fun calculateDataForMostShiftsAsleepForGuard(isSleepingData: List<BooleanArray>): DataForMostShiftsAsleepForGuard {
    val numberOfDaysAsleepByMinute = IntArray(MINUTES_IN_AN_HOUR)

    isSleepingData.forEach { isSleepingDataForShift ->
        for (minute in 0 until MINUTES_IN_AN_HOUR) {
            if (isSleepingDataForShift[minute]) {
                numberOfDaysAsleepByMinute[minute]++
            }
        }
    }

    return DataForMostShiftsAsleepForGuard(numberOfDaysAsleepByMinute.indexOf(numberOfDaysAsleepByMinute.max()!!),
            numberOfDaysAsleepByMinute.max()!!)
}

fun solvePartII(guardData: Map<Int, List<BooleanArray>>): Int {
    var guardIdWithMaxShiftsAsleepSameMinute = -1
    var maxShiftsAsleepSameMinute = -1
    var minuteWithMaxShiftsAsleep = -1

    guardData.keys.forEach { guardId ->
        val (minuteForGuard, numberOfShiftsForGuard) = calculateDataForMostShiftsAsleepForGuard(guardData[guardId]!!)

        if (numberOfShiftsForGuard > maxShiftsAsleepSameMinute) {
            guardIdWithMaxShiftsAsleepSameMinute = guardId
            minuteWithMaxShiftsAsleep = minuteForGuard
            maxShiftsAsleepSameMinute = numberOfShiftsForGuard
        }
    }

    return guardIdWithMaxShiftsAsleepSameMinute * minuteWithMaxShiftsAsleep
}

data class Entry(val dateAsString: String, val text: String)

data class DataForMostShiftsAsleepForGuard(val minute: Int, val numberOfShifts: Int)

class Constants {
    companion object {
        const val MINUTES_IN_AN_HOUR = 60
    }
}

fun Boolean.toInt() = if (this) 1 else 0
