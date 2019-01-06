import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class InputFile {

    val inputData = getBalanceFromInput()

    private fun getBalanceFromInput(): Pair<String, ArrayList<Pair<String, Double>>> {
        return try {
            val inputFile = File("input.txt")
            val scanner = Scanner(inputFile)
            var innerBalance = arrayListOf<Pair<String, Double>>()
            var currency = "usd"

            while (scanner.hasNextLine()) {
                val inputLine = scanner.nextLine()
                if(!inputLine.isEmpty()){
                    val lineElements = inputLine.split(" ")
                    if (lineElements[0].equals("currency", true)) {
                        currency = lineElements[1]
                    } else {
                        innerBalance.add(Pair(lineElements[0], lineElements[1].toDouble()))
                    }
                }
            }
            Pair(currency, innerBalance)
        } catch (e: Exception) {
            println("file input.txt not found!")
            Pair("usd", arrayListOf())
        }

    }
}