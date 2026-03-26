package util

import function.Computable
import java.io.File
import java.io.FileWriter
import java.math.BigDecimal
import java.util.Locale

class CSVGraphWriter(
    private val computable: Computable,
    private val outputDir: String,
    private val fileName: String
) {
    val filePath: String = File(outputDir, "$fileName.csv").path

    fun write(x1: BigDecimal, x2: BigDecimal, step: BigDecimal, precision: BigDecimal) {
        val file = File(filePath)
        file.parentFile?.mkdirs()
        FileWriter(file, false).use { writer ->
            writer.write("x, y\n")
            var x = x1
            while (x <= x2) {
                try {
                    val y = computable.compute(x, precision)
                    writer.write(String.format(Locale.ENGLISH, "%f,%f%n", x.toDouble(), y.toDouble()))
                } catch (e: Exception) {
                    writer.write(String.format(Locale.ENGLISH, "%f,%n", x.toDouble()))
                }
                x += step
            }
        }
    }
}