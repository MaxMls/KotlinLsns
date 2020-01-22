package GUISamples

import Crypto
import GetCoin
import javafx.application.Application
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.event.Event
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.StringConverter
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.max
import kotlin.math.min


class Kripta : Application() {
    data class Item(
        val crypto: Crypto,
        val color: ColorPicker,
        var isActive: Boolean = true,
        var select: Boolean = false
    ) {
        val name: String get() = crypto.name
        val cost: Double get() = crypto.priceUsd
    }

    private val getCoin: GetCoin = GetCoin()
    private val colorList = mutableListOf<Color>()
    private var cryptoListNoUs: MutableList<Crypto>
    private var cryptoListUs = mutableListOf<Crypto>()

    private val delBt = Button("Удалить валюту")
    private val refrBt = Button("Обновить график")
    private val bt = Button("Добавить валюту")
    private val saveBt = Button("Сохранить текущие настройки")
    private val tableView = TableView<Item>()
    private val datePickerStart = DateTimePicker()
    private val datePickerEnd = DateTimePicker()
    val period = ChoiceBox(FXCollections.observableArrayList(GetCoin.EIntervalType.values().toList()))

    init {
        for (f in Color::class.java.fields) {
            val c = f.get(null) as Color
            if (c.opacity > 0.9)
                colorList.add(c)
        }

        cryptoListNoUs = getCoin.coinNames().toMutableList()

        period.value = GetCoin.EIntervalType.DAY


        period.converter = object : StringConverter<GetCoin.EIntervalType>() {

            override fun toString(o: GetCoin.EIntervalType): String {
                return o.desc
            }

            override fun fromString(string: String): GetCoin.EIntervalType {
                return period.items.filter {
                    it.desc == string
                }[0]
            }
        }

        // load settings from json
        try {
            val json = File("settings.json").readText(Charsets.UTF_8)

            val items = JSONObject(json).getJSONArray("items")

            for (i in 0 until items.length()) {
                val info = items.getJSONObject(i)

                val color = info.getJSONObject("color")
                val r = color.getInt("r")
                val g = color.getInt("g")
                val b = color.getInt("b")
                var c = colorList.find {
                    (it.red * 255).toInt() == r && (it.green * 255).toInt() == color.getInt("g") && (it.blue * 255).toInt() == color.getInt(
                        "b"
                    )
                }
                if (c != null)
                    colorList.remove(c)
                else
                    c = Color.color(r / 255.0, g / 255.0, b / 255.0)

                val id = info.getString("id")
                val crypto = cryptoListNoUs.find {
                    it.id == id
                }
                val isActive = info.getBoolean("active")
                if (crypto == null) throw Exception("invalid file")

                tableView.items.add(Item(getCrypto(crypto), ColorPicker(c), isActive))
            }
            bt.isDisable = cryptoListNoUs.count() == 0
        } catch (e: Exception) {
            if (tableView.items.count() == 0)
                tableView.items.add(Item(getCrypto(cryptoListNoUs[0]), nextColor(), true))
        }
    }

    private fun save() {
        val j = JSONObject()

        class TempColor(val r: Int, val g: Int, val b: Int)
        class Temp(val color: TempColor, val id: String, val active: Boolean)

        val list = mutableListOf<Temp>()
        for (i in tableView.items)
            list.add(
                Temp(
                    TempColor(
                        (i.color.value.red * 255).toInt(),
                        (i.color.value.green * 255).toInt(),
                        (i.color.value.blue * 255).toInt()
                    ), i.crypto.id, i.isActive
                )
            )
        j.put("items", list)
        File("settings.json").writeText(j.toString())
    }

    private fun rgb(color: Color): String {
        return "${color.red * 255}, ${color.green * 255}, ${color.blue * 255}"
    }

    private fun nextColor(): ColorPicker {
        return ColorPicker(colorList.removeAt(colorList.lastIndex))
    }

    private fun returnColor(cp: ColorPicker) {
        colorList.add(cp.value)
    }

    private fun getCrypto(cp: Crypto): Crypto {
        cryptoListNoUs.remove(cp)
        cryptoListUs.add(cp)
        return cp
    }

    private fun returnCrypto(cp: Crypto) {
        cryptoListNoUs.add(0, cp)
        cryptoListNoUs.sortBy { it.rank }
        try {
            cryptoListUs.remove(cp)
        } catch (e: Exception) {
        }
    }


