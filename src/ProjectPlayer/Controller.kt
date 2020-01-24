package ProjectPlayer

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text

class Controller {

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
    private fun dragTime(e: MouseEvent) {
        val (m, s) = alltime.text.split(':').map { it.toInt() }
        val at = (m * 60 + s)
        val n = ( at* tSl.value).toLong()

        val tr = at - n
        trit.text = "-" + (tr / 60) + ":" + (tr % 60)
        tleftt.text = "" + (n / 60) + ":" + (n % 60)
    }

    fun dragTime(n: Long) {
        val (m, s) = alltime.text.split(':').map { it.toInt() }

        val at = (m * 60 + s)

        val tr = at - n
        tSl.value = n.toDouble()/at

        trit.text = "-" + (tr / 60) + ":" + (tr % 60)
        tleftt.text = "" + (n / 60) + ":" + (n % 60)
    }



    @FXML
    fun setTime(e: MouseEvent) {

    }



}