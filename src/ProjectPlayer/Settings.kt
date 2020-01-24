package ProjectPlayer

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.stage.Stage
import java.io.File
import java.net.URL
import java.nio.file.Path
import java.util.*
import javax.swing.JFileChooser


class Settings : Initializable {
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @FXML
    lateinit var can: Button
    @FXML
    lateinit var sub: Button
    @FXML
    lateinit var pathBt: Button
    @FXML
    lateinit var pathF: TextField

    private var onSettingsPathChanged: ((newPath: String) -> Unit)? = null
    fun setOnSettingsPathChanged(listener: (newPath: String) -> Unit) {
        onSettingsPathChanged = listener
    }

    private lateinit var stage: Stage
    fun setStage(stage: Stage, path: Path) {
        this.stage = stage
        pathF.text = path.toAbsolutePath().toString()

        var file: File

        can.setOnAction {
            stage.close()
        }

        sub.setOnAction {
            onSettingsPathChanged?.invoke(pathF.text)
            stage.close()
        }

        pathBt.setOnAction {
            val chooser = JFileChooser(".")
            chooser.isMultiSelectionEnabled = true
            chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val ret = chooser.showOpenDialog(null)

            if (ret == JFileChooser.APPROVE_OPTION) {
                file = chooser.selectedFile
                pathF.text = file.toString()
            }

        }

        /*
         if(ret == JFileChooser.APPROVE_OPTION) {
             File[] files = chooser.getSelectedFiles();
             ...
         }*/


    }

}