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
        assertBigDecimalEquals(BigDecimal.ONE, result)
    }

    @Test
    fun `cos(pi delete 2) should be 0`() {
        val pi = BigDecimal(PI.toString())
        val halfPi = pi.divide(BigDecimal("2"), SCALE, RoundingMode.HALF_EVEN)
        val result = cosine.compute(halfPi, PRECISION)
        assertBigDecimalEquals(BigDecimal.ZERO, result)
    }

    @Test
    fun `cos(pi) should be -1`() {
        val pi = BigDecimal(PI.toString())
        val result = cosine.compute(pi, PRECISION)
        assertBigDecimalEquals(BigDecimal.ONE.negate(), result)
    }

    @Test
    fun `cos(-pi delete 2) should be 0`() {
        val pi = BigDecimal(PI.toString())
        val halfPi = pi.divide(BigDecimal("2"), SCALE, RoundingMode.HALF_EVEN)
        val result = cosine.compute(halfPi.negate(), PRECISION)
        assertBigDecimalEquals(BigDecimal.ZERO, result)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/function/trigonometry/cos.csv"], numLinesToSkip = 1)
    fun `cos(x) from csv`(x: Double, y: Double) {
        val xBD = BigDecimal(x.toString())
        val yBD = BigDecimal(y.toString())
        val result = cosine.compute(xBD, PRECISION)
        assertBigDecimalEquals(yBD, result, "cos($x) should be close to $y")
    }

    companion object {
        private val PRECISION = BigDecimal("0.0000001")
        private const val SCALE = 7
    }
}