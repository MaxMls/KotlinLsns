package ProjectPlayer

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.prefs.Preferences
import kotlin.system.exitProcess


/*Application name	musicplayer
API key	97ba4be3429469c825510ff016095ef5
Shared secret	a144f21436665400f2bc2415892584ae
Registered to	lugyfmtdrhefw*/

// searsh track http://ws.audioscrobbler.com/2.0/?method=track.search&track=Doja%20Cat%20Boss%20Bitch&api_key=97ba4be3429469c825510ff016095ef5&format=json
// get album info  http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=97ba4be3429469c825510ff016095ef5&artist=cher&track=believe&format=json
// get image http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=97ba4be3429469c825510ff016095ef5&artist=Cher&album=Believe&format=json


class AMain : Application() {

    lateinit var c: Controller
    lateinit var mp: MusicPlayer
    val prefs = Preferences.userRoot().node("/musicPlayerTask");
    lateinit var primaryStage: Stage
    val apiKey = BasicNameValuePair("api_key", "97ba4be3429469c825510ff016095ef5")
    val apiUri = "http://ws.audioscrobbler.com/2.0/"
    var imgLoadThread: Thread? = null

    fun getAlbumPic(name: String) {
        imgLoadThread?.stop()

        imgLoadThread = Thread {
            try {
                c.img.image = Image(javaClass.getResource("default.png").toString())


                val res1 = DB.makeAPICall(
                    apiUri, listOf<NameValuePair>(
                        apiKey,
                        BasicNameValuePair("method", "track.search"),
                        BasicNameValuePair("track", name),
                        BasicNameValuePair("format", "json")
                    )
                )
                val r1json = JSONObject(res1)
                println(res1)
                val fr =
                    r1json.getJSONObject("results").getJSONObject("trackmatches").getJSONArray("track").getJSONObject(0)

                val res2 = DB.makeAPICall(
                    apiUri, listOf<NameValuePair>(
                        apiKey,
                        BasicNameValuePair("method", "track.getInfo"),
                        BasicNameValuePair("artist", fr.getString("artist")),
                        BasicNameValuePair("track", fr.getString("name")),
                        BasicNameValuePair("format", "json")
                    )
                )
                println(res2)

                val r2json = JSONObject(res2)
                val ims = r2json.getJSONObject("track").getJSONObject("album").getJSONArray("image")

                val img = ims.getJSONObject(ims.length() - 1).getString("#text")
                println(img)
                c.img.image = Image(img)
            } catch (e: Exception) {

            }

        }
        imgLoadThread?.start()
    }


    fun onOtherSongSelect(ns: Song) {
        try {
            println("chaa")
            getAlbumPic(ns.title + " " + ns.artist + " " + ns.album)

            if (ns.title == null || ns.title == "") {
                c.textWeb.engine.loadContent("");
                return
            } else c.textWeb.engine.load("https://google.com/search?q=текст+песни+" + ns.title)
            if (ns.artist == null || ns.artist == "")
                c.artistWeb.engine.loadContent("");
            else
                c.artistWeb.engine.load("https://google.com/search?q=site:wikipedia.com+" + ns.artist)
        } catch (e: Exception) {
        }
    }

    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage
        val loader = FXMLLoader(javaClass.getResource("player.fxml"))
        primaryStage.scene = Scene(loader.load<Parent>())
        c = loader.getController<Controller>()
        primaryStage.show()

        libInit()
        val l = mutableListOf<String>()
        val series = Series<String, Float>()
        val b = Series<String, Float>()
        for (i in 0..100) {
            l.add(i.toString())
            series.data.add(XYChart.Data<String, Float>(l[i], 0f))
        }
        b.data.add(XYChart.Data<String, Float>("0", 40f))
        b.data.add(XYChart.Data<String, Float>(series.data.lastIndex.toString(), 40f))

        val xAxis = CategoryAxis()
        xAxis.categories = FXCollections.observableArrayList(l)

        /* c.tm.selectionModel.selectedItemProperty().addListener { _, os, ns ->

         }
 */
        c.chart1.data.add(series)

        c.chart1.data.add(b)

        c.chart1.yAxis.animated = false;
        // c.chart1.yAxis.maxHeight = 60.0
        c.chart1.yAxis.minHeight = 1000.0
        c.chart1.xAxis.animated = false;

        mp = MusicPlayer(c.sliders, series.data)
        mp = playerInit()

