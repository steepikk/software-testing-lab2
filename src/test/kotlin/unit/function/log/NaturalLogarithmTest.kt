package unit.function.log

import function.log.NaturalLogarithm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ln
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assumptions.assumeTrue

class NaturalLogarithmTest {

    private lateinit var ln: NaturalLogarithm

    @BeforeEach
    fun setUp() {
        ln = NaturalLogarithm()
    }

    @Test
    fun `should not calculate for zero`() {
        assertThrows<IllegalArgumentException> {
            ln.compute(BigDecimal.ZERO, PRECISION)
        }
    }

    @Test
    fun `should not calculate for negative x`() {
        assertThrows<IllegalArgumentException> {
            ln.compute(BigDecimal("-1"), PRECISION)
        }
    }

    @Test
    fun `should calculate ln(1) = 0`() {
        val result = ln.compute(BigDecimal.ONE, PRECISION)
        assertEquals(0, result.compareTo(BigDecimal.ZERO), "ln(1) должен быть 0")
    }

    @ParameterizedTest(name = "ln({0})")
    @ValueSource(doubles = [0.5, 0.801, 1.5, 2.0, 2.2, 3.0, 5.0, 10.0])
    fun `ln(x) should be close to Math log(x)`(x: Double) {
        assumeTrue(x > 0)

        val xBD = BigDecimal(x.toString())
        val expectedDouble = ln(x)
        val expectedBD = BigDecimal(expectedDouble.toString()).setScale(SCALE, RoundingMode.HALF_EVEN)
        val actualBD = ln.compute(xBD, PRECISION).setScale(SCALE, RoundingMode.HALF_EVEN)

        assertEquals(expectedBD, actualBD, "ln($x) должен быть ≈ $expectedDouble")
    }

    @Test
    fun `ln(e) should be approximately 1`() {
        val e = BigDecimal("2.718281828")
        val result = ln.compute(e, PRECISION)
        val tolerance = BigDecimal("0.0001")

        val diff = result.subtract(BigDecimal.ONE).abs()
        assert(diff <= tolerance) { "ln(e) = $result, ожидалось ≈1, разница = $diff" }
    }

    companion object {
        private val PRECISION = BigDecimal("0.000001")
        private const val SCALE = 6
    }
}