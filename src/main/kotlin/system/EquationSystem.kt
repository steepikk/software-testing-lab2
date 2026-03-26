package system

import function.BaseFunction
import function.log.BaseNLogarithm
import function.log.NaturalLogarithm
import function.trigonometry.Tangent
import java.math.BigDecimal
import java.math.RoundingMode

class EquationSystem(
    private val tangent: Tangent = Tangent(),
    private val ln: NaturalLogarithm = NaturalLogarithm(),
    private val log2: BaseNLogarithm = BaseNLogarithm(2, NaturalLogarithm()),
    private val log3: BaseNLogarithm = BaseNLogarithm(3, NaturalLogarithm()),
    private val log5: BaseNLogarithm = BaseNLogarithm(5, NaturalLogarithm())
) : BaseFunction() {

    override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
        validatePrecision(precision)

        if (x.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ZERO.setScale(precision.scale(), RoundingMode.HALF_EVEN)
        }

        return if (x <= BigDecimal.ZERO) {
            val tanValue = tangent.compute(x, precision)
            (tanValue * tanValue).setScale(precision.scale(), RoundingMode.HALF_EVEN)
        } else {
            val highPrecision = precision.setScale(precision.scale() + 5, RoundingMode.HALF_EVEN)

            val log5x = log5.compute(x, highPrecision)
            val log3x = log3.compute(x, highPrecision)
            val log2x = log2.compute(x, highPrecision)
            val lnx = ln.compute(x, highPrecision)

            val part1 = (log5x * log3x) / log2x
            val part2 = part1 * lnx
            val denominator = log2x + lnx + lnx
            val part3 = part2 / denominator
            val result = part3 * part3

            result.setScale(precision.scale(), RoundingMode.HALF_EVEN)
        }
    }
}