        c.exitBt.setOnAction {
            exitProcess(0)
        }
    }


    private fun playerInit(): MusicPlayer {
        fun ss(i: Int) {
            c.tm.selectionModel.select((c.tm.selectionModel.selectedIndex + c.tm.items.count() + i) % c.tm.items.count())
            mp.song(c.tm.selectionModel.selectedItem.file)
            c.nameCurSong.text = c.tm.selectionModel.selectedItem.title
            onOtherSongSelect(c.tm.selectionModel.selectedItem)
            mp.play()
        }
        mp.setOnSongEnd { ss(1) }
        c.nextBt.setOnAction { ss(1) }
        c.prevBt.setOnAction { ss(-1) }

        mp.setOnTotalTimeChanged {
            val d = it.toSeconds().toLong()
            c.alltime.text = "${d / 60}:${d % 60}"
        }

        c.playBt.setOnAction {
            if (c.tm.selectionModel.selectedItem == null) return@setOnAction
            c.nameCurSong.text = c.tm.selectionModel.selectedItem.title
            mp.play()
        }
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

    lateinit var db: DB

    private fun libInit(): TableView<Song> {
        var oldSel: Song? = null
        c.tm.setOnMousePressed { event ->
            if (event.isPrimaryButtonDown && event.clickCount == 2) {
                val f = c.tm.selectionModel.selectedItem
                mp.song(f.file)
                c.nameCurSong.text = c.tm.selectionModel.selectedItem.title
                mp.play()

                onOtherSongSelect(f)
                oldSel = f
            } else {
                c.tm.selectionModel.select(oldSel)
            }
        }

        val t = c.tm
        db = DB()

        t.items.addAll(db.getSongs())
        db.setOnSongsChanged {
            t.items.clear()
            t.items.addAll(db.getSongs())
        }
        c.delSonfBt.setOnAction {
            if (c.tm.selectionModel.selectedItem == null) return@setOnAction
            val oldS = c.tm.selectionModel.selectedItem
            c.tm.selectionModel.select((c.tm.selectionModel.selectedIndex + 1) % c.tm.items.count())
            mp.song(c.tm.selectionModel.selectedItem.file)
            c.nameCurSong.text = c.tm.selectionModel.selectedItem.title
            c.tm.items.remove(oldS)
            oldSel = c.tm.selectionModel.selectedItem
            db.delSong(oldS)

        }

        c.addSongBt.setOnAction {
            val chooser = FileChooser()
            chooser.title = "Open Image file"
            chooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Audio Files", "*.mp3")
            )
            val selectedFiles = chooser.showOpenMultipleDialog(primaryStage)
            val l = mutableListOf<Song>()

            val ts = mutableListOf<Thread>()
            if (selectedFiles == null) return@setOnAction
            for (f in selectedFiles) {
                val music = Media(f.toURI().toString())
                val mp = MediaPlayer(music)


                val t = Thread {
                    var tt = 0
                    while (true) {
                        Thread.sleep(400)
                        if (mp.status != MediaPlayer.Status.READY) continue
                        println(mp.media.metadata)


                        val d = mp.totalDuration.toSeconds().toLong()
                        l.add(
                            Song(
                                0,
                                if (mp.media.metadata["title"] is String) {
                                    mp.media.metadata["title"] as String
                                } else {
                                    f.nameWithoutExtension
                                },
                                "" + (d / 60) + ":" + (d % 60),
                                "",
                                if (mp.media.metadata["album artist"] is String) {
                                    mp.media.metadata["album artist"] as String
                                } else {
                                    ""
                                },

                                if (mp.media.metadata["album artist"] is String) {
                                    mp.media.metadata["album artist"] as String
                                } else {
                                    ""
                                },

                                if (mp.media.metadata["year"] is Int) {
                                    (mp.media.metadata["year"] as Int)
                                } else {
                                    null
                                },
                                f.absolutePath.toString()
                            )
                        )
                        break
                    }
                }
                ts.add(t)
                t.start()
            }

            val w = Thread {
                while (ts.size != 0) {
                    ts.removeAt(0).join()
                }
                db.addSongs(l)
            }
            w.start()
        }




        c.aboutBt.setOnAction {
            val key = prefs.get("settingsPath", "noKey");
            System.out.println("key - " + key);
        }

        c.setBt.setOnAction {
            val loader = FXMLLoader(javaClass.getResource("settings.fxml"))
            val stage = Stage()
            stage.initModality(Modality.APPLICATION_MODAL)
            stage.scene = Scene(loader.load<Parent>())
            val cs = loader.getController<Settings>()
            cs.setStage(stage, Paths.get(prefs.get("settingsPath", "/")))

            cs.setOnSettingsPathChanged {

                val sourceDir = File(prefs.get("settingsPath", "/")) //this directory already exists
                prefs.put("settingsPath", it)
                val destDir = File(it)
                destDir.mkdirs()

                val destPath = destDir.toPath()

                for (sourceFile in sourceDir.listFiles()) {
                    if (sourceFile.name == "data.db") {
                        val sourcePath = sourceFile.toPath()
                        Files.move(
                            sourcePath,
                            destPath.resolve(sourcePath.fileName),
                            StandardCopyOption.REPLACE_EXISTING
                        )

                    }
                }
            }

            stage.showAndWait()
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