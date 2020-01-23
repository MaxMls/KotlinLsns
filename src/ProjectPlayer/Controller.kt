package ProjectPlayer

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Slider
import javafx.scene.control.TableView
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
    lateinit var janrChoice: ChoiceBox<String>
    @FXML
    lateinit var artistChoice: ChoiceBox<String>
    @FXML
    lateinit var albumChoice: ChoiceBox<String>
    @FXML
    lateinit var yearChoice: ChoiceBox<String>
    @FXML
    lateinit var tm: TableView<Song>

}