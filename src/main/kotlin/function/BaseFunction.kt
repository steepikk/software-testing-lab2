package function

import java.math.BigDecimal

abstract class BaseFunction : Computable {
    protected fun validatePrecision(precision: BigDecimal) {
        require(precision > BigDecimal.ZERO && precision < BigDecimal.ONE) {
            "Precision must be greater than 0 and less than 1. Current: $precision"
        }
    }

    companion object {
        const val MAX_ITERATIONS = 1000
    }
}