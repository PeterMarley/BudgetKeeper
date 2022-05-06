package model.db.admin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;

/**
 * This program gives quick administrator access to important database functions without needing to engage with the sqlite3 shell or the main
 * BudgetKeeper program.
 * 
 * @author Peter Marley
 * @StudentNumber 13404067
 * @Email pmarley03@qub.ac.uk
 * @GitHub https://github.com/PeterMarley
 *
 */
public class Shell {
	private static List<Month> testMonths;
	private static final DatabaseAdministration DAO = new DatabaseAdministration();

	/**
	 * Start point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// CREATE MONTHS
		Month month2022May = new Month(LocalDate.of(2022, 5, 1));
		Month month2022Apr = new Month(LocalDate.of(2022, 4, 1));
		Month month2015Jan = new Month(LocalDate.of(2015, 1, 1));
		Month month2015Feb = new Month(LocalDate.of(2015, 2, 20));
		Month month2021Jan = new Month(LocalDate.of(2021, 1, 29));
		Month month2021Dec = new Month(LocalDate.of(2021, 12, 6));
		// CREATE TRANSACTIONS
		Transaction trans2022May1 = new Transaction("Shopping", true, LocalDate.of(2022, 5, 1), false, Type.CASH, 15.00);
		Transaction trans2022May2 = new Transaction("Spotify", false, LocalDate.of(2022, 5, 2), false, Type.DIRECTDEBIT, 20.20);
		Transaction trans2022May3 = new Transaction("Laptop Finance", true, LocalDate.of(2022, 5, 3), false, Type.STANDINGORDER, 30.30);
		Transaction trans2022May4 = new Transaction("Loan Repayment", false, LocalDate.of(2022, 5, 4), false, Type.STANDINGORDER, 500.1);
		Transaction trans2022May5 = new Transaction("Rent and Owed", false, LocalDate.of(2022, 5, 5), false, Type.BANKTRANSFER, 230.55);
		Transaction trans2022May6 = new Transaction("Salary", true, LocalDate.of(2022, 5, 6), true, Type.BANKTRANSFER, 1450.99);

		// ADD TRANSACTIONS TO MONTHS
		month2022May.addTransaction(trans2022May1);
		month2022May.addTransaction(trans2022May2);
		month2022May.addTransaction(trans2022May3);
		month2022May.addTransaction(trans2022May4);
		month2022May.addTransaction(trans2022May5);
		month2022May.addTransaction(trans2022May6);

		testMonths = new ArrayList<Month>();
		testMonths.add(month2015Jan);
		testMonths.add(month2015Feb);
		testMonths.add(month2021Jan);
		testMonths.add(month2021Dec);
		testMonths.add(month2022Apr);
		testMonths.add(month2022May);
		menu();
	}

	private static final String DIV = "================================================";

	/**
	 * Menu for Database Shell.
	 */
	private enum Menu {

		CREATE_TABLES("Create Tables") {
			public boolean menuAction() {
				System.out.println(DIV);
				System.out.println("Creating Tables...");
				DAO.createTables();
				System.out.println(DIV);
				return true;
			}
		},
		DROP_TABLES("Drop Tables") {
			public boolean menuAction() {
				System.out.println(DIV);
				System.out.println("Dropping Tables...");
				DAO.dropTables();
				System.out.println(DIV);
				return true;
			}
		},
		ADD_TEST_DATA("Add Test Data") {
			public boolean menuAction() {
				for (Month m : testMonths) {
					DAO.addMonth(m);
				}
				return true;
			}
		},
		QUIT("Quit") {
			public boolean menuAction() {
				System.out.println(DIV);
				System.out.println("Qutting...");
				System.out.println(DIV);
				return false;
			}
		};

		private String menuText;

		private Menu(String menuText) {
			this.menuText = menuText;
		}

		public String toString() {
			return (this.ordinal() + 1) + ". " + this.menuText;
		}

		public boolean menuAction() {
			System.out.println("Not implemented");
			return true;
		}
	}

	/**
	 * Display menu, get user selection, and begin appropriate menu action
	 */
	public static void menu() {
		Menu[] menu = Menu.values();
		boolean toContinue = true;
		while (toContinue) {
			System.out.println(DIV);
			for (Menu item : menu) {
				System.out.println(item.toString());
			}
			System.out.println(DIV);
			int selection = getInt("Please select a menu item by entering a number:", "Sorry you must enter a number between 1 and " + Menu.values().length + ".", 1, Menu.values().length);
			toContinue = menu[selection - 1].menuAction();
			System.out.println(DIV);
		}
	}

	/**
	 * Get an int from user between {@code lowerBound} and {@code upperBound} (inclusive). Display {@code prompt} to user, then if invalid selection is
	 * made, display {@code errMsg) prior to each subsequent user selection. Repeat until a valid int is selected.
	 * @param prompt
	 * 
	 * @param errMsg
	 * @param lowerBound
	 * @param upperBound
	 * @return the selected int, validated range.
	 */
	private static int getInt(String prompt, String errMsg, int lowerBound, int upperBound) {
		Scanner scanner = new Scanner(System.in);
		int selection = 0;
		boolean accepted = false;
		boolean first = true;

		System.out.println(prompt);
		while (!accepted) {
			if (first) {
				first = false;
			} else {
				System.out.println(errMsg);
			}
			try {
				selection = scanner.nextInt();
				if (selection >= lowerBound && selection <= upperBound) {
					accepted = true;
				}
			} catch (InputMismatchException notIntEx) {
				scanner.nextLine();
			}
		}
		return selection;
	}
}
