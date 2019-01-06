import tornadofx.Controller

class BalanceController : Controller() {
    private val file: InputFile = InputFile()
    private val inputData = file.inputData
    private val json = Json()
    var currencyName: String
    var usdIsUsed: Boolean
    var coinValues = arrayListOf<Token>()

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

        return coinValues
    }

}