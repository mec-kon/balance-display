import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import java.io.StringReader
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
     * In this method, a json string is loaded from a web page and then parsed into a klaxon json object.
     */
    private fun getKlaxonJsonObject(apiAddress: String): JsonObject {

        val url: String = URL(apiAddress).readText()
        val klaxon = Klaxon()

        return klaxon.parseJsonObject(StringReader(url))

    }

    /**
    * This method returns a Token object list with names and symbols
    */
    fun addNameSymbolAndIdToTokenList(tokenList: ArrayList<Token>): ArrayList<Token> {
        val jsonArray = getKlaxonJsonArray("https://api.coingecko.com/api/v3/coins/list")
        for (item in jsonArray){
            for (token in tokenList){
                if ((item["symbol"] as String).equals(token.name, ignoreCase = true)
                        || (item["name"] as String).equals(token.name, ignoreCase = true)
                        || (item["id"] as String).equals(token.name, ignoreCase = true)
                ){
                    token.name = item["name"] as String
                    token.symbol = item["symbol"] as String
                    token.id = item["id"] as String
                }
            }
        }
        return tokenList
    }

    fun addCoinValuesInEUR(tokenList: ArrayList<Token>): ArrayList<Token> {
        val apiLink = "https://api.coingecko.com/api/v3/simple/price?vs_currencies=eur&ids="

        for(token in tokenList){
            val jsonObject = getKlaxonJsonObject(apiLink+token.id)
            val price = jsonObject[token.id] as JsonObject
            token.priceInEUR = price["eur"] as Double
        }

        return tokenList
    }

    fun addCoinValuesInUSD(tokenList: ArrayList<Token>): ArrayList<Token> {
        val apiLink = "https://api.coingecko.com/api/v3/simple/price?vs_currencies=usd&ids="

        for(token in tokenList){
            val jsonObject = getKlaxonJsonObject(apiLink+token.id)
            val price = jsonObject[token.id] as JsonObject
            token.priceInUSD = price["usd"] as Double
        }

        return tokenList
    }
}