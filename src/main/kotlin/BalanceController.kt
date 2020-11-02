import tornadofx.Controller
import java.io.FileNotFoundException
import java.net.ConnectException

class Token(
    var name: String, var balance: Double
){

    var id: String = ""
    var symbol: String = ""
    var balanceInUSD: Double = 0.0
    var balanceInEUR: Double = 0.0
    var priceInEUR: Double = 0.0
    var priceInUSD: Double = 0.0

}

class BalanceController : Controller() {
    private val file: InputFile = InputFile()
    private var inputData: Pair<String, ArrayList<Pair<String, Double>>>
    private val json = Json()

    var inputFileNotFound = false
    private var apiNotReachable = false

    var totalBalance = 0.0
    var usdIsUsed: Boolean
    var tokenList = arrayListOf<Token>()

    init {
        inputData = try {
            file.getBalanceFromInput()
        } catch (exception: Exception) {
            inputFileNotFound = true
            Pair("usd", arrayListOf())
        }

        usdIsUsed = inputData.first.equals("usd", ignoreCase = true)
        try {
            createTokenList()
        }catch (exeption: Exception){
           apiNotReachable = true
        }
    }

    fun refresh() {
        if (inputFileNotFound){
            throw FileNotFoundException("File input.txt not found!")
        }
        if(apiNotReachable){
            throw ConnectException("Error! no api connection possible ")
        }
        tokenList.clear()
        createTokenList()
        loadCoinValues()
        calculateBalanceInFiatCurrency()
        totalBalance = calculateTotalBalance()

    }

    private fun createTokenList(){
        for (item in inputData.second){
            tokenList.add(Token(item.first, item.second))
        }
        tokenList = json.addNameSymbolAndIdToTokenList(tokenList)
    }


    private fun loadCoinValues(){
        if (usdIsUsed) {
            tokenList = json.addCoinValuesInUSD(tokenList)
            tokenList.sortByDescending { it.balanceInUSD }

        } else {
            tokenList = json.addCoinValuesInEUR(tokenList)
            tokenList.sortByDescending { it.balanceInEUR }

        }
    }

    /**
     * calculates the balance of each cryptocurrency in EUR or USD
     */
    private fun calculateBalanceInFiatCurrency(){
        for (token in tokenList){
            if (usdIsUsed){
                token.balanceInUSD = Math.round(token.balance * token.priceInUSD * 100.0) / 100.0
            }
            else {
                token.balanceInEUR = Math.round(token.balance * token.priceInEUR * 100.0) / 100.0
            }
        }
    }

    private fun calculateTotalBalance(): Double {
        var totalBalance = 0.0
        for (token in tokenList) {
            if (usdIsUsed){
                totalBalance += token.balanceInUSD
            }
            else {
                totalBalance += token.balanceInEUR
            }

        }
        return Math.round(totalBalance * 100.0) / 100.0
    }

}