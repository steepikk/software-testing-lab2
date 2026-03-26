package stub

import function.Computable
import java.math.BigDecimal

class NaturalLogarithmTableStub : CsvTwoColumnTableStub(
    resourcePath = null,
    fallbackFilePath = "plots/NaturalLogarithm.csv"
), Computable {
    override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
        require(x > BigDecimal.ZERO) { "Natural logarithm is defined only for x > 0" }
        return super.compute(x, precision)
    }
}

