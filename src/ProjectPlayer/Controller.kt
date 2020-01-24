package ProjectPlayer

import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.media.EqualizerBand
import javafx.scene.text.Text
import java.lang.String.format
import java.util.*
import java.net.URL

class Controller : Initializable {

    @FXML
    lateinit var tSl: Slider
    @FXML
    lateinit var volumesli: Slider
    @FXML
    lateinit var tleftt: Text
    @FXML
    lateinit var trit: Text
    @FXML
    lateinit var nameCurSong: Text
    @FXML
    lateinit var alltime: Text
    @FXML
    lateinit var pauseBt: Button
    @FXML
    lateinit var prevBt: Button
    @FXML
    lateinit var playBt: Button
    @FXML
    lateinit var nextBt: Button
    @FXML
    lateinit var addSongBt: Button
    @FXML
    lateinit var delSonfBt: Button
    @FXML
    lateinit var setBt: MenuItem
    @FXML
    lateinit var aboutBt: MenuItem
    @FXML
    lateinit var exitBt: MenuItem
    @FXML
    lateinit var stopBt: Button
    @FXML
    lateinit var janrChoice: ChoiceBox<String>
    @FXML
    lateinit var artistChoice: ChoiceBox<String>
    @FXML
    lateinit var albumChoice: ChoiceBox<String>
    @FXML
    lateinit var yearChoice: ChoiceBox<String>
    @FXML
    lateinit var tm: TableView<Song>
    @FXML
    lateinit var slidersPaneBox: HBox

    val sliders = mutableListOf<Slider>()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        for (c in slidersPaneBox.children) {
            val vb = (c as VBox)
            val t = vb.children[0] as Text
            val s = vb.children[1] as Slider
            s.min = EqualizerBand.MIN_GAIN
            s.max = EqualizerBand.MAX_GAIN

            sliders.add(s)
            s.valueProperty().addListener { _, _, newValue ->
                t.text = format("%.2f", newValue)
            }
        }

    }


    @FXML
    private fun dragTime(e: MouseEvent) {
        val (m, s) = alltime.text.split(':').map { it.toInt() }
        val at = (m * 60 + s)
        val n = (at * tSl.value).toLong()

        val tr = at - n
        trit.text = "-" + (tr / 60) + ":" + (tr % 60)
        tleftt.text = "" + (n / 60) + ":" + (n % 60)
    }

    fun dragTime(n: Long) {
        val (m, s) = alltime.text.split(':').map { it.toInt() }

        val at = (m * 60 + s)

        val tr = at - n
        tSl.value = n.toDouble() / at

        trit.text = "-" + (tr / 60) + ":" + (tr % 60)
        tleftt.text = "" + (n / 60) + ":" + (n % 60)
    }


    @FXML
    fun setTime(e: MouseEvent) {

    }


}