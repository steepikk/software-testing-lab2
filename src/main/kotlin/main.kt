import function.log.BaseNLogarithm
import function.log.NaturalLogarithm
import function.trigonometry.Cosine
import function.trigonometry.Sine
import function.trigonometry.Tangent
import system.EquationSystem
import util.CSVGraphWriter
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

fun main() {
    val outputDir = System.getProperty("user.dir") + File.separator + "plots" + File.separator
    val precision = BigDecimal("0.0000001")
    val step = BigDecimal("0.01")

    val start = BigDecimal(-10).setScale(7, RoundingMode.HALF_EVEN)
    val end = BigDecimal(10).setScale(7, RoundingMode.HALF_EVEN)
    val logStart = BigDecimal("0.01")

    val functions = listOf(
        Triple(Sine(), "sin(x)", "Sine"),
        Triple(Cosine(), "cos(x)", "Cosine"),
        Triple(Tangent(), "tan(x)", "Tangent"),
        Triple(NaturalLogarithm(), "ln(x)", "NaturalLogarithm"),
        Triple(BaseNLogarithm(2), "log₂(x)", "Log2"),
        Triple(BaseNLogarithm(3), "log₃(x)", "Log3"),
        Triple(BaseNLogarithm(5), "log₅(x)", "Log5"),
        Triple(EquationSystem(), "f(x)", "EquationSystem")
    )

    functions.forEach { (computable, displayName, fileName) ->
        print("Генерация для $displayName")

        val writer = CSVGraphWriter(computable, outputDir, fileName)

        when (computable) {
            is NaturalLogarithm,
            is BaseNLogarithm -> writer.write(logStart, BigDecimal("10"), step, precision)
            else -> writer.write(start, end, step, precision)
        }

        println("→ $fileName.csv")
    }
    println("CSV файлы сохранены в: $outputDir")
}

