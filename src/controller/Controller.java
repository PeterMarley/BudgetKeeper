package controller;

import javafx.application.Application;
import model.db.DatabaseAccessObject;
import view.WindowMonth;
import view.WindowTransaction;
import view.WindowYear;

public class Controller {

	//**********************************\
	//									|
	//	GUI Windows						|
	//									|
	//**********************************/
	
	private static DatabaseAccessObject databaseAccessObject;
	private static WindowYear windowYear;
	private static WindowMonth windowMonth;
	private static WindowTransaction windowTransaction;

	//**********************************\
	//									|
	//	Main method						|
	//									|
	//**********************************/
	
	public static void main(String[] args) {
		try {
			setDatabaseAccessObject();
			Application.launch(WindowYear.class);
		} catch (Exception e) {
			System.err.println("EXCEPTION CAUGHT BY Controller.main()!");
			e.printStackTrace();
		}
	}

	//**********************************\
	//									|
	//	Setters							|
	//									|
	//**********************************/
	
	/**
	 * @param databaseAccessObject the databaseAccessObject to set
	 */
	public static void setDatabaseAccessObject() {
		Controller.databaseAccessObject = new DatabaseAccessObject();
	}

	/**
	 * @param windowYear the windowYear to set
	 */
	public static void setWindowYear(WindowYear windowYear) {
		Controller.windowYear = windowYear;
	}

	/**
	 * @param windowMonth the windowMonth to set
	 */
	public static void setWindowMonth(WindowMonth windowMonth) {
		Controller.windowMonth = windowMonth;
	}

	/**
	 * @param windowTransaction the windowTransaction to set
	 */
	public static void setWindowTransaction(WindowTransaction windowTransaction) {
		Controller.windowTransaction = windowTransaction;
	}

	//**********************************\
	//									|
	//	Getters							|
	//									|
	//**********************************/
	
	/**
	 * @return the databaseAccessObject
	 */
	public static DatabaseAccessObject getDatabaseAccessObject() {
		return databaseAccessObject;
	}

	/**
	 * @return the windowYear
	 */
	public static WindowYear getWindowYear() {
		return windowYear;
	}

	/**
	 * @return the windowMonth
	 */
	public static WindowMonth getWindowMonth() {
		return windowMonth;
	}

	/**
	 * @return the windowTransaction
	 */
	public static WindowTransaction getWindowTransaction() {
		return windowTransaction;
	}

}
