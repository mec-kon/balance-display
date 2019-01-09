import tornadofx.Controller
import java.io.FileNotFoundException

class Token(
    val name: String,
    val symbol: String,
    val valueInEuro: Double,
    val balance: Double,
    val balanceInEuroOrUSD: Double
)

class BalanceController : Controller() {
    private val file: InputFile = InputFile()
    private var inputData: Pair<String, ArrayList<Pair<String, Double>>>
    private val json = Json()
    private var usdIsUsed: Boolean
    var inputFileNotFound = false
    var currencyName: String
    var coinValues = arrayListOf<Token>()
    var totalBalance = 0.0

    init {
        inputData = try {
            file.getBalanceFromInput()
        } catch (exception: Exception) {
            inputFileNotFound = true
            Pair("usd", arrayListOf())
        }

        if (inputData.first.equals("usd", ignoreCase = true)) {
            currencyName = "USD"
            usdIsUsed = true

        } else {
            currencyName = "Euro"
            usdIsUsed = false
        }
    }

    fun refresh() {
        if (inputFileNotFound){
            throw FileNotFoundException("ile input.txt not found!")
        }
        coinValues.clear()
        coinValues = loadCoinValues()
        totalBalance = calculateTotalBalance(coinValues)
    }

    private fun loadCoinValues(): ArrayList<Token> {
        coinValues = if (usdIsUsed) {
            json.getCoinValuesInUSD(inputData.second)
        } else {
            json.getCoinValuesInEuro(inputData.second)

        }
        coinValues.sortByDescending { it.balanceInEuroOrUSD }
        return coinValues
    }

    private fun calculateTotalBalance(coinValues: ArrayList<Token>): Double {
        var totalBalance = 0.0
        for (token in coinValues) {
            totalBalance += token.balanceInEuroOrUSD
        }
        return Math.round(totalBalance * 100.0) / 100.0
    }

}