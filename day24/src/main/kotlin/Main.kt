import java.io.File

fun main() {
    println("Part I: the solution is ${solvePartI(readImmuneSystemInputFile(), readInfectionInputFile())}.")
    println("Part II: the solution is ${solvePartII(readImmuneSystemInputFile(), readInfectionInputFile())}.")
}

fun readImmuneSystemInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("immune_system_input.txt").file).readLines()
}

fun readInfectionInputFile(): List<String> {
    return File(ClassLoader.getSystemResource("infection_input.txt").file).readLines()
}

fun solvePartI(immuneSystemInput: List<String>, infectionInput: List<String>): Int {
    val immuneSystemArmy = createArmy(immuneSystemInput, ArmyType.IMMUNE_SYSTEM)
    val infectionArmy = createArmy(infectionInput, ArmyType.INFECTION)

    val outcome = runCombat(immuneSystemArmy, infectionArmy)

    return if (outcome == Outcome.IMMUNE_SYSTEM) {
        immuneSystemArmy.groups.sumBy { group -> group.numberOfUnits }
    } else { // assuming outcome will never be a stalemate
        infectionArmy.groups.sumBy { group -> group.numberOfUnits }
    }
}

fun solvePartII(immuneSystemInput: List<String>, infectionInput: List<String>): Int {
    var boost = 76

    while (true) {
        val immuneSystemArmy = createArmy(immuneSystemInput, ArmyType.IMMUNE_SYSTEM)
        val infectionArmy = createArmy(infectionInput, ArmyType.INFECTION)
        immuneSystemArmy.boostBy(boost)

        val outcome = runCombat(immuneSystemArmy, infectionArmy)

        if (outcome == Outcome.IMMUNE_SYSTEM) {
            return immuneSystemArmy.groups.sumBy { group -> group.numberOfUnits }
        } else {
            boost++
        }
    }
}

fun createArmy(input: List<String>, armyType: ArmyType): Army {
    val groupRegex = "(\\d+) units each with (\\d+) hit points(\\s?.+\\s?)with an attack that does (\\d+) (.+) damage at initiative (\\d+)".toRegex()

    var groupId = 0
    return Army(
            input.map { groupInput ->
                val (numberOfUnitsString,
                        numberOfHitPointsString,
                        immunitiesWeaknessesString,
                        attackDamageString,
                        attackTypeString,
                        initiativeString) = groupRegex.matchEntire(groupInput)!!.destructured

                var weaknesses = listOf<Attack>()
                var immunities = listOf<Attack>()

                if (immunitiesWeaknessesString.isNotEmpty()) {
                    if (";" in immunitiesWeaknessesString) {
                        val splitImmunitiesWeaknesses = immunitiesWeaknessesString.split(";")

                        if ("weak" in splitImmunitiesWeaknesses[0]) {
                            weaknesses = Attack.identify(splitImmunitiesWeaknesses[0])
                        } else if ("weak" in splitImmunitiesWeaknesses[1]) {
                            weaknesses = Attack.identify(splitImmunitiesWeaknesses[1])
                        }

                        if ("immune" in splitImmunitiesWeaknesses[0]) {
                            immunities = Attack.identify(splitImmunitiesWeaknesses[0])
                        } else if ("immune" in splitImmunitiesWeaknesses[1]) {
                            immunities = Attack.identify(splitImmunitiesWeaknesses[1])
                        }
                    } else {
                        if ("weak" in immunitiesWeaknessesString) {
                            weaknesses = Attack.identify(immunitiesWeaknessesString)
                        }
                        if ("immune" in immunitiesWeaknessesString) {
                            immunities = Attack.identify(immunitiesWeaknessesString)
                        }
                    }
                }

                groupId++

                Group(armyType,
                        armyType.toString() + groupId,
                        numberOfUnitsString.toInt(),
                        numberOfHitPointsString.toInt(),
                        attackDamageString.toInt(),
                        Attack.identify(attackTypeString)[0],
                        initiativeString.toInt(),
                        weaknesses,
                        immunities)
            }
                    .toMutableList())
}

fun getGroup(army: Army, id: String): Group {
    return army.groups.first { group -> group.id == id }
}