    override fun start(stage: Stage) {
        val graphBox = VBox()


        val root = VBox()

        root.children.add(graphBox)


        tableView.isEditable = true
        root.children.add(tableView)

        // Генерация таблицы
        run {
            val nameColumn = TableColumn<Item, String>("Name")
            nameColumn.cellValueFactory = PropertyValueFactory("name")
            tableView.columns.add(nameColumn)

            val costColumn = TableColumn<Item, Double>("Cost")
            costColumn.cellValueFactory = PropertyValueFactory("cost")
            tableView.columns.add(costColumn)

            val colorColumn = TableColumn<Item, ColorPicker>("Color")
            colorColumn.cellValueFactory = PropertyValueFactory("color")

            tableView.columns.add(colorColumn)

            val booleanColumn = TableColumn<Item, Boolean>("isActive")
            booleanColumn.cellValueFactory = PropertyValueFactory("isActive")

            booleanColumn.setCellFactory { column ->
                val cell = CheckBoxTableCell<Item, Boolean> { index ->
                    val selected = SimpleBooleanProperty(
                        tableView.items[index].isActive
                    )
                    selected.addListener { _, _, n ->
                        tableView.items[index].isActive = n
                        tableView.selectionModel.select(index)

                        Event.fireEvent(
                            column.tableView, MouseEvent(
                                MouseEvent.MOUSE_CLICKED, 0.0, 0.0, 0.0, 0.0,
                                MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null
                            )
                        )
                    }
                    selected
                }
                cell
            }
            tableView.columns.add(booleanColumn)
        }



        delBt.isDisable = true
        tableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            delBt.isDisable = cryptoListUs.count() == 0 || newValue == null
        }

        bt.setOnAction {
            val dialog = ChoiceDialog(cryptoListNoUs[0], cryptoListNoUs)

            dialog.title = "Добавить"
            dialog.headerText = "Выберите валюту:"
            dialog.showAndWait().ifPresent { crypto ->
                tableView.items.add(Item(getCrypto(crypto), nextColor(), false))
                bt.isDisable = cryptoListNoUs.count() == 0
            }
        }

        delBt.setOnAction {
            val si = tableView.selectionModel.selectedItem
            returnColor(si.color)
            returnCrypto(si.crypto)
            tableView.items.remove(si)
            bt.isDisable = false
        }

        refrBt.setOnAction {
            graphBox.children.clear()
            graphBox.children.add(updateChart())
        }

        saveBt.setOnAction {
            save()
        }

        datePickerEnd.value = LocalDate.now()
        datePickerStart.value = datePickerEnd.value.minusMonths(1)

        val buttonBar = FlowPane()
        buttonBar.alignment = Pos.BASELINE_CENTER
        buttonBar.padding = Insets(10.0)
        buttonBar.children.addAll(bt,delBt,refrBt,saveBt)

        val periodBar = FlowPane()
        periodBar.padding = Insets(10.0)
        periodBar.alignment = Pos.BASELINE_CENTER

        periodBar.children.addAll(Text(" Период: "), period, Text(" Начало: "), datePickerStart, Text(" Конец: "), datePickerEnd)

        root.children.addAll(buttonBar,periodBar)

        val scene = Scene(root, 1000.0, 800.0)
        stage.title = "Crypto"
        stage.scene = scene
        stage.show()
    }

    private fun updateChart(): LineChart<String, Number> {

        val tempStart = datePickerEnd.dateTimeValue.minusDays(period.value.maxPeriod)
        if (tempStart.toEpochSecond(ZoneOffset.UTC) > datePickerStart.dateTimeValue.toEpochSecond(ZoneOffset.UTC)) {
            datePickerStart.dateTimeValue = tempStart
        }

        val start = datePickerStart.dateTimeValue.toEpochSecond(ZoneOffset.UTC) * 1000
        val end = datePickerEnd.dateTimeValue.toEpochSecond(ZoneOffset.UTC) * 1000

        val threads = mutableListOf<Thread>()
        // asynk
        for (i in tableView.items) {
            if (!i.isActive) continue
            val thread = Thread {
                getCoin.loadState(period.value, i.crypto, start, end)
            }
            threads.add(thread)
            thread.start()
        }

        for (t in threads) {
            t.join()
        }

        var maxPrice = Double.MIN_VALUE
        var minPrice = Double.MAX_VALUE
        var last: Crypto? = null

        for (i in tableView.items) {
            if (!i.isActive) continue
            for (h in i.crypto.history) {
                maxPrice = max(h.priceUsd, maxPrice)
                minPrice = min(h.priceUsd, minPrice)
            }
            last = i.crypto
        }

        val yAxis = NumberAxis(minPrice, maxPrice, (maxPrice - minPrice) / 10)
        yAxis.label = "Price"

        val sdfDate = SimpleDateFormat("dd.MM.yy HH:mm")

        val xAxis = CategoryAxis()
        if (last == null) return LineChart(xAxis, yAxis)

        val dates = last.history.map { sdfDate.format(it.time) }.distinct()

        xAxis.categories = FXCollections.observableArrayList(dates)

        val areaChart = LineChart(xAxis, yAxis)
        areaChart.title = "Time"

        var style = ""

        tableView.items.forEachIndexed { index, i ->

            if (i.isActive) {
                val series1 = XYChart.Series<String, Number>()
                series1.name = i.crypto.name

                for (h in i.crypto.history) {
                    series1.data.add(XYChart.Data(sdfDate.format(h.time), h.priceUsd))
                }
                areaChart.data.add(series1)

                val color = i.color.value
                val rgb = rgb(color)

                style += "CHART_COLOR_${index + 1}: rgba($rgb, 1.0) ;"
            }
        }

        areaChart.style = style

        return areaChart
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Kripta::class.java)
        }
    }
}