package ProjectPlayer

import javafx.application.Application
import javafx.collections.ListChangeListener
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.prefs.Preferences
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.system.exitProcess


class AMain : Application() {

    lateinit var c: Controller
    lateinit var mp: MusicPlayer
    val prefs = Preferences.userRoot().node("/musicPlayerTask");
    lateinit var primaryStage: Stage

    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage
        val loader = FXMLLoader(javaClass.getResource("player.fxml"))
        primaryStage.scene = Scene(loader.load<Parent>())
        c = loader.getController<Controller>()
        primaryStage.show()

        libInit()

        mp = MusicPlayer(c.sliders)
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

            var ts = mutableListOf<Thread>()
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