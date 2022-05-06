package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Application;
import log.Logger;
import model.db.DatabaseAccessObject;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.comparators.MonthComparatorDate;
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

	private static HashMap<Month, Boolean> mapToSave;

	/**
	 * Main data for program
	 */
	private static List<Month> months;

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

	/**
	 * Load data from database
	 */
	private static void loadData() {
		months = dao.queryMonths();
		generateMaps(months);
	}
	

	
	public static void saveData(Month m) {
		dao.saveData(m);
	}

	private static void generateMaps(List<Month> months) {
		mapOfYears = new HashMap<Integer, List<Month>>();
		mapToSave = new HashMap<Month, Boolean>();
		for (Month month : months) {
			int year = month.getDate().getYear();
			if (!mapOfYears.containsKey(month.getDate().getYear())) {
				mapOfYears.put(year, new ArrayList<Month>(12));
			}
			mapToSave.put(month, false);
			mapOfYears.get(year).add(month);
		}
	}

	/**
	 * // * Get all Months for a specific year.
	 * // *
	 * // * @param year
	 * // * @return
	 * //
	 */
	//	public static List<Month> getMonths(int year) {
	//		List<Month> returnList = new ArrayList<Month>(12);
	//		for (Month month : mapOfAllMonths.keySet()) {
	//			if (month.getDate().getYear() == year) {
	//				returnList.add(month);
	//			}
	//		}
	//		Collections.sort(returnList, new MonthComparatorDate());
	//		return returnList;
	//	}

	/**
	 * Get the {@link controller.Controller#mapOfYears mapOfYears}
	 * 
	 * @return
	 */
	//	public static HashMap<Integer, List<Month>> getMapOfYears() {
	//		return mapOfYears;
	//	}

	//	public static List<Month> getMonths() {
	//		return new ArrayList<Month>(mapOfAllMonths.keySet());
	//	}

	public static void updateMonth(Month original, Month edited) {
		months.remove(original);
		months.add(edited);
		mapToSave.put(edited, true);
	}

	//**********************************\
	//									|
	//	Data Manipulation				|
	//									|
	//**********************************/

	//	private static void saveData() {
	//		dao.saveData(mapOfAllMonths);
	//	}

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
		return months;
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
