package integration.function.log

import function.log.BaseNLogarithm
import function.log.NaturalLogarithm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ln

@ExtendWith(MockitoExtension::class)
class LogIntegrationTest {

    @Mock
    private lateinit var mockNaturalLog: NaturalLogarithm

    @Spy
    private lateinit var spyNaturalLog: NaturalLogarithm

    private lateinit var logBase5: BaseNLogarithm

    @BeforeEach
    fun setUp() {
        logBase5 = BaseNLogarithm(base = 5, naturalLog = spyNaturalLog)
    }

    @Test
    @DisplayName("BaseNLogarithm should call NaturalLogarithm for ln(x) and ln(5)")
    fun shouldCallNaturalLogarithmForXAndBase() {
        val x = BigDecimal("25")
        val highPrecision = PRECISION.setScale(PRECISION.scale() + 5, RoundingMode.HALF_EVEN)

        logBase5.compute(x, PRECISION)

        verify(spyNaturalLog).compute(x, highPrecision)
        verify(spyNaturalLog).compute(BigDecimal("5"), highPrecision)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/function/log/log5.csv"], numLinesToSkip = 1)
    @DisplayName("BaseNLogarithm with base 5 should return correct log5(x) from CSV")
    fun shouldComputeLog5Correctly(x: Double, expected: Double) {
        val bigX = BigDecimal(x)
        val expectedValue = BigDecimal(expected).setScale(SCALE, RoundingMode.HALF_EVEN)
        val highPrecision = PRECISION.setScale(PRECISION.scale() + 5, RoundingMode.HALF_EVEN)

        val lnX = BigDecimal(ln(x)).setScale(highPrecision.scale(), RoundingMode.HALF_EVEN)
        val lnBase = BigDecimal(ln(5.0)).setScale(highPrecision.scale(), RoundingMode.HALF_EVEN)

        `when`(mockNaturalLog.compute(bigX, highPrecision)).thenReturn(lnX)
        `when`(mockNaturalLog.compute(BigDecimal("5"), highPrecision)).thenReturn(lnBase)

        logBase5 = BaseNLogarithm(base = 5, naturalLog = mockNaturalLog)

        val result = logBase5.compute(bigX, PRECISION)

        assertEquals(expectedValue, result)
    }

    @Test
    @DisplayName("BaseNLogarithm should fail when x <= 0")
    fun shouldFailOnInvalidArgument() {
        val x = BigDecimal.ZERO
        val highPrecision = PRECISION.setScale(PRECISION.scale() + 5, RoundingMode.HALF_EVEN)

        `when`(mockNaturalLog.compute(x, highPrecision))
            .thenThrow(IllegalArgumentException("Натуральный логарифм не определен при x = 0"))

        logBase5 = BaseNLogarithm(base = 5, naturalLog = mockNaturalLog)

        val exception = assertThrows<IllegalArgumentException> {
            logBase5.compute(x, PRECISION)
        }

        assertTrue(exception.message?.contains("не определен") ?: false)
    }

    private companion object {
        val PRECISION = BigDecimal("0.0000001")
        const val SCALE = 7
    }
}