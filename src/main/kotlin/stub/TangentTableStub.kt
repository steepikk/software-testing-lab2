package stub

import function.Computable

class TangentTableStub : CsvTwoColumnTableStub(
    resourcePath = "/function/trigonometry/tan.csv",
    fallbackFilePath = "plots/Tangent.csv"
), Computable

