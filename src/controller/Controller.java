package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.db.DatabaseAccessObject;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Utility;
import view.WindowMonth;
import view.WindowTransaction;
import view.WindowYear;

public class Controller {

	//**********************************\
	//									|
	//	GUI Windows						|
	//									|
	//**********************************/

	private static WindowYear windowYear;
	private static WindowMonth windowMonth;
	private static WindowTransaction windowTransaction;

	//**********************************\
	//									|
	//	Instance Fields					|
	//									|
	//**********************************/

	/**
	 * 
	 * <b>KEY:</b> year<br>
	 * <b>VALUE:</b> list of Months for that year
	 */
	private static HashMap<Integer, List<Month>> mapOfYears;

	//private static HashMap<Month, Boolean> mapToSave;

	/**
	 * Main data for program
	 */
	//private static List<Month> months;
	private static ObservableList<Month> obsMonths;

	/**
	 * Database access object
	 */
	private static DatabaseAccessObject dao;

	//**********************************\
	//									|
	//	Main method						|
	//									|
	//**********************************/

	/**
	 * Program start point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			setDAO();
			loadData();
			Application.launch(WindowYear.class);
		} catch (Exception e) {
			System.err.println("EXCEPTION CAUGHT BY Controller.main()!");
			e.printStackTrace();
		}
	}

	//**********************************\
	//									|
	//	Data Manipulation				|
	//									|
	//**********************************/

	/**
	 * Load data from database, and maps each Month to the year value. Each year key is associated with a value of a List of Months.
	 */
	private static void loadData() {
		obsMonths = FXCollections.observableArrayList(dao.loadData());
		generateMaps(obsMonths);
	}

	/**
	 * Save a month to database
	 * 
	 * @param m
	 */
	public static void saveData(Month m) {
		dao.saveData(m);
	}
	
	public static void removeUnusedMonth(Month m) {
		dao.deleteMonth(m);
	}

	/**
	 * Generate a {@code HashMap<Integer, List<Month>>} of year values to {@code List} of {@link model.domain.Month Months} for that year
	 * 
	 * @param months
	 */
	private static void generateMaps(List<Month> months) {
		mapOfYears = new HashMap<Integer, List<Month>>();
		//mapToSave = new HashMap<Month, Boolean>();
		for (Month month : Utility.nullCheck(months)) {
			int year = month.getDate().getYear();
			if (!mapOfYears.containsKey(month.getDate().getYear())) {
				mapOfYears.put(year, new ArrayList<Month>(12));
			}
			//mapToSave.put(month, false);
			mapOfYears.get(year).add(month);
		}
	}

	/**
	 * Update a month in the observable list, from which the JavaFX GUI draws its data. 
	 * @param original
	 * @param edited
	 */
	public static void updateMonth(Month original, Month edited) {
		obsMonths.remove(original);
		obsMonths.add(edited);
		//mapToSave.put(edited, true);
	}

	//**********************************\
	//									|
	//	Setters							|
	//									|
	//**********************************/

	/**
	 * Set the database access object
	 */
	private static void setDAO() {
		dao = new DatabaseAccessObject();
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

	public static List<Month> getData() {
		loadData();
		return obsMonths;
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

	//**********************************\
	//									|
	//	Utility							|
	//									|
	//**********************************/

	public static void print(List<Month> months) {
		System.out.println("=========================");
		for (Month mo : months) {
			System.out.println(mo.toString());
			for (Transaction tr : mo.getTransactions()) {
				System.out.println(tr.toString());
			}
		}
		System.out.println("=========================");
	}

}
