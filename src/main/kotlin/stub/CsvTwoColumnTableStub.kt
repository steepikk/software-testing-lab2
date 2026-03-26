package stub

import function.BaseFunction
import java.io.File
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.charset.StandardCharsets

/**
 * Base class for table-backed stubs:
 * - loads values from a CSV with two columns: x,y (optional header line is ignored)
 * - rounds incoming x to the same scale as table keys
 * - returns table y rounded to requested precision scale
 */
abstract class CsvTwoColumnTableStub(
    private val resourcePath: String?, // classpath resource, e.g. "/function/trigonometry/sin.csv"
    private val fallbackFilePath: String?, // filesystem path, e.g. "plots/Sine.csv"
) : BaseFunction() {

    private val table: Map<BigDecimal, BigDecimal> by lazy { loadTable() }
    private val keyScale: Int by lazy { table.keys.firstOrNull()?.scale() ?: 0 }

    override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
        validatePrecision(precision)

        val roundedX = x.setScale(keyScale, RoundingMode.HALF_EVEN)
        val value = table[roundedX]
            ?: throw IllegalArgumentException("x=$x is out of table domain for ${this::class.simpleName}")

        return value.setScale(precision.scale(), RoundingMode.HALF_EVEN)
    }

    private fun loadTable(): Map<BigDecimal, BigDecimal> {
        val reader = openReader()
        reader.use { it ->
            val br = java.io.BufferedReader(it)
            return br.lineSequence()
                .map { line: String -> line.trim() }
                .filter { line: String -> line.isNotBlank() }
                .mapNotNull { line: String ->
                    val parts = line.split(',')
                    if (parts.size < 2) return@mapNotNull null

                    return@mapNotNull try {
                        val x = BigDecimal(parts[0].trim())
                        val y = BigDecimal(parts[1].trim())
                        x to y
                    } catch (_: NumberFormatException) {
                        // Header line like "x,y" is not parseable as BigDecimal.
                        null
                    }
                }
                .toMap()
        }
    }

    private fun openReader() = run {
        val resourcePathNonNull = resourcePath
        if (resourcePathNonNull != null) {
            val stream = javaClass.getResourceAsStream(resourcePathNonNull)
            if (stream != null) return@run InputStreamReader(stream, StandardCharsets.UTF_8)
        }
        val fallbackFilePathNonNull = fallbackFilePath
        if (fallbackFilePathNonNull != null) {
            val file = File(fallbackFilePathNonNull)
            require(file.exists()) { "Table file not found: ${file.absolutePath}" }
            return@run file.reader(StandardCharsets.UTF_8)
        }
        error("No resourcePath and no fallbackFilePath provided")
    }
}

