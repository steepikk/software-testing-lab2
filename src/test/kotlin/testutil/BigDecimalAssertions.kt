package testutil

import org.junit.jupiter.api.Assertions.assertEquals
import java.math.BigDecimal


object BigDecimalAssertions {
    fun assertBigDecimalEquals(
        expected: BigDecimal,
        actual: BigDecimal,
        message: String? = null
    ) {
        assertEquals(0, expected.compareTo(actual), message)
    }

    fun assertBigDecimalCloseTo(
        expected: BigDecimal,
        actual: BigDecimal,
        tolerance: BigDecimal,
        message: String? = null
    ) {
        val diff = expected.subtract(actual).abs()
        val condition = diff <= tolerance
        val fullMessage = buildString {
            append(message ?: "Values are not close enough")
            append(". expected=")
            append(expected)
            append(", actual=")
            append(actual)
            append(", diff=")
            append(diff)
            append(", tolerance=")
            append(tolerance)
        }
        assertEquals(true, condition, fullMessage)
    }
}