fun calculateTargetIds(attackingArmy: Army, defendingArmy: Army): Map<String, String?> {
    val sortedAttackingArmyGroups = attackingArmy
            .groups
            .sortedWith(compareByDescending<Group> { attackingGroup ->
                attackingGroup.effectivePower
            }.thenByDescending { attackingGroup -> attackingGroup.initiative })

    val mutableDefendingArmyGroups = defendingArmy.groups.toMutableList()
    val result = mutableMapOf<String, String?>()

    sortedAttackingArmyGroups.forEach { attackingGroup ->
        if (mutableDefendingArmyGroups.isNotEmpty()) {
            val potentialDefendingTarget = mutableDefendingArmyGroups
                    .sortedWith(compareByDescending<Group> { defendingGroup ->
                        defendingGroup.damageBy(attackingGroup)
                    }.thenByDescending { defendingGroup ->
                        defendingGroup.effectivePower
                    }.thenByDescending { defendingGroup ->
                        defendingGroup.initiative
                    })
                    .first()

            if (potentialDefendingTarget.damageBy(attackingGroup) > 0) {
                result[attackingGroup.id] = potentialDefendingTarget.id
                mutableDefendingArmyGroups -= potentialDefendingTarget
            } else {
                result[attackingGroup.id] = null
            }
        } else {
            result[attackingGroup.id] = null
        }
    }

    return result.toMap()
}

fun runCombat(immuneSystemArmy: Army, infectionArmy: Army): Outcome {
    var currentImmuneSystemTotalSize = immuneSystemArmy.totalSize()
    var currentInfectionTotalSize = infectionArmy.totalSize()

    while (true) {
        // targets must be calculated first, rather than on the fly
        val immuneSystemTargetIds = calculateTargetIds(immuneSystemArmy, infectionArmy)
        val infectionTargetIds = calculateTargetIds(infectionArmy, immuneSystemArmy)

        val sortedCombinedGroups = (immuneSystemArmy.groups + infectionArmy.groups)
                .sortedByDescending { group -> group.initiative }

        sortedCombinedGroups.forEach { attackingGroup ->
            if (attackingGroup.numberOfUnits != 0) {
                val targetIds: Map<String, String?>
                val targetArmy: Army

                when (attackingGroup.armyType) {
                    ArmyType.IMMUNE_SYSTEM -> {
                        targetIds = immuneSystemTargetIds
                        targetArmy = infectionArmy
                    }
                    ArmyType.INFECTION -> {
                        targetIds = infectionTargetIds
                        targetArmy = immuneSystemArmy
                    }
                }

                val targetId = targetIds[attackingGroup.id]
                if (targetId != null) {
                    val targetGroup = getGroup(targetArmy, targetId)
                    targetGroup.attackBy(attackingGroup)
                }

                targetArmy.groups.removeIf { group -> group.numberOfUnits <= 0 }
            }
        }

        if (immuneSystemArmy.isEmpty()) {
            return Outcome.INFECTION
        } else if (infectionArmy.isEmpty()) {
            return Outcome.IMMUNE_SYSTEM
        } else if (currentImmuneSystemTotalSize == immuneSystemArmy.totalSize() && currentInfectionTotalSize == infectionArmy.totalSize()) {
            return Outcome.STALEMATE
        } else {
            currentImmuneSystemTotalSize = immuneSystemArmy.totalSize()
            currentInfectionTotalSize = infectionArmy.totalSize()
        }
    }
}

data class Army(var groups: MutableList<Group>) {
    fun isEmpty(): Boolean {
        return groups.size == 0
    }

    fun totalSize(): Int {
        return groups.sumBy { group -> group.numberOfUnits }
    }

    fun boostBy(boost: Int) {
        groups.forEach { group -> group.attackDamage += boost }
    }
}

data class Group(val armyType: ArmyType,
                 val id: String,
                 var numberOfUnits: Int,
                 val numberOfHitPoints: Int,
                 var attackDamage: Int,
                 val attackType: Attack,
                 val initiative: Int,
                 val weaknesses: List<Attack>,
                 val immunities: List<Attack>) {

    val effectivePower: Int
        get() = numberOfUnits * attackDamage

    fun damageBy(otherGroup: Group): Int {
        return when {
            otherGroup.attackType in this.weaknesses -> otherGroup.effectivePower * 2
            otherGroup.attackType in this.immunities -> 0
            else -> otherGroup.effectivePower
        }
    }

    fun attackBy(otherGroup: Group) {
        numberOfUnits = Math.max(0, numberOfUnits - damageBy(otherGroup) / numberOfHitPoints)
    }
}

enum class ArmyType {
    IMMUNE_SYSTEM, INFECTION;
}

enum class Attack {
    BLUDGEONING, COLD, FIRE, RADIATION, SLASHING;

    companion object {
        fun identify(input: String): List<Attack> {
            val result = mutableListOf<Attack>()

            if ("bludgeoning" in input) result += BLUDGEONING
            if ("cold" in input) result += COLD
            if ("fire" in input) result += FIRE
            if ("radiation" in input) result += RADIATION
            if ("slashing" in input) result += SLASHING

            return result
        }
    }
}

enum class Outcome {
    IMMUNE_SYSTEM, INFECTION, STALEMATE;
}
