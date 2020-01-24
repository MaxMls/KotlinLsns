package ProjectPlayer

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.stage.Stage
import java.net.URLEncoder
import java.sql.DriverManager

class AMain : Application() {

    lateinit var c: Controller
    lateinit var mp: MusicPlayer

    fun p(s: String): String {
        return URLEncoder.encode(s, "UTF-8")
    }


    override fun start(primaryStage: Stage) {

        val loader = FXMLLoader(javaClass.getResource("player.fxml"))
        primaryStage.scene = Scene(loader.load<Parent>())
        c = loader.getController<Controller>()
        primaryStage.show()

        mp = MusicPlayer(c.sliders)
        libInit()
        mp = playerInit()

        mp.song("D:\\Users\\Max1\\Music\\TOPHAMHAT-KYO Princess♂.mp3");
    }


    private fun playerInit(): MusicPlayer {
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


    private fun libInit(): TableView<Song> {

        val con = DriverManager.getConnection("jdbc:sqlite:data.db")
        val stat = con.createStatement()
        //stat.executeUpdate("DROP TABLE IF EXISTS MusicLib")
        stat.executeUpdate(
            """ 
            CREATE TABLE IF NOT EXISTS MusicLib (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title       TEXT,
                    file        TEXT UNIQUE,
                    duration    INTEGER,
                    genre       TEXT,
                    artist      TEXT,
                    album       TEXT,
                    releaseYear INTEGER
                    );
        """
        )

/*
        val ins = con.prepareStatement(
            "INSERT INTO MusicLib(title, file, duration, genre, artist, album, releaseYear) values(?,?,?,?,?,?,?)"
        )
        ins.setString(1, "TOPHAMHAT-KYO Princess♂.mp3")
        ins.setString(2, "D:\\Users\\Max1\\Music\\TOPHAMHAT-KYO Princess♂.mp3")
        ins.setInt(3, 0)
        ins.setString(4, null)
        ins.setString(5, "Tophamhatkyo")
        ins.setString(6, "Tophamhatkyo")
        ins.setInt(7, 2019)

        ins.executeUpdate()
        try {
            ins.executeUpdate()
        } catch (e: Exception) {
            println(e)
        }*/

        val t = c.tm

        val res = stat.executeQuery("SELECT * FROM  MusicLib")

        fun n(t: Any?): String {
            try{
                if(t as Int? == 0) return "Неизвестно"
            } catch (e:Exception){}

            return t?.toString() ?: "Неизвестно"
        }

        while (res.next()) {
            c.tm.items.add(
                Song(
                    n(res.getInt("id")),
                    n(res.getString("title")),
                    { x: Int -> if (x == 0) n(x) else "${x / 60}:${x % 60}" }(res.getInt("duration")),
                    n(res.getString("genre")),
                    n(res.getString("artist")),
                    n(res.getString("album")),
                    n(res.getInt("releaseYear"))
                )
            )
        }

        return t
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(AMain::class.java)
        }
    }

}