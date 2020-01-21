package GUISamples

import GetCoin
import javafx.application.Application
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.event.Event
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.StackedAreaChart
import javafx.scene.chart.XYChart
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage

class Kripta : Application() {
    private var graph: Node? = null

    data class Item(
        val name: String,
        val cost: Double,
        val color: ColorPicker,
        var isActive: Boolean,
        var select: Boolean = false
    )

    private val grtCoin: GetCoin = GetCoin()

    override fun start(stage: Stage) {



        graph = addChart() //дефолтный граф

        val root = VBox()
        root.children.add(graph)
        val tableView = TableView<Item>()
        tableView.isEditable = true
        root.children.add(tableView)


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

                    println(n)

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

        val cp = ColorPicker(Color.AQUA)
        tableView.items.add(Item("Bitok", 100.0, cp, false))
        cp.setOnAction {
            println(it)
        }

        val bt = Button("Добавить")

        bt.setOnAction {
            val dialog = TextInputDialog("Tran")

            dialog.title = "o7planning"
            dialog.headerText = "Enter name:"
            dialog.contentText = "Name:"

            val result = dialog.showAndWait()

            result.ifPresent {

                tableView.items.add(Item(it, 100.0, ColorPicker(), false))
            }
        }


        root.children.add(bt)

        //Создаём сцену
        val scene = Scene(root, 600.0, 400.0)
        stage.title = "Stacked Area Chart"
        //Устанавливаем сцену
        stage.scene = scene
        //Показываем окно
        stage.show()
    }

    private fun addChart(): StackedAreaChart<String, Number> {

        //Defining the axes
        val xAxis = CategoryAxis()
        xAxis.categories = FXCollections.observableArrayList(
            listOf("1750", "1800", "1850", "1900", "1950", "1999", "2050")
        )

        val yAxis = NumberAxis(0.0, 10000.0, 2500.0)
        yAxis.label = "Population in Millions"

        //Creating the Area chart
        val areaChart = StackedAreaChart(xAxis, yAxis)
        areaChart.title = "Historic and Estimated Worldwide Population Growth by Region"

        //Prepare XYChart.Series objects by setting data
        val series1 = XYChart.Series<String, Number>()

        series1.name = "Asia"
        series1.data.add(XYChart.Data("1750", 502))
        series1.data.add(XYChart.Data("1800", 635))
        series1.data.add(XYChart.Data("1850", 809))
        series1.data.add(XYChart.Data("1900", 947))
        series1.data.add(XYChart.Data("1950", 1402))
        series1.data.add(XYChart.Data("1999", 3634))
        series1.data.add(XYChart.Data("2050", 5268))

        val series2 = XYChart.Series<String, Number>()
        series2.name = "Africa"
        series2.data.add(XYChart.Data("1750", 106))
        series2.data.add(XYChart.Data("1800", 107))
        series2.data.add(XYChart.Data("1850", 111))
        series2.data.add(XYChart.Data("1900", 133))
        series2.data.add(XYChart.Data("1950", 221))
        series2.data.add(XYChart.Data("1999", 767))
        series2.data.add(XYChart.Data("2050", 1766))

        val series3 = XYChart.Series<String, Number>()
        series3.name = "Europe"

        series3.data.add(XYChart.Data("1750", 163))
        series3.data.add(XYChart.Data("1800", 203))
        series3.data.add(XYChart.Data("1850", 276))
        series3.data.add(XYChart.Data("1900", 408))
        series3.data.add(XYChart.Data("1950", 547))
        series3.data.add(XYChart.Data("1999", 729))
        series3.data.add(XYChart.Data("2050", 628))

        val series4 = XYChart.Series<String, Number>()
        series4.name = "America"
        series4.data.add(XYChart.Data("1750", 18))
        series4.data.add(XYChart.Data("1800", 31))
        series4.data.add(XYChart.Data("1850", 54))
        series4.data.add(XYChart.Data("1900", 156))
        series4.data.add(XYChart.Data("1950", 339))
        series4.data.add(XYChart.Data("1999", 818))
        series4.data.add(XYChart.Data("2050", 1201))

        val series5 = XYChart.Series<String, Number>()
        series5.name = "Oceania"
        series5.data.add(XYChart.Data("1750", 2))
        series5.data.add(XYChart.Data("1800", 2))
        series5.data.add(XYChart.Data("1850", 2))
        series5.data.add(XYChart.Data("1900", 6))
        series5.data.add(XYChart.Data("1950", 13))
        series5.data.add(XYChart.Data("1999", 30))
        series5.data.add(XYChart.Data("2050", 46))

        //Setting the data to area chart
        areaChart.data.addAll(series1, series2, series3, series4, series5)

        return areaChart
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Kripta::class.java)
        }
    }
}