package ProjectPlayer

import com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import java.net.URLEncoder


class AMain : Application() {

    lateinit var c: Controller
    lateinit var mp: MusicPlayer


    override fun start(primaryStage: Stage) {

        val loader = FXMLLoader(javaClass.getResource("player.fxml"))
        primaryStage.scene = Scene(loader.load<Parent>())
        c = loader.getController<Controller>()
        primaryStage.show()

        libInit()

        mp = MusicPlayer(c.sliders)
        mp = playerInit()
        mp.song("D:\\Users\\Max1\\Music\\TOPHAMHAT-KYO Princess♂.mp3")



        c.tm.setOnMousePressed { event ->
            if (event.isPrimaryButtonDown && event.clickCount == 2) {
                val f = c.tm.selectionModel.selectedItem
                mp.song(f.file)
                mp.play()
            }
        }
    }


    private fun playerInit(): MusicPlayer {

        mp.setOnSongEnd {
            println("hui")
            mp.song("D:\\Users\\Max1\\Music\\TOPHAMHAT-KYO Princess♂.mp3")
            mp.play()
        }

        mp.setOnTotalTimeChanged {
            val d = it.toSeconds().toLong()
            c.alltime.text = "${d / 60}:${d % 60}"
        }
        c.playBt.setOnAction { mp.play() }
        run {
            var changePos = false
            mp.setOnSongPositionChanged { if (!changePos) c.tSl.value = it }
            c.tSl.setOnMousePressed { changePos = true }
            c.tSl.setOnMouseReleased {
                changePos = false
                mp.setPos(c.tSl.value)
            }
        }
        c.pauseBt.setOnAction { mp.pause() }
        c.stopBt.setOnAction { mp.stop() }
        c.volumesli.valueProperty().addListener { _, _, newVal ->
            mp.setVolume(newVal.toDouble())
        }

        return mp
    }


    private fun libInit(): TableView<Song> {


        val t = c.tm
        val db = DB()

        t.items.addAll(db.getSongs())

        return t
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(AMain::class.java)
        }
    }

}