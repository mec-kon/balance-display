import tornadofx.Controller

class Token(
    val name: String,
    val symbol: String,
    val valueInEuro: Double,
    val balance: Double,
    val balanceInEuroOrUSD: Double
)

class BalanceController : Controller() {
    private val file: InputFile = InputFile()
    private val inputData = file.inputData
    private val json = Json()
    private var usdIsUsed: Boolean
    var currencyName: String
    var coinValues = arrayListOf<Token>()
    var totalBalance = 0.0

    init {
        if (inputData.first.equals("usd", true)) {
            currencyName = "USD"
            usdIsUsed = true

        } else {
            currencyName = "Euro"
            usdIsUsed = false
        }
    }

    fun loadCoinValues(): ArrayList<Token> {
        coinValues = if (usdIsUsed) {
            json.getCoinValuesInUSD(inputData.second)
        } else {
            json.getCoinValuesInEuro(inputData.second)

        }
        coinValues.sortByDescending { it.balanceInEuroOrUSD }
        return coinValues
    }

    fun calculateTotalBalance(coinValues: ArrayList<Token>): Double {
        var totalBalance = 0.0
        for (token in coinValues){
            totalBalance += token.balanceInEuroOrUSD
        }
        return totalBalance
    }

}