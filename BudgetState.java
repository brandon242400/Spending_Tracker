import java.io.*;
import java.text.DecimalFormat;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.time.LocalDate;


class BudgetState implements Serializable {

    private static final long serialVersionUID = 32L;
    private BudgetQueue queue;
    private String fileName;
    private String userName;
    private Double budgetLimit;
    private Double totalSpent;
    private LocalDate startDate;
    private LocalDate lastEntryDate;

    public BudgetState() {
        fileName = "saved.ser";
        startDate = LocalDate.now();
        budgetLimit = 0.0;
        userName = "User";
        totalSpent = 0.0;
        queue = new BudgetQueue();
    }

    public void save() {
        try {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(this);
        out.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setBudgetLimit(double amount) { budgetLimit = amount; }

    public Double getBudgetLimit() { return budgetLimit; }

    public void setUserName(String name) { userName = name; }

    public String getUserName() { return userName; }

    public Double getTotalSpent() { return totalSpent; }

    private LocalDate getDate() {
        return LocalDate.now();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void spend(double value) { spend(value, null); }

    public void spend(double value, String message) {
        String date = getDate().toString();
        queue.queue(value, message, date);
        totalSpent += value;
        lastEntryDate = LocalDate.now();
    }

    public void removeLastAction() {
        double element;
        try {
            element = queue.dequeueLast();
            totalSpent -= element;
        }
        catch(EmptyListException ex) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Heads up");
            alert.setHeaderText("There are no transactions for you to revert");
            alert.setContentText("The list of transactions you've made is empty");
            alert.showAndWait();
        }
    }

    public Double calculateTotalSpent() {
        Double result = 0.;

        BudgetQueue.Node temp = queue.first;
        while(temp != null) {
            result += temp.element;
            temp = temp.next;
        }

        return result;
    }

    public String[] getRecentTransactions() {
        String entry = "";
        BudgetQueue.Node node;
        DecimalFormat dformat = new DecimalFormat("0.00");
        int count = 0;
        String[] result = {null, null, null};

        if(queue.last == null) {
            node = null;
        }
        else {
            node = queue.last.previous;
        }

        while(node != null && count < 3) {
            entry += node.date.substring(5, 7) + "/" + node.date.substring(8) + "  -  $";
            entry += dformat.format(node.element) + "\n";
            if(node.description.length() > 40) {
                entry += node.description.substring(0, 40) + "...";
            }
            else 
                entry += node.description;
            
            result[count] = entry;
            entry = "";
            node = node.previous;
            count++;
        }
        
        return result;
    }

    public LocalDate getEndDate() {
        return lastEntryDate;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public BudgetQueue getQueue() {
        return queue;
    }
}