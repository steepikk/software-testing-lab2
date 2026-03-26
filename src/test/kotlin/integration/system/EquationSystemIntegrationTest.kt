package integration.system

import org.mockito.Mockito.mock
import function.log.BaseNLogarithm
import function.log.NaturalLogarithm
import function.trigonometry.Tangent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.spy
import org.junit.jupiter.api.Assertions.assertTrue
import system.EquationSystem
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ln
import kotlin.math.tan
import testutil.BigDecimalAssertions.assertBigDecimalEquals
import testutil.BigDecimalAssertions.assertBigDecimalCloseTo
import testutil.BigDecimalAssertions.assertBigDecimalCloseTo

@ExtendWith(MockitoExtension::class)
class EquationSystemIntegrationTest {

    private lateinit var spyTangent: Tangent

    private lateinit var spyLn: NaturalLogarithm

    private lateinit var spyLog2: BaseNLogarithm

    private lateinit var spyLog3: BaseNLogarithm

    private lateinit var spyLog5: BaseNLogarithm

    private lateinit var system: EquationSystem

    @BeforeEach
    fun setUp() {
        spyTangent = spy(Tangent())
        spyLn = spy(NaturalLogarithm())
        spyLog2 = spy(BaseNLogarithm(base = 2))
        spyLog3 = spy(BaseNLogarithm(base = 3))
        spyLog5 = spy(BaseNLogarithm(base = 5))

        system = EquationSystem(spyTangent, spyLn, spyLog2, spyLog3, spyLog5)
    }

    @Test
    @DisplayName("For x <= 0 system should call only tangent")
    fun shouldCallOnlyTangentForNonPositiveX() {
        val x = BigDecimal("-2.5")

        val result = system.compute(x, PRECISION)

        verify(spyTangent).compute(x, PRECISION)
        verifyNoInteractions(spyLn, spyLog2, spyLog3, spyLog5)

        val tanValue = Tangent().compute(x, PRECISION)
        val expected = tanValue.multiply(tanValue).setScale(PRECISION.scale(), RoundingMode.HALF_EVEN)
        val tolerance = BigDecimal("1E-6")
        assertBigDecimalCloseTo(expected, result, tolerance, "EquationSystem result should be close to expected")
    }

    @Test
    @DisplayName("For x = 0 system should call only tangent")
    fun shouldCallOnlyTangentForZero() {
        val x = BigDecimal.ZERO

        val result = system.compute(x, PRECISION)

        verify(spyTangent).compute(x, PRECISION)
        verifyNoInteractions(spyLn, spyLog2, spyLog3, spyLog5)

        assertBigDecimalEquals(BigDecimal.ZERO.setScale(PRECISION.scale(), RoundingMode.HALF_EVEN), result)
    }

    @Test
    @DisplayName("For x > 0 system should call all logarithmic functions")
    fun shouldCallAllLogFunctionsForPositiveX() {
        val x = BigDecimal("2.0")
        val highPrecision = PRECISION.setScale(PRECISION.scale() + 5, RoundingMode.HALF_EVEN)

        val result = system.compute(x, PRECISION)

        verify(spyLn).compute(x, highPrecision)
        verify(spyLog2).compute(x, highPrecision)
        verify(spyLog3).compute(x, highPrecision)
        verify(spyLog5).compute(x, highPrecision)
        verifyNoInteractions(spyTangent)

        val lnValue = NaturalLogarithm().compute(x, highPrecision)
        val log2Value = BaseNLogarithm(2).compute(x, highPrecision)
        val log3Value = BaseNLogarithm(3).compute(x, highPrecision)
        val log5Value = BaseNLogarithm(5).compute(x, highPrecision)

        val part1 = (log5Value * log3Value) / log2Value
        val part2 = part1 * lnValue
        val denominator = log2Value + lnValue + lnValue
        val part3 = part2 / denominator
        val expected = part3 * part3
            .setScale(PRECISION.scale(), RoundingMode.HALF_EVEN)

        val tolerance = BigDecimal("1E-5")
        assertBigDecimalCloseTo(
            expected,
            result,
            tolerance,
            "EquationSystem result should be close to expected"
        )
    }

