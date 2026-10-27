package com.fgmachines.secrets

import com.fgmachines.secrets.domain.*
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class CalculatorTest {
    private fun d(s: String) = BigDecimal(s)
    private fun equal(expected: String, actual: BigDecimal) { assertEquals(0, d(expected).compareTo(actual)) }
    private fun rejects(block: () -> Unit) { try { block(); fail("Expected rejection") } catch (_: IllegalArgumentException) {} }
    @Test fun localizedDecimalParsing() { equal("12.5", Numbers.parse("١٢٫٥")); equal("12.5", Numbers.parse("۱۲.۵")) }
    @Test fun rejectsMalformedAndUnboundedNumbers() { listOf("NaN", "Infinity", "1,000", "1e9", "", "-1", "1.2.3", "1٬000", "9".repeat(25), "0.123456789").forEach { rejects { Numbers.parse(it) } } }
    @Test fun scalesAndConservesMass() {
        val amounts = FormulationCalculator.scale(listOf(d("33.33"), d("66.67")), d("3"))
        equal("0.9999", amounts[0]); equal("2.0001", amounts[1]); equal("3", amounts.reduce(BigDecimal::add))
    }
    @Test fun convertsMassUnits() { equal("250000", FormulationCalculator.scale(listOf(d("25"), d("75")), d("1"), MassUnit.TONNE, MassUnit.GRAM)[0]) }
    @Test fun rejectsInvalidFormulations() {
        listOf(emptyList(), listOf(d("99.999")), listOf(d("101"), d("-1"))).forEach { rejects { FormulationCalculator.scale(it, d("1")) } }
        rejects { FormulationCalculator.scale(listOf(d("100")), d("0")) }
    }
    @Test fun evidenceAndSafetyGatesFailClosed() {
        val recipe = Formulation(listOf(d("100")), Verification.UNVERIFIED, null)
        rejects { FormulationCalculator.scaleVerified(recipe, d("1"), MassUnit.KILOGRAM) }
        val evidence = Evidence("ref", "2026-10-27")
        assertTrue(recipe.copy(verification = Verification.LITERATURE_REFERENCED, evidence = evidence).mayPublish())
        assertFalse(recipe.copy(verification = Verification.PRODUCTION_VALIDATED, evidence = evidence).mayPublish())
        val validated = recipe.copy(verification = Verification.PRODUCTION_VALIDATED, evidence = evidence.copy(testReport = "report"))
        assertTrue(validated.mayPublish()); assertFalse(validated.copy(sensitive = true).mayPublish())
        assertTrue(validated.copy(sensitive = true, technicalApproval = "review").mayPublish())
    }
    private fun input() = CostInput(d("100"), d("20"), d("10"), d("5"), d("15"), d("100"), d("10"), d("25"))
    @Test fun wasteAndMarginAreNotMarkup() {
        val r = CostCalculator.calculate(input()); equal("150", r.batchCost); equal("90", r.saleableUnits)
        equal("1.666666666667", r.unitCost); equal("2.222222222222", r.sellingPrice)
    }
    @Test fun rejectsInvalidCosts() {
        listOf(input().copy(material = d("-1")), input().copy(outputUnits = d("0")), input().copy(lossPercent = d("100")), input().copy(marginPercent = d("100")), input().copy(lossPercent = d("-1")), input().copy(marginPercent = d("-1"))).forEach { rejects { CostCalculator.calculate(it) } }
    }
    @Test fun zeroCostAndZeroLossAreValid() { equal("0", CostCalculator.calculate(CostInput(d("0"),d("0"),d("0"),d("0"),d("0"),d("1"),d("0"),d("0"))).sellingPrice) }
}
