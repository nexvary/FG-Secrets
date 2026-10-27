package com.fgmachines.secrets.domain

import java.math.BigDecimal
import java.math.RoundingMode

private val HUNDRED = BigDecimal("100")
private fun BigDecimal.nonNegative() = signum() >= 0
private fun BigDecimal.positive() = signum() > 0

enum class MassUnit(val grams: BigDecimal) { GRAM(BigDecimal.ONE), KILOGRAM(BigDecimal("1000")), TONNE(BigDecimal("1000000")) }
enum class Verification { UNVERIFIED, LITERATURE_REFERENCED, LABORATORY_TESTED, PRODUCTION_VALIDATED }
data class Evidence(val reference: String, val reviewedOn: String, val testReport: String = "")
data class Formulation(val percentages: List<BigDecimal>, val verification: Verification, val evidence: Evidence?, val sensitive: Boolean = false, val technicalApproval: String = "") {
    fun mayPublish(): Boolean = verification != Verification.UNVERIFIED &&
        evidence?.reference?.isNotBlank() == true && evidence.reviewedOn.isNotBlank() &&
        (verification == Verification.LITERATURE_REFERENCED || evidence.testReport.isNotBlank()) &&
        (!sensitive || technicalApproval.isNotBlank())
}

object Numbers {
    /** Decimal input only: Arabic digits and decimal separator supported; grouping is rejected. */
    fun parse(text: String): BigDecimal {
        val normalized = text.trim().map { c -> when (c) {
            in '٠'..'٩' -> '0' + (c - '٠')
            in '۰'..'۹' -> '0' + (c - '۰')
            '٫' -> '.'
            else -> c
        } }.joinToString("")
        require(normalized.length <= 24 && normalized.matches(Regex("[0-9]+(\\.[0-9]{1,8})?"))) { "Invalid decimal number" }
        return normalized.toBigDecimal()
    }
}

object FormulationCalculator {
    /** Mass percentages only. No implicit conversion between mass and volume. */
    fun scale(percentages: List<BigDecimal>, batch: BigDecimal, unit: MassUnit = MassUnit.KILOGRAM, output: MassUnit = unit): List<BigDecimal> {
        require(batch.positive()) { "Batch must be positive" }
        require(percentages.isNotEmpty() && percentages.all { it.nonNegative() && it <= HUNDRED }) { "Invalid percentages" }
        require(percentages.fold(BigDecimal.ZERO, BigDecimal::add).compareTo(HUNDRED) == 0) { "Percentages must total 100" }
        return percentages.map { batch.multiply(unit.grams).multiply(it).divide(HUNDRED.multiply(output.grams), 12, RoundingMode.HALF_UP).stripTrailingZeros() }
    }
    fun scaleVerified(formulation: Formulation, batch: BigDecimal, unit: MassUnit): List<BigDecimal> {
        require(formulation.mayPublish()) { "Formulation requires evidence and safety review" }
        return scale(formulation.percentages, batch, unit)
    }
}

data class CostInput(val material: BigDecimal, val labor: BigDecimal, val energy: BigDecimal, val packaging: BigDecimal, val other: BigDecimal, val outputUnits: BigDecimal, val lossPercent: BigDecimal, val marginPercent: BigDecimal)
data class CostResult(val batchCost: BigDecimal, val saleableUnits: BigDecimal, val unitCost: BigDecimal, val sellingPrice: BigDecimal)
object CostCalculator {
    /** Waste reduces saleable output; margin is profit / selling price, not markup. */
    fun calculate(input: CostInput): CostResult {
        val costs = listOf(input.material, input.labor, input.energy, input.packaging, input.other)
        require(costs.all { it.nonNegative() }) { "Negative cost" }
        require(input.outputUnits.positive()) { "Output must be positive" }
        require(input.lossPercent.nonNegative() && input.lossPercent < HUNDRED) { "Loss outside range" }
        require(input.marginPercent.nonNegative() && input.marginPercent < HUNDRED) { "Margin outside range" }
        val total = costs.fold(BigDecimal.ZERO, BigDecimal::add)
        val saleable = input.outputUnits.multiply(BigDecimal.ONE.subtract(input.lossPercent.divide(HUNDRED)))
        val unit = total.divide(saleable, 12, RoundingMode.HALF_UP)
        val price = total.divide(saleable.multiply(BigDecimal.ONE.subtract(input.marginPercent.divide(HUNDRED))), 12, RoundingMode.HALF_UP)
        return CostResult(total, saleable, unit, price)
    }
}
fun BigDecimal.display(): String = setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