    @Test
    @DisplayName("For x = 1 system should return zero without calling any functions")
    fun shouldReturnZeroForXEqualsOne() {
        val x = BigDecimal.ONE

        val result = system.compute(x, PRECISION)

        assertBigDecimalEquals(BigDecimal.ZERO.setScale(PRECISION.scale(), RoundingMode.HALF_EVEN), result)
        verifyNoInteractions(spyTangent, spyLn, spyLog2, spyLog3, spyLog5)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/system/equationSystem.csv"], numLinesToSkip = 1)
    @DisplayName("Integration test using mocked functions")
    fun shouldComputeCorrectlyWithMocks(x: BigDecimal, expected: BigDecimal) {
        val mockTangent = mock(Tangent::class.java)
        val mockLn = mock(NaturalLogarithm::class.java)
        val mockLog2 = mock(BaseNLogarithm::class.java)
        val mockLog3 = mock(BaseNLogarithm::class.java)
        val mockLog5 = mock(BaseNLogarithm::class.java)

        val highPrecision = PRECISION.setScale(PRECISION.scale() + 5, RoundingMode.HALF_EVEN)

        when {
            x <= BigDecimal.ZERO -> {
                val tanValue = BigDecimal.valueOf(tan(x.toDouble()))
                    .setScale(PRECISION.scale(), RoundingMode.HALF_EVEN)
                `when`(mockTangent.compute(x, PRECISION)).thenReturn(tanValue)
            }
            x.compareTo(BigDecimal.ONE) == 0 -> {
                // Для x = 1 никакие моки не нужны
            }
            else -> {
                // Для x > 0 и x != 1 настраиваем логарифмы
                val lnValue = BigDecimal.valueOf(ln(x.toDouble()))
                    .setScale(highPrecision.scale(), RoundingMode.HALF_EVEN)
                val log2Value = BigDecimal.valueOf(ln(x.toDouble()) / ln(2.0))
                    .setScale(highPrecision.scale(), RoundingMode.HALF_EVEN)
                val log3Value = BigDecimal.valueOf(ln(x.toDouble()) / ln(3.0))
                    .setScale(highPrecision.scale(), RoundingMode.HALF_EVEN)
                val log5Value = BigDecimal.valueOf(ln(x.toDouble()) / ln(5.0))
                    .setScale(highPrecision.scale(), RoundingMode.HALF_EVEN)

                `when`(mockLn.compute(x, highPrecision)).thenReturn(lnValue)
                `when`(mockLog2.compute(x, highPrecision)).thenReturn(log2Value)
                `when`(mockLog3.compute(x, highPrecision)).thenReturn(log3Value)
                `when`(mockLog5.compute(x, highPrecision)).thenReturn(log5Value)
            }
        }

        val systemWithMocks = EquationSystem(mockTangent, mockLn, mockLog2, mockLog3, mockLog5)

        val result = systemWithMocks.compute(x, PRECISION)

        val expectedScaled = expected.setScale(PRECISION.scale(), RoundingMode.HALF_EVEN)
        assertBigDecimalEquals(expectedScaled, result)
    }

    @Test
    @DisplayName("Should propagate exception when tangent fails")
    fun shouldPropagateTangentException() {
        val x = BigDecimal("-1.0")

        val mockTangent = mock(Tangent::class.java)
        `when`(mockTangent.compute(x, PRECISION))
            .thenThrow(IllegalArgumentException("Tangent is not defined"))

        val systemWithMock = EquationSystem(mockTangent, spyLn, spyLog2, spyLog3, spyLog5)

        val exception = assertThrows<IllegalArgumentException> {
            systemWithMock.compute(x, PRECISION)
        }

        assertTrue(exception.message?.contains("not defined") ?: false)
    }

    @Test
    @DisplayName("Should propagate exception when natural log fails")
    fun shouldPropagateLogarithmException() {
        val x = BigDecimal("2.0")
        val highPrecision = PRECISION.setScale(PRECISION.scale() + 5, RoundingMode.HALF_EVEN)

        val mockLn = mock(NaturalLogarithm::class.java)
        `when`(mockLn.compute(x, highPrecision))
            .thenThrow(IllegalArgumentException("Natural logarithm is not defined"))

        val systemWithMock = EquationSystem(spyTangent, mockLn, spyLog2, spyLog3, spyLog5)

        val exception = assertThrows<IllegalArgumentException> {
            systemWithMock.compute(x, PRECISION)
        }

        assertTrue(exception.message?.contains("not defined") ?: false)
    }

    private companion object {
        val PRECISION = BigDecimal("0.0000001")
    }
}