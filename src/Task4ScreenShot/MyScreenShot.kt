package Task4ScreenShot

import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.imageio.ImageIO


class MyScreenShot : Application() {

    var brushSize = 18.0
    val slider = Slider(0.0, 10.0, 0.0)

    lateinit var c: Controller
    val robot = Robot()

    val areaSelection = AreaSelection()
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
            c.drawMode = false;

        }

        c.cancelCropBt.setOnAction {
            c.cropTools.isVisible = false
            c.stTools.isVisible = true
            areaSelection.clearSelection(c.selectionGroup)
            cropMode = false
            c.drawMode = true;
            areaSelection.stopSelection()
        }

        c.applyCropBt.setOnAction {
            c.cropTools.isVisible = false
            c.stTools.isVisible = true
            areaSelection.cropImage()
            areaSelection.clearSelection(c.selectionGroup)
            cropMode = false
            c.drawMode = true;
            areaSelection.stopSelection()
        }


        c.shotBt.setOnAction {
            if (c.hidable.isSelected) {
                primaryStage.hide()
                Thread.sleep((c.durationSlider.value * 1000.0).toLong())
            }
            val screenSize = Toolkit.getDefaultToolkit().screenSize;
            val captureRect = Rectangle(0, 0, screenSize.width, screenSize.height);
            val screenFullImage = robot.createScreenCapture(captureRect);
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
                c.canvas.graphicsContext2D.drawImage(img, 0.0,0.0)
            }
        }


        val scene = Scene(root)
        primaryStage.scene = scene
        primaryStage.show()
    }

    fun saveAsPng() {
        try {
            val robot = Robot()
            val fileName = "screen.jpg"

            val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
            val screenFullImage = robot.createScreenCapture(screenRect)
            ImageIO.write(screenFullImage, "jpg", File(fileName))
            print("Done")
        } catch (ex: IOException) {
            print(ex)
        }
    }

    fun savePart() {
        try {
            val robot = Robot()
            val fileName = "screen.jpg"

            val screenSize = Toolkit.getDefaultToolkit().screenSize;
            val captureRect = Rectangle(0, 0, screenSize.width / 2, screenSize.height / 2);
            val screenFullImage = robot.createScreenCapture(captureRect);
            ImageIO.write(screenFullImage, "jpg", File(fileName));

            print("Done")
        } catch (ex: IOException) {
            print(ex)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(MyScreenShot::class.java)
        }
    }

    private fun convertFileToImage(imageFile: File): Image {
        lateinit var image: Image;
        try {
            FileInputStream(imageFile).use { fileInputStream -> image = Image(fileInputStream) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return image
    }
}