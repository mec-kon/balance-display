import javafx.stage.Stage
import tornadofx.*


/**
 * The main view of the program is specified here.
 * The main view is located in the MainView.kt file.
 */

class MainApp: App(MainView::class){
    override fun start(stage: Stage) {
        stage.width = 1000.0
        stage.height = 800.0
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<MainApp>(args)
}
