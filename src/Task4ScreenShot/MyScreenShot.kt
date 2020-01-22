package Task4ScreenShot

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.Stage
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO


class MyScreenShot : Application() {

    var brushSize = 18.0
    val slider = Slider(0.0, 10.0, 0.0)

    lateinit var c: Controller


    override fun start(primaryStage: Stage) {

        val loader = FXMLLoader(javaClass.getResource("Main.fxml"))
        val root = loader.load<Parent>()
        c = loader.getController<Controller>()

        c.shotBt.onAction = EventHandler {
            savePart()
        }

        var g = c.canvas
            .graphicsContext2D

        c.canvas.onMouseDragged = EventHandler<MouseEvent> { e ->
            val size = brushSize
            val x = e.x - size / 2
            val y = e.y - size / 2

            if (e.button == MouseButton.SECONDARY) {
                g.clearRect(x, y, size, size)
            } else {
                g.fill = c.cp.value
                g.fillRect(x, y, size, size)
            }
        }

        val scene = Scene(root)
        primaryStage.scene = scene
        primaryStage.show()
    }

    fun saveAsPng() {
        try {
            var robot = Robot()
            var fileName = "screen.jpg"

            var screenRect = Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
            var screenFullImage = robot.createScreenCapture(screenRect)
            ImageIO.write(screenFullImage, "jpg", File(fileName))
            print("Done")
        } catch (ex: IOException) {
            print(ex)
        }
    }

    fun savePart() {
        try {
            var robot = Robot()
            var fileName = "screen.jpg"

            var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            var captureRect = Rectangle(0, 0, screenSize.width / 2, screenSize.height / 2);
            var screenFullImage = robot.createScreenCapture(captureRect);
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
}