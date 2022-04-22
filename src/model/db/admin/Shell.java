package model.db.admin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import model.db.DatabaseAccessObject;
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

	/**
	 * Start point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// CREATE MONTHS
		int currentYear = LocalDate.now().getYear();
		Month m1 = new Month(LocalDate.of(currentYear, 5, 1));
		Month m1b = new Month(LocalDate.of(currentYear - 1, 6, 1));
		Month m2 = new Month(LocalDate.of(2015, 1, 1));
		Month m3 = new Month(LocalDate.of(2015, 2, 20));
		Month m4 = new Month(LocalDate.of(2021, 1, 29));
		Month m4b = new Month(LocalDate.of(2021, 1, 6));
		// CREATE TRANSACTIONS
		Transaction m1t1 = new Transaction("T1", true, m1.getDate(), true, Type.CASH, 15.00);
		Transaction m1t2 = new Transaction("T2", false, m1.getDate(), false, Type.DIRECT_DEBIT, 20.20);
		Transaction m1t3 = new Transaction("T3", true, m1.getDate(), true, Type.STANDING_ORDER, 30.30);
		Transaction m1t4 = new Transaction("T3", false, m1.getDate(), true, Type.STANDING_ORDER, 500.1);

		// ADD TRANSACTIONS TO MONTHS
		m1.addTransaction(m1t1);
		m1.addTransaction(m1t2);
		m1.addTransaction(m1t3);
		m1.addTransaction(m1t4);
		testMonths = new ArrayList<Month>();
		testMonths.add(m1);
		testMonths.add(m2);
		testMonths.add(m3);
		testMonths.add(m4);
		testMonths.add(m4b);
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
				DatabaseAdministration.createTables();
				System.out.println(DIV);
				return true;
			}
		},
		DROP_TABLES("Drop Tables") {
			public boolean menuAction() {
				System.out.println(DIV);
				System.out.println("Dropping Tables...");
				DatabaseAdministration.dropTables();
				System.out.println(DIV);
				return true;
			}
		},
		ADD_TEST_DATA("Add Test Data") {
			public boolean menuAction() {
				for (Month m : testMonths) {
					DatabaseAdministration.dao.addMonth(m);
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
			for (Menu m : menu) {
				System.out.println(m.toString());
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
	 * @return the int selected by user.
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
