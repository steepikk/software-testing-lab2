package unit.util

import function.Computable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import util.CSVGraphWriter
import java.io.File
import java.math.BigDecimal
import java.nio.file.Path
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class CSVGraphWriterTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var outputFile: File

    @BeforeEach
    fun setUp() {
        outputFile = tempDir.resolve("test.csv").toFile()
    }

    @AfterEach
    fun tearDown() {
        outputFile.delete()
    }

    @Test
    fun `should write correct values for valid computable`() {
        val computable = stubComputable()
        val writer = CSVGraphWriter(computable, tempDir.toString(), "test")
        val x1 = BigDecimal("1.0")
        val x2 = BigDecimal("3.0")
        val step = BigDecimal("1.0")
        val precision = BigDecimal("1E-6")

        writer.write(x1, x2, step, precision)

        assertTrue(outputFile.exists(), "Файл должен быть создан")

        val lines = outputFile.readLines()
        assertEquals(4, lines.size, "Должно быть 4 строки: заголовок + 3 значения")

        assertEquals("x, y", lines[0])
        assertEquals("1.000000,2.000000", lines[1])
        assertEquals("2.000000,4.000000", lines[2])
        assertEquals("3.000000,6.000000", lines[3])
    }

    @Test
    fun `should write x only when computation fails`() {
        val computable = failingComputable()
        val writer = CSVGraphWriter(computable, tempDir.toString(), "test")
        val x1 = BigDecimal("1.0")
        val x2 = BigDecimal("2.0")
        val step = BigDecimal("1.0")
        val precision = BigDecimal("1E-6")

        writer.write(x1, x2, step, precision)

        assertTrue(outputFile.exists())

        val lines = outputFile.readLines()
        assertEquals(3, lines.size)

        assertEquals("x, y", lines[0])
        assertEquals("1.000000,", lines[1])
        assertEquals("2.000000,", lines[2])
    }

    @Test
    fun `should create parent directory if not exists`() {
        val nestedDir = tempDir.resolve("plots").toString()
        val writer = CSVGraphWriter(stubComputable(), nestedDir, "nested")
        val file = File(writer.filePath)

        try {
            writer.write(
                BigDecimal("1.0"),
                BigDecimal("2.0"),
                BigDecimal("1.0"),
                BigDecimal("1E-6")
            )

            assertTrue(file.exists(), "Файл должен быть создан")
            assertEquals(file.parentFile?.exists(), true, "Папка plots должна быть создана")
        } finally {
            file.delete()
            file.parentFile?.delete()
        }
    }


    private fun stubComputable(): Computable = object : Computable {
        override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
            return x.multiply(BigDecimal("2"))
        }
    }

    private fun failingComputable(): Computable = object : Computable {
        override fun compute(x: BigDecimal, precision: BigDecimal): BigDecimal {
            throw ArithmeticException("Simulated error")
        }
    }
}