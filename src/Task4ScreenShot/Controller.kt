package Task4ScreenShot

import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text
import javafx.stage.FileChooser

class Controller {
    @FXML
    lateinit var openBt: MenuItem
    @FXML
    lateinit var saveBt: MenuItem
    @FXML
    lateinit var saveAsBt: MenuItem
    @FXML
    lateinit var exitBt: MenuItem
    @FXML
    lateinit var crop: MenuItem
    @FXML
    lateinit var shotBt: Button
    @FXML
    lateinit var cancelCropBt: Button
    @FXML
    lateinit var applyCropBt: Button
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
    lateinit var hidable: CheckBox
    @FXML
    lateinit var selectionGroup: Group
    @FXML
    lateinit var cropTools: ToolBar
    @FXML
    lateinit var stTools: ToolBar

    public var drawMode = true;

    init {

    }

    @FXML
    fun onDraggedSize() {
        sizeText.text = sizeSlider.value.toInt().toString()
    }

    @FXML
    fun timeDragged() {
        timer.text = durationSlider.value.toInt().toString()
    }

    @FXML
    fun openFile() {

        val fileChooser = FileChooser()
        fileChooser.title = "Open Image file"
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        )
/*        val selectedFile = fileChooser.showOpenDialog(primaryStage)
        if (selectedFile != null) {


            *//*clearSelection(selectionGroup)
            mainImage = convertFileToImage(selectedFile)
            mainImageView.setImage(mainImage)
            changeStageSizeImageDimensions(primaryStage, mainImage)*//*
        }*/
    }


    @FXML
    fun canvasDragged(e : MouseEvent) {
        if (!drawMode) return
        var g = canvas.graphicsContext2D;
        val size = sizeSlider.value
        val x = e.x - size / 2
        val y = e.y - size / 2

        if (e.button == MouseButton.SECONDARY) {
            g.clearRect(x, y, size, size)
        } else {
            g.fill = cp.value
            g.fillRect(x, y, size, size)
        }
    }

}