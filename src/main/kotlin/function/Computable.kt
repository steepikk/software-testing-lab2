package function

import java.math.BigDecimal

interface Computable {
    fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal
}