package integration.stubs

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.api.BeforeEach
import stub.EquationSystemTableStub
import java.math.BigDecimal
import java.math.RoundingMode
import testutil.BigDecimalAssertions.assertBigDecimalCloseTo
import testutil.BigDecimalAssertions.assertBigDecimalEquals

class EquationSystemStubIntegrationTest {

    private lateinit var stub: EquationSystemTableStub
    private val precision = BigDecimal("0.0000001")
    private val tolerance = BigDecimal("1E-5")

    @BeforeEach
    fun setUp() {
        stub = EquationSystemTableStub()
    }

    @ParameterizedTest(name = "f(x) from table for x={0}")
    @CsvFileSource(resources = ["/system/equationSystem.csv"], numLinesToSkip = 1)
    @DisplayName("EquationSystemTableStub should match equationSystem table for listed x values")
    fun shouldMatchEquationSystemTable(x: Double, expected: Double) {
        val xBD = BigDecimal(x.toString())
        val expectedBD = BigDecimal(expected).setScale(precision.scale(), RoundingMode.HALF_EVEN)

        val actual = stub.compute(xBD, precision)
        assertBigDecimalCloseTo(expectedBD, actual, tolerance, "Table value mismatch for x=$x")
    }
}

