package unit.function.trigonometry

import function.trigonometry.Cosine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.math.cos
import testutil.BigDecimalAssertions.assertBigDecimalCloseTo
import testutil.BigDecimalAssertions.assertBigDecimalEquals

class CosineTest {

    private lateinit var cosine: Cosine

    @BeforeEach
    fun setUp() {
        cosine = Cosine()
    }

    @Test
    fun `cos(0) should be 1`() {
        val result = cosine.compute(BigDecimal.ZERO, PRECISION)
        assertBigDecimalCloseTo(BigDecimal.ONE, result, TOLERANCE)
    }

    @Test
    fun `cos(pi delete 2) should be 0`() {
        val pi = BigDecimal(PI.toString())
        val halfPi = pi.divide(BigDecimal("2"), SCALE, RoundingMode.HALF_EVEN)
        val result = cosine.compute(halfPi, PRECISION)
        assertBigDecimalCloseTo(BigDecimal.ZERO, result, TOLERANCE)
    }

    @Test
    fun `cos(pi) should be -1`() {
        val pi = BigDecimal(PI.toString())
        val result = cosine.compute(pi, PRECISION)
        assertBigDecimalCloseTo(BigDecimal.ONE.negate(), result, TOLERANCE)
    }

    @Test
    fun `cos(-pi delete 2) should be 0`() {
        val pi = BigDecimal(PI.toString())
        val halfPi = pi.divide(BigDecimal("2"), SCALE, RoundingMode.HALF_EVEN)
        val result = cosine.compute(halfPi.negate(), PRECISION)
        assertBigDecimalCloseTo(BigDecimal.ZERO, result, TOLERANCE)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/function/trigonometry/cos.csv"], numLinesToSkip = 1)
    fun `cos(x) from csv`(x: Double, y: Double) {
        val xBD = BigDecimal(x.toString())
        val expected = BigDecimal(cos(x)).setScale(SCALE, RoundingMode.HALF_EVEN)
        val result = cosine.compute(xBD, PRECISION)
        assertBigDecimalCloseTo(expected, result, TOLERANCE, "cos($x) should be close to reference")
    }

    companion object {
        private val PRECISION = BigDecimal("0.0000001")
        private const val SCALE = 7
        private val TOLERANCE = BigDecimal("1E-5")
    }
}