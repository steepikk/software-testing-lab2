package function.trigonometry

import function.BaseFunction
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class Cosine(
    private val sine: Sine = Sine(),
) : BaseFunction() {

    override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
        validatePrecision(precision)
        val mathContext = MathContext(precision.scale() + 2, RoundingMode.HALF_EVEN)
        val piHalf = getPiHalf(mathContext)
        val argument = piHalf - x

        return sine.compute(argument, precision)
    }

    private fun getPiHalf(mc: MathContext): BigDecimal {
        val pi = BigDecimal(Math.PI, mc)
        return pi / BigDecimal(2)
    }
}