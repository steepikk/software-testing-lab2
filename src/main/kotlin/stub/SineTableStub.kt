package stub

import function.Computable
import java.math.BigDecimal

class SineTableStub : CsvTwoColumnTableStub(
    resourcePath = "/function/trigonometry/sin.csv",
    fallbackFilePath = "plots/Sine.csv"
), Computable

