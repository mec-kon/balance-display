import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import java.io.StringReader
import java.lang.Exception
import java.net.URL

class Json {

    /**
     * In this method, a json string is loaded from a web page and then parsed into a klaxon json array.
     */
    private fun getKlaxonJsonArray(apiAddress: String): JsonArray<JsonObject> {

        val url: String = URL(apiAddress).readText()
        val klaxon = Klaxon()
        val parsed = klaxon.parseJsonArray(StringReader(url))

        return parsed as JsonArray<JsonObject>

    }

    /**
     * This method returns a json object that has the name or symbol you are searching for
     */
    private fun getToken(jsonArray: JsonArray<JsonObject>, symbolOrName: String): JsonObject? {
        for (item in jsonArray) {
            if ((item["symbol"] as String).equals(symbolOrName, ignoreCase = true)
                || (item["name"] as String).equals(symbolOrName, ignoreCase = true)
                || (item["id"] as String).equals(symbolOrName, ignoreCase = true)
            ) {
                return item
            }
        }
        return null
    }

    fun getCoinValuesInEuro(currentBalance: ArrayList<Pair<String, Double>>): ArrayList<Token> {

        val jsonArray = getKlaxonJsonArray("https://api.coinmarketcap.com/v1/ticker/?convert=EUR")

        var tokenBalance = arrayListOf<Token>()
        for (item in currentBalance) {
            val token = getToken(jsonArray, item.first)
            token?.let {
                val name = it["name"] as String
                val symbol = it["symbol"] as String
                val valueInEuro = (it["price_eur"] as String).toDouble()
                val balanceInEuro = Math.round(item.second * valueInEuro * 100.0) / 100.0
                tokenBalance.add(Token(name, symbol, valueInEuro, item.second, balanceInEuro))
            }
        }
        return tokenBalance
    }

    fun getCoinValuesInUSD(currentBalance: ArrayList<Pair<String, Double>>): ArrayList<Token> {

        val jsonArray = getKlaxonJsonArray("https://api.coinmarketcap.com/v1/ticker/")

        val tokenBalance = arrayListOf<Token>()
        for (item in currentBalance) {
            val token = getToken(jsonArray, item.first)
            token?.let {
                val name = it["name"] as String
                val symbol = it["symbol"] as String
                val valueInUSD = (it["price_usd"] as String).toDouble()
                val balanceInUSD = Math.round(item.second * valueInUSD * 100.0) / 100.0
                tokenBalance.add(Token(name, symbol, valueInUSD, item.second, balanceInUSD))
            }
        }
        return tokenBalance
    }
}