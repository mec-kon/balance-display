import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import tornadofx.*
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception


/**
 * The charts displayed in the program are declared in this class.
 */
class ChartView : View() {
    private val controller: BalanceController by inject()

    override val root = VBox()

    init {
        with(root) {

            piechart("Balance in ${controller.currencyName}") {
                for (item in controller.coinValues) {
                    data(item.name, item.balanceInEuroOrUSD)
                }
            }
            barchart("", CategoryAxis(), NumberAxis()) {
                series("Balance in ${controller.currencyName}") {
                    for (item in controller.coinValues) {
                        data(item.name, item.balanceInEuroOrUSD)
                    }
                }

                series("Balance in coins") {
                    for (item in controller.coinValues) {
                        data(item.name, item.balance)
                    }
                }

            }

            minWidth = 500.0

        }
    }

}

/**
 * The table displayed in the program is declared in this class.
 */
class BalanceTableAndTotalBalanceView : View() {
    private val controller: BalanceController by inject()
    private val customViewModel: CustomViewModel by inject()

    override val root = borderpane {

        center = tableview(customViewModel.token) {
            readonlyColumn("Name", Token::name).fixedWidth(100)
            readonlyColumn("Symbol", Token::symbol).fixedWidth(80)
            readonlyColumn("Value in ${controller.currencyName}", Token::valueInEuro).fixedWidth(130)
            readonlyColumn("Balance", Token::balance).fixedWidth(80)
            readonlyColumn("Balance in ${controller.currencyName}", Token::balanceInEuroOrUSD).fixedWidth(110)
            minWidth = 500.0
            paddingLeft = 10.0
        }

        bottom = stackpane {
            label("total balance: ${controller.totalBalance} ${controller.currencyName}")
            paddingAll = 20.0
        }

    }

}

/**
 * This is the view of the loading screen
 */
class ProgressBarView : View() {
    private val status: TaskStatus by inject()

    override val root = StackPane()

    init {
        with(root) {
            progressbar(status.progress)
            label(status.message)
        }
    }
}

/**
 * This view is displayed if there is an error while loading the json string
 */
class FailView : View() {
    override val root = StackPane()

    fun connectionError() {
        root.add(label("Connection error: Please check your internet connection or try again later."))
    }
    fun inputError() {
        root.add(label("File input.txt not found!"))
    }
}

/**
 * This is the view displayed after the loading screen.
 * It displays the table and charts.
 */
class MainDisplayView : View() {
    override val root = borderpane {
        left<BalanceTableAndTotalBalanceView>()
        center<ChartView>()
    }
}

/**
 * This class is used to load the value of the coins in an asynchronous thread from coinmarketcap
 * and display the main program screen (MainDisplayView) afterwards.
 */
class CustomViewModel : ItemViewModel<Token>() {
    private val controller: BalanceController by inject()
    private val mainView: MainView by inject()
    private val mainDisplayView: MainDisplayView by inject()
    private val failView: FailView by inject()
    val token = SimpleObjectProperty<ObservableList<Token>>()

    fun refresh() {
        runAsync {
            updateMessage("loading...")
            controller.refresh()
        } ui {
            token.set(controller.coinValues.observable())
            mainView.root.replaceChildren(mainDisplayView)
        } fail {
            if (controller.inputFileNotFound) {
                failView.inputError()
            } else {
                failView.connectionError()

            }
            mainView.root.replaceChildren(failView)
        }
    }


}


/**
 * This is the root element of the main view.
 * It is the outer window in which any content is displayed.
 */
class MainView : View(title = "balance display") {

    private val progressBarView: ProgressBarView by inject()
    private val customViewModel: CustomViewModel by inject()

    override val root = StackPane()

    init {
        root += progressBarView
        customViewModel.refresh()
    }

}
