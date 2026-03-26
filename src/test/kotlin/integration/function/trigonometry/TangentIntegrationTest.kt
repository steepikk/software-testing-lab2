package integration.function.trigonometry

import function.trigonometry.Cosine
import function.trigonometry.Sine
import function.trigonometry.Tangent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
import kotlin.math.cos
import kotlin.math.sin

@ExtendWith(MockitoExtension::class)
class TangentIntegrationTest {

    @Mock
    private lateinit var mockSine: Sine

    @Mock
    private lateinit var mockCosine: Cosine

    @Spy
    private lateinit var spySine: Sine

    @Spy
    private lateinit var spyCosine: Cosine

    private lateinit var tangent: Tangent

    @BeforeEach
    fun setUp() {
        tangent = Tangent(spySine, spyCosine)
    }

    @Test
    @DisplayName("Tangent must call both sine and cosine during computation")
    fun shouldCallSineAndCosine() {
        val x = BigDecimal("1.5")

        tangent.compute(x, PRECISION)

        verify(spySine).compute(x, PRECISION.setScale(PRECISION.scale() + 5))
        verify(spyCosine).compute(x, PRECISION.setScale(PRECISION.scale() + 5))
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/function/trigonometry/tan.csv"], numLinesToSkip = 1)
    @DisplayName("Tangent(x) should equal sin(x)/cos(x) based on mocked delegates")
    fun shouldComputeCorrectResultFromMocks(x: Double, expected: Double) {
        val highPrecision = PRECISION.setScale(PRECISION.scale() + 5)

        val sinValue = BigDecimal(sin(x)).setScale(highPrecision.scale(), RoundingMode.HALF_EVEN)
        val cosValue = BigDecimal(cos(x)).setScale(highPrecision.scale(), RoundingMode.HALF_EVEN)

        val x = BigDecimal(x)
        val expected = BigDecimal(expected).setScale(SCALE, RoundingMode.HALF_EVEN)

        `when`(mockSine.compute(x, highPrecision)).thenReturn(sinValue)
        `when`(mockCosine.compute(x, highPrecision)).thenReturn(cosValue)

        tangent = Tangent(mockSine, mockCosine)
        val result = tangent.compute(x, PRECISION)

        assertEquals(expected, result)
    }

    @Test
    @DisplayName("Tangent should fail when cosine is zero")
    fun shouldFailOnUndefinedTangent() {
        val x = BigDecimal("1.570796")
        val highPrecision = PRECISION.setScale(PRECISION.scale() + 5)

        `when`(mockSine.compute(x, highPrecision)).thenReturn(BigDecimal("1.0"))
        `when`(mockCosine.compute(x, highPrecision)).thenReturn(BigDecimal.ZERO)

        tangent = Tangent(mockSine, mockCosine)

        val exception = assertThrows(
            java.lang.IllegalArgumentException::class.java
        ) {
            tangent.compute(x, PRECISION)
        }
        assertTrue(
            exception.message?.contains("не определен") ?: false
        )
    }

    private companion object {
        val PRECISION = BigDecimal("0.0000001")
        const val SCALE = 7
    }
}