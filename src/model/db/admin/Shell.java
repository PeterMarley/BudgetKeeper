package model.db.admin;

import java.util.InputMismatchException;
import java.util.Scanner;

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

	/**
	 * Start point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
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
			for (Menu m : menu) {
				System.out.println(m.toString());
			}
			int selection = getInt("Please select a menu item by entering a number:", "Sorry you must enter a number between 1 and " + Menu.values().length + ".", 1, Menu.values().length);
			toContinue = menu[selection - 1].menuAction();
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
