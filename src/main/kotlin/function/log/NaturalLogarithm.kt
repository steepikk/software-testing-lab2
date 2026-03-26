package function.log

import function.BaseFunction
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class NaturalLogarithm : BaseFunction() {

    override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
        validate(x, precision)

        require(x > BigDecimal.ZERO) {
            "Натуральный логарифм не определен при x = $x"
        }

        return when {
            x == BigDecimal.ONE -> handleOne(precision)
            else -> computeUsingSeries(x, precision)
        }
    }

    /**
     * Обработка случая x = 1 → ln(1) = 0
     */
    private fun handleOne(precision: BigDecimal): BigDecimal {
        return BigDecimal.ZERO.setScale(precision.scale(), RoundingMode.HALF_EVEN)
    }

    /**
     * Основной расчёт ln(x) через ряд при x ≠ 1
     */
    private fun computeUsingSeries(x: BigDecimal, precision: BigDecimal): BigDecimal {
        val mc = MathContext(precision.scale() + 10, RoundingMode.HALF_EVEN)
        val z = computeZ(x, mc)
        return computeTaylorSeries(z, mc).scaleToPrecision(precision)
    }

    /**
     * Вычисление z = (x - 1) / (x + 1)
     */
    private fun computeZ(x: BigDecimal, mc: MathContext): BigDecimal {
        val numerator = x - BigDecimal.ONE
        val denominator = x + BigDecimal.ONE
        return numerator.divide(denominator, mc)
    }

    /**
     * Вычисление ряда: artanh(z) = z + z³/3 + z⁵/5 + ...
     * ln(x) = 2 * artanh(z)
     */
    private fun computeTaylorSeries(z: BigDecimal, mc: MathContext): BigDecimal {
        val zSquared = z.multiply(z, mc)
        var result = BigDecimal.ZERO
        var term = z
        var n = 1

        while (term.abs() > BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.precision), mc) &&
            n < MAX_ITERATIONS
        ) {
            result = result.add(term.divide(BigDecimal(n), mc), mc)
            term = term.multiply(zSquared, mc)
            n += 2
        }

        return result.multiply(BigDecimal(2), mc)
    }

    /**
     * Приведение результата к требуемой точности (масштабу)
     */
    private fun BigDecimal.scaleToPrecision(precision: BigDecimal): BigDecimal {
        return this.setScale(precision.scale(), RoundingMode.HALF_EVEN)
    }
}