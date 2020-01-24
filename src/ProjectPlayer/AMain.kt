package ProjectPlayer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.URLEncoder
import java.sql.DriverManager
import org.sqlite.JDBC

class AMain : Application() {

    lateinit var c: Controller
    var mp = MusicPlayer()

    fun p(s: String): String {
        return URLEncoder.encode(s, "UTF-8")
    }


    override fun start(primaryStage: Stage) {

        val loader = FXMLLoader(javaClass.getResource("player.fxml"))
        primaryStage.scene = Scene(loader.load<Parent>())
        c = loader.getController<Controller>()
        primaryStage.show()
        val mp = playerInit()

        //val config = SQLiteConfig()

        val con = DriverManager.getConnection("jdbc:sqlite:data.db")
        val stat = con.createStatement()
        stat.executeUpdate("CREATE TABLE IF NOT EXISTS NotesDB (idno INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Title VARCHAR(500), Description VARCHAR(1000), DateCreated DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL);")




        mp.song("D:\\Users\\Max1\\Music\\TOPHAMHAT-KYO Princess♂.mp3");
    }


    private fun playerInit(): MusicPlayer {
        val mp = MusicPlayer()

        var changePos = false
        mp.setOnSongEnd {
            println("hui")
            mp.song("D:\\Users\\Max1\\Music\\TOPHAMHAT-KYO Princess♂.mp3")
            mp.play()
        }
        mp.setOnSongPositionChanged { if (!changePos) c.tSl.value = it }
        mp.setOnTotalTimeChanged {
            val d = it.toSeconds().toLong()
            c.alltime.text = "${d / 60}:${d % 60}"
        }
        c.playBt.setOnAction { mp.play() }

        c.tSl.setOnMousePressed { changePos = true }

        c.tSl.setOnMouseReleased {
            changePos = false
            mp.setPos(c.tSl.value)
        }

        c.pauseBt.setOnAction { mp.pause() }
        c.stopBt.setOnAction { mp.stop() }
        c.volumesli.setOnMouseDragged { mp.setVolume(c.volumesli.value) }
        c.volumesli.setOnMousePressed { mp.setVolume(c.volumesli.value) }
        return mp
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(AMain::class.java)
        }
    }

}