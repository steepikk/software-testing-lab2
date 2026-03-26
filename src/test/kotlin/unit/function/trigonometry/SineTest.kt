package unit.function.trigonometry

import function.trigonometry.Sine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI
import org.junit.jupiter.api.Assertions.assertEquals

class SineTest {

    private lateinit var sine: Sine

    @BeforeEach
    fun setUp() {
        sine = Sine()
    }

    @Test
    fun `sin(0) should be 0`() {
        val result = sine.compute(BigDecimal.ZERO, PRECISION)
        assertEquals(0, result.compareTo(BigDecimal.ZERO))
    }

    @Test
    fun `sin(pi delete 2) should be 1`() {
        val pi = BigDecimal(PI.toString())
        val halfPi = pi.divide(BigDecimal("2"), SCALE, RoundingMode.HALF_EVEN)
        val expected = BigDecimal.ONE
        val result = sine.compute(halfPi, PRECISION)
        assertEquals(expected, result.stripTrailingZeros())
    }

    @Test
    fun `sin(pi) should be 0`() {
        val pi = BigDecimal(PI.toString())
        val result = sine.compute(pi, PRECISION)
        assertEquals(BigDecimal.ZERO, result.stripTrailingZeros())
    }

    @Test
    fun `sin(-pi delete 2) should be -1`() {
        val pi = BigDecimal(PI.toString())
        val halfPi = pi.divide(BigDecimal("2"), SCALE, RoundingMode.HALF_EVEN)
        val expected = BigDecimal.ONE.negate()
        val result = sine.compute(halfPi.negate(), PRECISION)
        assertEquals(expected, result.stripTrailingZeros())
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/function/trigonometry/sin.csv"], numLinesToSkip = 1)
    fun `sin(x) from csv`(x: Double, y: Double) {
        val xBD = BigDecimal(x.toString())
        val yBD = BigDecimal(y.toString())
        val result = sine.compute(xBD, PRECISION)
        assertEquals(yBD.stripTrailingZeros(), result.stripTrailingZeros(), "sin($x) должен быть $y")
    }

    companion object {
        private val PRECISION = BigDecimal("0.000001")
        private const val SCALE = 6
    }
}