import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/** Main class to manage the boat fleet */
public class FleetManagement {

    private static final String DB_FILENAME = "FleetData.db";
    private static final Scanner keyboard = new Scanner(System.in);

    /** Main method with menu loop */
    public static void main(String[] args) {

        System.out.println("Welcome to the Fleet Management System");
        System.out.println("--------------------------------------");
        System.out.println();

        ArrayList<Boat> fleet;

        if (args.length > 0) {
            fleet = loadFromCsv(args[0]); // first run
        } else {
            fleet = loadFromDb(); // later runs
        }

        char choice;

        do {
            System.out.print("(P)rint, (A)dd, (R)emove, (E)xpense, e(X)it : ");
            String text = keyboard.nextLine().trim();
            choice = text.isEmpty() ? ' ' : Character.toUpperCase(text.charAt(0));

            if (choice == 'P') {
                printFleetReport(fleet);
            } else if (choice == 'A') {
                addBoat(fleet);
            } else if (choice == 'R') {
                removeBoat(fleet);
            } else if (choice == 'E') {
                handleExpense(fleet);
            } else if (choice != 'X') {
                System.out.println("Invalid menu option, try again");
            }

        } while (choice != 'X'); // end of menu loop

        saveToDb(fleet);
        System.out.println("\nExiting the Fleet Management System");
    } // end of main

    /** Loads boats from a CSV file */
    private static ArrayList<Boat> loadFromCsv(String filename) {
        ArrayList<Boat> fleet = new ArrayList<>();

        try (Scanner fileScan = new Scanner(new FileInputStream(filename))) {

            while (fileScan.hasNextLine()) {
                String[] parts = fileScan.nextLine().trim().split(",");
                if (parts.length != 6) continue;

                BoatType type = BoatType.valueOf(parts[0].trim().toUpperCase());
                String name = parts[1].trim();
                int year = Integer.parseInt(parts[2].trim());
                String make = parts[3].trim();
                int length = Integer.parseInt(parts[4].trim());
                double price = Double.parseDouble(parts[5].trim());

                fleet.add(new Boat(type, name, year, make, length, price));
            }
        } catch (Exception e) {
            System.out.println("Could not load CSV file.");
        }

        return fleet;
    } // end of loadFromCsv

    /** Saves boats to DB file */
    private static void saveToDb(ArrayList<Boat> fleet) {

        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(DB_FILENAME))) {
            out.writeObject(fleet);
        } catch (IOException e) {
            System.out.println("Error saving DB file.");
        }
    } // end of saveToDb

    /** Loads boats from DB or empty first time */
    @SuppressWarnings("unchecked")
    private static ArrayList<Boat> loadFromDb() {
        ArrayList<Boat> fleet = new ArrayList<>();

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(DB_FILENAME))) {
            fleet = (ArrayList<Boat>) in.readObject();
        } catch (Exception e) {
            // first run OK
        }

        return fleet;
    } // end of loadFromDb

    /** Prints report with totals */
    private static void printFleetReport(ArrayList<Boat> fleet) {
        System.out.println();
        System.out.println("Fleet report:");

        double totalPaid = 0.0;
        double totalSpent = 0.0;

        for (Boat b : fleet) {
            System.out.println(b);
            totalPaid += b.getPurchasePrice();
            totalSpent += b.getExpenses();
        }

        String left = "    Total                                             ";
        String money = String.format(": Paid $ %8.2f : Spent $ %8.2f", totalPaid, totalSpent);

        System.out.println(left + money);
        System.out.println();
    } // end of printFleetReport

    /** Adds a boat from one CSV text line */
    private static void addBoat(ArrayList<Boat> fleet) {
        System.out.print("Please enter the new boat CSV data          : ");
        String[] parts = keyboard.nextLine().trim().split(",");

        BoatType type = BoatType.valueOf(parts[0].trim().toUpperCase());
        fleet.add(new Boat(type,
                parts[1].trim(),
                Integer.parseInt(parts[2].trim()),
                parts[3].trim(),
                Integer.parseInt(parts[4].trim()),
                Double.parseDouble(parts[5].trim())));
    } // end of addBoat

    /** Removes one boat by name */
    private static void removeBoat(ArrayList<Boat> fleet) {
        System.out.print("Which boat do you want to remove?           : ");
        String name = keyboard.nextLine().trim();

        Boat b = findBoat(fleet, name);
        if (b == null) System.out.println("Cannot find boat " + name);
        else fleet.remove(b);
    } // end of removeBoat

    /** Adds an expense to a boat */
    private static void handleExpense(ArrayList<Boat> fleet) {
        System.out.print("Which boat do you want to spend on?         : ");
        Boat b = findBoat(fleet, keyboard.nextLine().trim());

        if (b == null) {
            System.out.println("Cannot find boat");
            return;
        }

        System.out.print("How much do you want to spend?              : ");
        double amount = Double.parseDouble(keyboard.nextLine());

        if (b.authorizeExpense(amount)) {
            System.out.printf("Expense authorized, $%.2f spent.%n", b.getExpenses());
        } else {
            System.out.printf("Expense not permitted, only $%.2f left to spend.%n",
                    b.getRemainingAllowance());
        }
    } // end of handleExpense

    /** Finds a boat by name (not case sensitive) */
    private static Boat findBoat(ArrayList<Boat> fleet, String name) {
        for (Boat b : fleet) {
            if (b.getName().equalsIgnoreCase(name)) return b;
        }
        return null;
    } // end of findBoat
} // end of class FleetManagement