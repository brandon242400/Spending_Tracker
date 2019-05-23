import java.time.LocalDate;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;


class BudgetGraph {

    private Stage stage;
    private Scene scene;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private BudgetState currentState;
    private LineChart<String, Number> lineChart;
    private XYChart.Series<String, Number> series;
    private XYChart.Series<String, Number> budgetLimitSeries;
    private LocalDate startDate;

    public BudgetGraph(BudgetState currentState) {
        stage = new Stage();
        stage.setTitle("Budget Chart");

        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Date");

        this.currentState = currentState;
        lineChart = new LineChart<>(xAxis, yAxis);
        startDate = this.currentState.getStartDate();

        series = new XYChart.Series<>();
        series.setName("Spending");
        budgetLimitSeries = new XYChart.Series<>();
        budgetLimitSeries.setName("Budget");

        scene = new Scene(lineChart, 1500, 500);
        stage.setScene(scene);

        chartSetup();
    }

    public void chartSetup() {
        lineChart.setTitle("Spending Info Starting from " + startDate);

        addData();

        lineChart.getData().add(series);
        lineChart.getData().add(budgetLimitSeries);

        stage.show();
    }

    public void addData() {
        BudgetQueue.Node node = currentState.getQueue().first;
        LocalDate date;
        double amount = 0;

        while(node.next != null) {

            date = LocalDate.parse(node.date);
            amount += node.element;
            while(node.next.date != null) {
                if(!node.next.date.equals(date.toString()))
                    break;
                node = node.next;
                amount += node.element;
            }
            series.getData().add(new XYChart.Data<>(date.toString().substring(5), amount));
            budgetLimitSeries.getData().add(new XYChart.Data<>(date.toString().substring(5), currentState.getBudgetLimit()));

            node = node.next;
            //amount = 0;
        }
    }
}