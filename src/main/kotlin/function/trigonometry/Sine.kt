package function.trigonometry

import function.BaseFunction
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.PI

class Sine : BaseFunction() {

    override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
        validatePrecision(precision)

        val mc = MathContext(minOf(precision.scale() + 2, 16), RoundingMode.HALF_EVEN)

        val normalizedX = normalizeToRange(x, mc)

        return computeTaylorSeries(normalizedX, precision, mc)
    }

    /**
     * Приводит угол к диапазону [-π, π] для улучшения сходимости ряда
     */
    private fun normalizeToRange(x: BigDecimal, mc: MathContext): BigDecimal {
        val pi = PI.toBigDecimal()
        val twoPi = pi.multiply(BigDecimal(2), mc)
        var normalized = x.remainder(twoPi, mc)

        if (normalized > pi) {
            normalized = normalized.subtract(twoPi, mc)
        } else if (normalized < pi.negate()) {
            normalized = normalized.add(twoPi, mc)
        }
        return normalized
    }

    /**
     * Вычисляет sin(x) с помощью ряда Тейлора:
     * sin(x) = x - x³/3! + x⁵/5! - x⁷/7! + ...
     */
    private fun computeTaylorSeries(x: BigDecimal, precision: BigDecimal, mc: MathContext): BigDecimal {
        val xSquared = x.multiply(x, mc)
        var result = x
        var term = x
        var iteration = 1
        val targetPrecision = precision.divide(BigDecimal.TEN, mc)

        while (term.abs() > targetPrecision && iteration < MAX_ITERATIONS) {
            term = term.multiply(xSquared, mc)
                .divide(BigDecimal.valueOf((2L * iteration) * (2L * iteration + 1)), mc)

            result = result.add(term * signForIteration(iteration), mc)

            iteration++
        }

        return result.setScale(precision.scale(), RoundingMode.HALF_EVEN)
    }

    /**
     * Возвращает знак для текущей итерации ряда
     * Четные итерации: отрицательный знак, нечетные: положительный
     */
    private fun signForIteration(iteration: Int): BigDecimal {
        return if (iteration and 1 == 1) BigDecimal(-1) else BigDecimal(1)
    }
}