package function.log

import function.BaseFunction
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class BaseNLogarithm(
    private val base: Int = 10,
    private val naturalLog: NaturalLogarithm = NaturalLogarithm()
) : BaseFunction() {

    init {
        validateBase()
    }

    override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
        validate(x, precision)
        return computeLogarithm(x, precision)
    }

    /**
     * Проверка корректности основания: base > 0 и base ≠ 1
     */
    private fun validateBase() {
        require(base > 0) { "Основание логарифма должно быть > 0, текущее: $base" }
        require(base != 1) { "Основание логарифма не может быть 1, текущее: $base" }
    }

    /**
     * Основная логика вычисления log_base(x) = ln(x) / ln(base)
     */
    private fun computeLogarithm(x: BigDecimal, precision: BigDecimal): BigDecimal {
        val highPrecision = getHighPrecisionContext(precision)
        val lnX = naturalLog.compute(x, highPrecision)
        val lnBase = computeLnBase(highPrecision)
        return divideWithPrecision(lnX, lnBase, precision)
    }

    /**
     * Получение высокоточного контекста для промежуточных вычислений
     */
    private fun getHighPrecisionContext(precision: BigDecimal): BigDecimal {
        return precision.setScale(precision.scale() + 5, RoundingMode.HALF_EVEN)
    }

    /**
     * Вычисление ln(base) с высокой точностью
     */
    private fun computeLnBase(highPrecision: BigDecimal): BigDecimal {
        return naturalLog.compute(BigDecimal(base), highPrecision)
    }

    /**
     * Деление ln(x) / ln(base) с нужной точностью
     */
    private fun divideWithPrecision(lnX: BigDecimal, lnBase: BigDecimal, precision: BigDecimal): BigDecimal {
        val mathContext = MathContext(precision.scale() + 5, RoundingMode.HALF_EVEN)
        val result = lnX.divide(lnBase, mathContext)
        return result.setScale(precision.scale(), RoundingMode.HALF_EVEN)
    }
}