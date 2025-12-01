import java.io.Serializable;

/** Represents one boat in the fleet */
public class Boat implements Serializable {

    private BoatType type;
    private String name;
    private int year;
    private String makeModel;
    private int lengthFeet;
    private double purchasePrice;
    private double expenses;

    /** Makes a new boat with no expenses yet */
    public Boat(BoatType type, String name, int year,
                String makeModel, int lengthFeet, double purchasePrice) {
        this.type = type;
        this.name = name;
        this.year = year;
        this.makeModel = makeModel;
        this.lengthFeet = lengthFeet;
        this.purchasePrice = purchasePrice;
        this.expenses = 0.0;
    } // end of constructor

    public String getName() {
        return name;
    } // end of getName

    public double getPurchasePrice() {
        return purchasePrice;
    } // end of getPurchasePrice

    public double getExpenses() {
        return expenses;
    } // end of getExpenses

    /** Returns how much money is left to spend on this boat */
    public double getRemainingAllowance() {
        return purchasePrice - expenses;
    } // end of getRemainingAllowance

    /** Adds an expense if allowed */
    public boolean authorizeExpense(double amount) {
        double newTotal = expenses + amount;
        if (newTotal <= purchasePrice) {
            expenses = newTotal;
            return true;
        }
        return false;
    } // end of authorizeExpense

    /** Returns the formatted report line */
    @Override
    public String toString() {

        String typeStr = (type == BoatType.SAILING ? "SAILING" : "POWER");

        String left = String.format(
                "    %-7s %-20s %4d %-12s %2d' ",
                typeStr,
                name,
                year,
                makeModel,
                lengthFeet
        );

        String money = String.format(
                ": Paid $ %8.2f : Spent $ %8.2f",
                purchasePrice,
                expenses
        );

        return left + money;
    } // end of toString
} // end of class Boat