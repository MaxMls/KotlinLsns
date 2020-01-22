package Task4ScreenShot

import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.ColorPicker
import javafx.scene.control.MenuItem
import javafx.scene.control.Slider
import javafx.scene.text.Text

class Controller {
    @FXML
    lateinit var openBt: MenuItem
    @FXML
    lateinit var shotBt: Button
    @FXML
    lateinit var durationSlider: Slider
    @FXML
    lateinit var sizeSlider: Slider
    @FXML
    lateinit var sizeText: Text
    @FXML
    lateinit var timer: Text
    @FXML
    lateinit var canvas: Canvas
    @FXML
    lateinit var cp: ColorPicker

    @FXML
    fun onDraggedSize() {
        sizeText.text = sizeSlider.value.toInt().toString()
    }

    @FXML
    fun timeDragged() {
        timer.text = durationSlider.value.toInt().toString()
    }
}