package unit.system

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import system.EquationSystem
import testutil.BigDecimalAssertions.assertBigDecimalEquals

class EquationSystemTest {

    companion object {
        private val PRECISION = BigDecimal("0.0000001")
        private const val SCALE = 7
    }

    private lateinit var system: EquationSystem

    @BeforeEach
    fun setUp() {
        system = EquationSystem()
    }

    @Test
    fun `f(0) should be tan^2(0) = 0`() {
        val result = system.compute(BigDecimal.ZERO, PRECISION)
        assertBigDecimalEquals(BigDecimal.ZERO, result, "f(0) should be 0")
    }

    @Test
    fun `f(-pi) should be tan^2(-pi) = 0`() {
        val pi = BigDecimal(PI.toString())
        val result = system.compute(pi.negate(), PRECISION)
        assertBigDecimalEquals(BigDecimal.ZERO, result, "f(-π) should be 0")
    }

    @Test
    fun `f(x) should throw when tan(x) undefined (x = -pi delete 2)`() {
        val pi = BigDecimal(PI.toString())
        val piHalf = pi.divide(BigDecimal("2"), SCALE, RoundingMode.HALF_EVEN)

        assertThrows<IllegalArgumentException> {
            system.compute(piHalf.negate(), PRECISION)
        }
    }

    @Test
    fun `f(x) for x approaching 0+ should be finite and reasonable`() {
        val x = BigDecimal("0.0001")
        val result = system.compute(x, PRECISION)

        assertTrue(result < BigDecimal("100"), "f(0.0001) is too large: $result")
    }

    @Test
    fun `f(x) for large x should be small`() {
        val x = BigDecimal("1000")
        val result = system.compute(x, PRECISION)
        assertTrue(result < BigDecimal("0.7"), "f(1000) = $result, expected < 0.7")
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/system/equationSystem.csv"], numLinesToSkip = 1)
    fun `f(x) should match expected value`(x: Double, y: Double) {
        val xBD = BigDecimal(x.toString())
        val yBD = BigDecimal(y.toString())
        val result = system.compute(xBD, PRECISION).setScale(7, RoundingMode.HALF_EVEN)
        val expected = yBD.setScale(7, RoundingMode.HALF_EVEN)

        assertBigDecimalEquals(expected, result, "f($x) should be close to ≈ $y")
    }
}