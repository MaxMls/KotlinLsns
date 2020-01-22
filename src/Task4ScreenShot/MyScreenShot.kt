package Task4ScreenShot

import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.SnapshotParameters
import javafx.scene.control.Alert
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.json.JSONObject
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.imageio.ImageIO
import javax.swing.JFileChooser


class MyScreenShot : Application() {

    lateinit var c: Controller
    private val robot = Robot()

    private val areaSelection = AreaSelection()
    var cropMode = false

    override fun start(primaryStage: Stage) {

        val loader = FXMLLoader(javaClass.getResource("Main.fxml"))
        val root = loader.load<Parent>()
        c = loader.getController<Controller>()

        c.stTools.managedProperty().bind(c.stTools.visibleProperty())
        c.cropTools.managedProperty().bind(c.cropTools.visibleProperty())
        c.cropTools.isVisible = false

        c.crop.setOnAction {
            c.cropTools.isVisible = true
            c.stTools.isVisible = false
            areaSelection.selectArea(c.selectionGroup, c.canvas)
            c.drawMode = false

        }

        c.cancelCropBt.setOnAction {
            c.cropTools.isVisible = false
            c.stTools.isVisible = true
            areaSelection.clearSelection(c.selectionGroup)
            cropMode = false
            c.drawMode = true
            areaSelection.stopSelection()
        }

        c.applyCropBt.setOnAction {
            c.cropTools.isVisible = false
            c.stTools.isVisible = true
            areaSelection.cropImage()
            areaSelection.clearSelection(c.selectionGroup)
            cropMode = false
            c.drawMode = true
            areaSelection.stopSelection()
        }

        c.saveBt.setOnAction {
            saveImage(primaryStage)
        }
        c.saveBt.accelerator = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)

        c.saveAsBt.setOnAction {
            saveImage(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString())
        }
        c.saveAsBt.accelerator = KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN)

        c.shotBt.setOnAction {
            if (c.hidable.isSelected) {
                primaryStage.hide()
                Thread.sleep((c.durationSlider.value * 1000.0).toLong())
            }
            val screenSize = Toolkit.getDefaultToolkit().screenSize
            val captureRect = Rectangle(0, 0, screenSize.width, screenSize.height)
            val screenFullImage = robot.createScreenCapture(captureRect)
            c.canvas.height = screenSize.height.toDouble()
            c.canvas.width = screenSize.width.toDouble()

            c.canvas.graphicsContext2D.drawImage(SwingFXUtils.toFXImage(screenFullImage, null), 0.0, 0.0)
            primaryStage.show()
        }

        c.openBt.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.title = "Open Image file"
            fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
            )
            val selectedFile = fileChooser.showOpenDialog(primaryStage)
            if (selectedFile != null) {
                val img = convertFileToImage(selectedFile)
                c.canvas.height = img.height
                c.canvas.width = img.width
                c.canvas.graphicsContext2D.drawImage(img, 0.0, 0.0)
            }
        }


        val scene = Scene(root)
        primaryStage.scene = scene
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(MyScreenShot::class.java)
        }
    }

    private fun convertFileToImage(imageFile: File): Image {
        lateinit var image: Image
        try {
            FileInputStream(imageFile).use { fileInputStream -> image = Image(fileInputStream) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return image
    }


    private fun saveImage(stage: Stage) {
        var dir = File("/")

        try {
            val json = File("settings.json").readText(Charsets.UTF_8)
            dir = File(JSONObject(json).getString("dir"))
        } catch (e: Exception) {
        }


        val fileChooser = FileChooser()
        fileChooser.title = "Save Image"

        fileChooser.initialDirectory = File(dir.parent)
        fileChooser.initialFileName = dir.nameWithoutExtension

        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("portable network graphics", "*.png"))
        val file = fileChooser.showSaveDialog(stage) ?: return
        saveImage(file)

        val j = JSONObject()
        j.put("dir", file.absoluteFile)
        File("settings.json").writeText(j.toString())

    }

    private fun saveImage(s: String) {
        val p = JFileChooser().fileSystemView.defaultDirectory.toString();


        saveImage(File("$p\\$s.png").absoluteFile)
    }

    private fun saveImage(file: File) {
        val sp = SnapshotParameters();
        sp.fill = Color.TRANSPARENT;
        val wi = WritableImage(c.canvas.width.toInt(), c.canvas.height.toInt())
        ImageIO.write(SwingFXUtils.fromFXImage(c.canvas.snapshot(sp, wi), null), "png", file)

        val dialog = Alert(Alert.AlertType.INFORMATION)

        dialog.title = "Изображение сохранено"
        dialog.headerText = "Изображение успешно сохранено в файл:"
        val ta = TextArea(file.toString())
        ta.isEditable = false
        dialog.dialogPane.content = ta
        dialog.showAndWait()
    }


}