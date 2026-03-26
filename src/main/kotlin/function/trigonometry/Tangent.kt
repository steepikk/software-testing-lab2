package function.trigonometry

import function.BaseFunction
import java.math.BigDecimal
import java.math.RoundingMode

class Tangent(
    private val sine: Sine = Sine(),
    private val cosine: Cosine = Cosine(),
) : BaseFunction() {

    override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
        validatePrecision(precision)

        val highPrecision = precision.setScale(precision.scale() + 5, RoundingMode.HALF_EVEN)

        val sinValue = sine.compute(x, highPrecision)
        val cosValue = cosine.compute(x, highPrecision)

        require(cosValue.abs() >= precision) {
            "Tangent is not defined for x = $x (cos(x) is approximately 0)"
        }

        return sinValue.divide(cosValue, precision.scale(), RoundingMode.HALF_EVEN)
    }
}