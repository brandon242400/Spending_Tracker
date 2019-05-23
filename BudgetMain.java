import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.io.*;
import java.text.DecimalFormat;

public class BudgetMain extends Application {

    // Variables and objects being declared for the application to use
    static BudgetState currentState;
    static DecimalFormat dformat = new DecimalFormat("0.00");
    static Double amountSpent;
    Label recentTransaction1 = new Label();
    Label recentTransaction2 = new Label();
    Label recentTransaction3 = new Label();
    GridPane grid;
    GridPane optionGrid;
    Scene primaryScene;
    Scene optionScene;

    public static void main(String[] args) {
        String savedFileName = "saved.ser";

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(savedFileName));
            currentState = (BudgetState) in.readObject();
            in.close();
        } catch (Exception e) {
            System.out.println("No saved state");
            currentState = new BudgetState();
        }

        launch(args);
    }


    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Spending Tracker");

        // Setting up GridPane layout for main scene
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 20, 10));
        grid.setAlignment(Pos.BASELINE_LEFT);

        // Primary scene setup with grid and size
        primaryScene = new Scene(grid);
        Scene scene = primaryScene;
        int row = 0; // Row count for items

        if(true) {

        // Welcome Label
        Label welcome = new Label("Welcome back " + currentState.getUserName());
        welcome.setFont(Font.font("Times new roman", FontWeight.BOLD, FontPosture.ITALIC, 18));
        welcome.setUnderline(true);
        grid.add(welcome, 5, 0, 3, 1);

        // Adding the budget limit display
        Label budgetLimitLabel = new Label("Budget Limit:");
        budgetLimitLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 15));
        grid.add(budgetLimitLabel, 0, row, 2, 1);
        Label budgetAmount = new Label("$" + dformat.format(currentState.getBudgetLimit()).toString());
        budgetAmount.setFont(Font.font("Times new roman", FontWeight.SEMI_BOLD, 15));
        grid.add(budgetAmount, 2, row++);

        // Adding the 'amount spent' display
        Label totalSpentLabel = new Label("Amount Spent:");
        totalSpentLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 15));
        grid.add(totalSpentLabel, 0, row, 2, 1);
        Label spentAmount = new Label("$" + dformat.format(currentState.getTotalSpent()).toString());
        spentAmount.setFont(Font.font("Times new roman", FontWeight.SEMI_BOLD, 15));
        grid.add(spentAmount, 2, row++);

        // Adding 'budget remaining' section
        Label budgetRemaining = new Label("Budget Remaining:");
        budgetRemaining.setFont(Font.font("Times new roman", FontWeight.BOLD, 15));
        grid.add(budgetRemaining, 0, row, 2, 1);
        Label remainingAmount = new Label("$" + dformat.format(currentState.getBudgetLimit() - currentState.getTotalSpent()).toString());
        remainingAmount.setFont(Font.font("Times new roman", FontWeight.SEMI_BOLD, 15));
        grid.add(remainingAmount, 2, row++);

        // Section to make a transaction in the application
        Label transactionLabel = new Label("New transaction entry:");
        transactionLabel.setFont(Font.font("Times new roman", FontWeight.BOLD, 18));
        transactionLabel.setUnderline(true);
        grid.add(transactionLabel, 5, row++, 2, 1);
        TextField spentField = new TextField();
        spentField.setFont(Font.font("Times new roman", 12));
        grid.add(spentField, 5, row++, 2, 1);
        TextArea description = new TextArea();
        description.setFont(Font.font("Times new roman", 12));
        description.setPrefSize(100, 100);
        description.setWrapText(true);
        grid.add(description, 5, row++, 2, 3);
        row += 2;
        Button addAction = new Button("Enter transaction");
        addAction.setFont(Font.font("Times new roman", 15));
        addAction.setOnAction(e -> {
            double spentValue;
            try {
                spentValue = Double.parseDouble(spentField.getText());
                String des = description.getText();
                
                try {
                    if(des.charAt(0) == '\n')
                        des = des.substring(1); 
                }
                catch(StringIndexOutOfBoundsException except) {}
                
                currentState.spend(spentValue, des);
                spentAmount.setText("$" + dformat.format(currentState.getTotalSpent()).toString());
                remainingAmount.setText("$" + dformat.format(currentState.getBudgetLimit() - currentState.getTotalSpent()).toString());
                spentField.clear();
                description.clear();
                displayPastTransactions();
                save();
            }
            catch(Exception ex) {
                spentField.clear();
                spentField.setText("Only numbers here");
            }
        });
        grid.add(addAction, 5, row++);
        spentField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    addAction.fire();
                }
            }
        });
        description.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    addAction.fire();
                    spentField.requestFocus();
                }
            }
        });

        // Adding the 'remove last' button
        Button removeLast = new Button("Undo last");
        removeLast.setOnAction(e -> {
            currentState.removeLastAction();
            spentAmount.setText("$" + dformat.format(currentState.getTotalSpent()).toString());
            remainingAmount.setText("$" + dformat.format(currentState.getBudgetLimit() - currentState.getTotalSpent()).toString());
            displayPastTransactions();
            save();
        });
        removeLast.setFont(Font.font("Times new roman", 15));
        row = 4;
        grid.add(removeLast, 1, --row);               }

        // Button that takes you to the 'options' scene
        Button options = new Button("Settings");
        options.setOnAction(e -> options(primaryStage, scene));
        options.setFont(Font.font("Times new roman", 16));
        grid.add(options, 0, row);

        // Calling function that sets up recent transaction display
        grid.add(recentTransaction1, 0, 5, 3, 1);
        grid.add(recentTransaction2, 0, 6, 3, 1);
        grid.add(recentTransaction3, 0, 7, 3, 1);
        Label recent = new Label("Recent Transactions");
        recent.setFont(Font.font("times new roman", FontWeight.BOLD, 14));
        recent.setUnderline(true);
        grid.add(recent, 0, 4, 2, 1);
        displayPastTransactions();

        // Graph button
        Button graph = new Button("Graph");
        graph.setFont(Font.font("times new roman", 16));
        graph.setOnAction(e -> {
            if(currentState.isEmpty()) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Heads up");
                alert.setHeaderText("There is nothing to display");
                alert.setContentText("Add some transactions to the application to use the graph function");
                alert.showAndWait();
            }
            else
                new BudgetGraph(currentState);
        });
        grid.add(graph, 0, 8, 1, 1);

        // Show all past transactions button
        Button showAll = new Button("All transactions");
        showAll.setFont(Font.font("times new roman", 15));
        showAll.setOnAction(e -> displayAllTransactions());
        grid.add(showAll, 1, 8, 2, 1);

        // Exit button
        Button exit = new Button("Exit");
        exit.setFont(Font.font("times new roman", 15));
        exit.setOnAction(e -> System.exit(0));
        grid.add(exit, 6, 8);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void options(Stage stage, Scene mainScene) {
        stage.setTitle("Settings");

        // Setting up grid for options scene
        optionGrid = new GridPane();
        GridPane grid = optionGrid;
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setAlignment(Pos.BASELINE_LEFT);

        // Scene setup. Calling optionScene 'scene' for ease of use
        optionScene = new Scene(grid, 300, 300);
        Scene scene = optionScene;

        // Creating BACK button
        Button back = new Button("Go Back");
        back.setOnAction(e -> {
            try {
                start(stage);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        back.setFont(Font.font("Times new roman", 13));
        grid.add(back, 0, 25);

        // Text Field and Button to change budgeted amount
        TextField budgetEntry = new TextField();
        budgetEntry.setFont(Font.font("Times new roman", 12));
        budgetEntry.setMaxWidth(120);
        grid.add(budgetEntry, 0, 1);
        Label budgetL = new Label("Budget limit entry:");
        budgetL.setUnderline(true);
        budgetL.setFont(Font.font("Times new roman", FontWeight.SEMI_BOLD, 14));
        grid.add(budgetL, 0, 0);
        Button budgetButton = new Button("Enter");
        budgetButton.setOnAction(e -> {
            try {
                String num = budgetEntry.getText();
                Double amount = Double.parseDouble(num);
                currentState.setBudgetLimit(amount);
                budgetEntry.clear();
                save();
            }
            catch(NumberFormatException ex) {
                budgetEntry.setText("Must enter number");
            }
        });
        grid.add(budgetButton, 2, 1);

        // USER NAME ENTRY
        TextField userNameEntry = new TextField();
        userNameEntry.setFont(Font.font("Times new roman", 12));
        userNameEntry.setMaxWidth(120);
        grid.add(userNameEntry, 0, 3);
        Label nameL = new Label("User name entry:");
        nameL.setUnderline(true);
        nameL.setFont(Font.font("Times new roman", FontWeight.SEMI_BOLD, 14));
        grid.add(nameL, 0, 2);
        Button userNameButton = new Button("Enter");
        userNameButton.setOnAction(e -> {
            currentState.setUserName(userNameEntry.getText());
            userNameEntry.clear();
            save();
        });
        grid.add(userNameButton, 2, 3);

        // Button to reset all data
        Button resetButton = new Button("Reset all entered information");
        resetButton.setFont(Font.font("Times new roman", FontWeight.SEMI_BOLD, 14));
        resetButton.setOnAction(e -> {
            currentState = new BudgetState();
            save();
        });
        grid.add(resetButton, 0, 4, 2, 1);


        stage.setScene(scene);

    }


    public void save() { currentState.save(); }


    public void displayPastTransactions() {

        String[] transactions = currentState.getRecentTransactions();

        if(transactions[0] != null) {
            recentTransaction1.setText(transactions[0]);
            recentTransaction1.setFont(Font.font("times new roman", 12));
        }
        else
            recentTransaction1.setText("No transactions to display");
        if(transactions[1] != null) {
            recentTransaction2.setText(transactions[1]);
            recentTransaction2.setFont(Font.font("times new roman", 12));
        }
        else
            recentTransaction2.setText(null);
        if(transactions[2] != null) {
            recentTransaction3.setText(transactions[2]);
            recentTransaction3.setFont(Font.font("times new roman", 12));
        }
        else
            recentTransaction3.setText(null);
    }


    public void displayAllTransactions() {
        Stage stage = new Stage();
        VBox box = new VBox();
        String info;
        BudgetQueue.Node node = null;
        Text text;

        try {
            node = currentState.getQueue().last.previous;
        }
        catch(NullPointerException ex) {
            box.getChildren().add(new Label("No transactions found"));
        }

        while(node != null) {
            info = node.date + " - $" + node.element + "\n\t" + node.description;
            if(node.description.length() > 0)
                info += "\n";
            text = new Text();
            text.setText(info);
            text.setFont(Font.font("Times new roman", FontWeight.SEMI_BOLD, 14));
            text.setWrappingWidth(275);
            box.getChildren().add(text);
            node = node.previous;
        }

        ScrollPane sp = new ScrollPane(box);
        Scene scene = new Scene(sp, 300, 400);
        stage.setScene(scene);
        stage.show();
    }

}