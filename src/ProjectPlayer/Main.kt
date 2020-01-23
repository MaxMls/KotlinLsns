package ProjectPlayer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.stage.Stage

class Main : Application() {

    lateinit var c: Controller

    override fun start(primaryStage: Stage) {

        val loader = FXMLLoader(javaClass.getResource("player.fxml"))
        val root = loader.load<Parent>()
        c = loader.getController<Controller>()

    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }

}