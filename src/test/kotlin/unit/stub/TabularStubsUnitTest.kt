package unit.stub

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import stub.BaseNLogarithmTableStub
import stub.CosineTableStub
import stub.NaturalLogarithmTableStub
import stub.SineTableStub
import stub.TangentTableStub
import stub.EquationSystemTableStub
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI
import testutil.BigDecimalAssertions.assertBigDecimalEquals
import org.junit.jupiter.api.assertThrows

class TabularStubsUnitTest {

    private val precision = BigDecimal("0.0000001") // scale=7
    private val expectedScale = 7

    @ParameterizedTest
    @CsvFileSource(resources = ["/function/trigonometry/sin.csv"], numLinesToSkip = 0, delimiter = ',')
    fun `SineTableStub should return values from sin table`(x: Double, y: Double) {
        val stub = SineTableStub()
        val xBD = BigDecimal(x.toString())
        val expected = BigDecimal(y.toString()).setScale(expectedScale, RoundingMode.HALF_EVEN)

        val result = stub.compute(xBD, precision)
        assertBigDecimalEquals(expected, result)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/function/trigonometry/cos.csv"], numLinesToSkip = 0, delimiter = ',')
    fun `CosineTableStub should return values from cos table`(x: Double, y: Double) {
        val stub = CosineTableStub()
        val xBD = BigDecimal(x.toString())
        val expected = BigDecimal(y.toString()).setScale(expectedScale, RoundingMode.HALF_EVEN)

        val result = stub.compute(xBD, precision)
        assertBigDecimalEquals(expected, result)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/function/trigonometry/tan.csv"], numLinesToSkip = 0, delimiter = ',')
    fun `TangentTableStub should return values from tan table`(x: Double, y: Double) {
        val stub = TangentTableStub()
        val xBD = BigDecimal(x.toString())
        val expected = BigDecimal(y.toString()).setScale(expectedScale, RoundingMode.HALF_EVEN)

        val result = stub.compute(xBD, precision)
        assertBigDecimalEquals(expected, result)
    }

    @Test
    fun `NaturalLogarithmTableStub should throw for nonPositiveX`() {
        val stub = NaturalLogarithmTableStub()
        assertThrows<IllegalArgumentException> {
            stub.compute(BigDecimal.ZERO, precision)
        }
    }

    @Test
    fun `NaturalLogarithmTableStub should match table for a few reference points`() {
        val stub = NaturalLogarithmTableStub()
        val expected = readYFromPlotsTable("NaturalLogarithm.csv", BigDecimal("0.01"))
        val result = stub.compute(BigDecimal("0.01"), precision)
        assertBigDecimalEquals(expected.setScale(expectedScale, RoundingMode.HALF_EVEN), result)
    }

    @Test
    fun `BaseNLogarithmTableStub should match table for log2 of 2`() {
        val stub = BaseNLogarithmTableStub(base = 2)
        val expected = readYFromPlotsTable("Log2.csv", BigDecimal("2.0"))
        val result = stub.compute(BigDecimal("2.0"), precision)
        assertBigDecimalEquals(expected.setScale(expectedScale, RoundingMode.HALF_EVEN), result)
    }

    @Test
    fun `EquationSystemTableStub should match equationSystem table for a few points`() {
        val stub = EquationSystemTableStub()
        val expected0 = readYFromPlotsTable("EquationSystem.csv", BigDecimal("0.0"))
        val result0 = stub.compute(BigDecimal("0.0"), precision)
        assertBigDecimalEquals(expected0.setScale(expectedScale, RoundingMode.HALF_EVEN), result0)
    }

    @Test
    fun `TangentTableStub should throw when x is out of table domain near piOver2`() {
        val stub = TangentTableStub()
        val piHalf = BigDecimal(PI.toString()).divide(BigDecimal("2"), expectedScale, RoundingMode.HALF_EVEN)
        assertThrows<IllegalArgumentException> {
            stub.compute(piHalf, precision)
        }
    }

    private fun readYFromPlotsTable(fileName: String, x: BigDecimal): BigDecimal {
        val file = File("plots/$fileName")
        require(file.exists()) { "Missing table file: ${file.absolutePath}" }

        file.readLines()
            .drop(1) // header
            .forEach { line ->
                if (line.isBlank()) return@forEach
                val parts = line.split(',')
                if (parts.size < 2) return@forEach
                val xKey = BigDecimal(parts[0].trim())
                if (xKey.compareTo(x) == 0) {
                    return BigDecimal(parts[1].trim())
                }
            }

        error("x=$x not found in plots/$fileName")
    }
}

