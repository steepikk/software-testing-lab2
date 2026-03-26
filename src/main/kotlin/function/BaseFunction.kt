package function

import java.math.BigDecimal

abstract class BaseFunction : Computable {
    protected fun validate(x: BigDecimal, precision: BigDecimal) {
        require(precision > BigDecimal.ZERO && precision < BigDecimal.ONE) {
            "Точность должна быть между 0 и 1"
        }
    }

    companion object {
        const val MAX_ITERATIONS = 1000
    }
}