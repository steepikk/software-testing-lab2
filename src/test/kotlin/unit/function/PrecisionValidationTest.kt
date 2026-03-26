package unit.function

import function.log.BaseNLogarithm
import function.log.NaturalLogarithm
import function.trigonometry.Cosine
import function.trigonometry.Sine
import function.trigonometry.Tangent
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import system.EquationSystem
import java.math.BigDecimal
import java.util.stream.Stream

@DisplayName("Precision validation")
class PrecisionValidationTest {

    private val trigX = BigDecimal("0.5")
    private val logX = BigDecimal("2.0")

    @ParameterizedTest(name = "Sine should reject invalid precision: {0}")
    @MethodSource("invalidPrecisions")
    fun `Sine should reject invalid precision`(description: String, precision: BigDecimal) {
        assertThrows<IllegalArgumentException> {
            Sine().compute(trigX, precision)
        }
    }

    @ParameterizedTest(name = "Cosine should reject invalid precision: {0}")
    @MethodSource("invalidPrecisions")
    fun `Cosine should reject invalid precision`(description: String, precision: BigDecimal) {
        assertThrows<IllegalArgumentException> {
            Cosine().compute(trigX, precision)
        }
    }

    @ParameterizedTest(name = "Tangent should reject invalid precision: {0}")
    @MethodSource("invalidPrecisions")
    fun `Tangent should reject invalid precision`(description: String, precision: BigDecimal) {
        assertThrows<IllegalArgumentException> {
            Tangent().compute(trigX, precision)
        }
    }

    @ParameterizedTest(name = "NaturalLogarithm should reject invalid precision: {0}")
    @MethodSource("invalidPrecisions")
    fun `NaturalLogarithm should reject invalid precision`(description: String, precision: BigDecimal) {
        assertThrows<IllegalArgumentException> {
            NaturalLogarithm().compute(logX, precision)
        }
    }

    @ParameterizedTest(name = "BaseNLogarithm should reject invalid precision: {0}")
    @MethodSource("invalidPrecisions")
    fun `BaseNLogarithm should reject invalid precision`(description: String, precision: BigDecimal) {
        assertThrows<IllegalArgumentException> {
            BaseNLogarithm(base = 2).compute(logX, precision)
        }
    }

    @ParameterizedTest(name = "EquationSystem should reject invalid precision: {0}")
    @MethodSource("invalidPrecisions")
    fun `EquationSystem should reject invalid precision`(description: String, precision: BigDecimal) {
        assertThrows<IllegalArgumentException> {
            EquationSystem().compute(logX, precision)
        }
    }

    companion object {
        @JvmStatic
        private fun invalidPrecisions(): Stream<Arguments> = Stream.of(
            Arguments.of("zero", BigDecimal.ZERO),
            Arguments.of("one", BigDecimal.ONE),
            Arguments.of("negative", BigDecimal("-0.1")),
            Arguments.of("greaterThanOne", BigDecimal("1.1"))
        )
    }
}

