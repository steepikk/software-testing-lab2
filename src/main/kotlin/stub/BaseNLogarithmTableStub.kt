package stub

import function.Computable
import java.math.BigDecimal

class BaseNLogarithmTableStub(
    private val base: Int
) : CsvTwoColumnTableStub(
    resourcePath = null,
    fallbackFilePath = "plots/Log${base}.csv"
), Computable {

    override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
        require(x > BigDecimal.ZERO) { "Logarithm is defined only for x > 0" }
        require(base > 0) { "Logarithm base must be > 0" }
        require(base != 1) { "Logarithm base cannot be 1" }
        return super.compute(x, precision)
    }
}

