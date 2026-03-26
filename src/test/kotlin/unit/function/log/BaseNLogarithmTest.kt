package unit.function.log

import function.log.BaseNLogarithm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.math.BigDecimal
import java.math.RoundingMode
import org.junit.jupiter.api.Assertions.assertEquals

class BaseNLogarithmTest {

    private lateinit var log5: BaseNLogarithm

    @BeforeEach
    fun setUp() {
        log5 = BaseNLogarithm(5)
    }

    @Test
    fun `should not calculate for x = 0`() {
        assertThrows<IllegalArgumentException> {
            log5.compute(BigDecimal.ZERO, PRECISION)
        }
    }

    @Test
    fun `should not calculate for x less 0`() {
        assertThrows<IllegalArgumentException> {
            log5.compute(BigDecimal("-1"), PRECISION)
        }
    }

    @Test
    fun `should calculate log5(1) = 0`() {
        val result = log5.compute(BigDecimal.ONE, PRECISION)
        assertEquals(0, result.compareTo(BigDecimal.ZERO), "log₅(1) должен быть 0")
    }

    @Test
    fun `should not allow base less or = 0`() {
        assertThrows<IllegalArgumentException> {
            BaseNLogarithm(0)
        }
        assertThrows<IllegalArgumentException> {
            BaseNLogarithm(-5)
        }
    }

    @Test
    fun `should not allow base = 1`() {
        assertThrows<IllegalArgumentException> {
            BaseNLogarithm(1)
        }
    }

    @ParameterizedTest(name = "log5({0}) ≈ {1}")
    @CsvFileSource(resources = ["/function/log/log5.csv"], numLinesToSkip = 1, delimiter = ',')
    fun `log5(x) from csv`(x: BigDecimal, y: BigDecimal) {
        val result = log5.compute(x, PRECISION).setScale(SCALE, RoundingMode.HALF_EVEN)
        val expected = y.setScale(SCALE, RoundingMode.HALF_EVEN)
        assertEquals(expected, result, "log₅($x) должен быть ≈ $y")
    }

    companion object {
        private val PRECISION = BigDecimal("0.0000001")
        private const val SCALE = 7
    }
}