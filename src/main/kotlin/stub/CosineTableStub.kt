package stub

import function.Computable

class CosineTableStub : CsvTwoColumnTableStub(
    resourcePath = "/function/trigonometry/cos.csv",
    fallbackFilePath = "plots/Cosine.csv"
) , Computable

