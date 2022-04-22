package controller;

import javafx.application.Application;
import model.db.DatabaseAccessObject;
import view.WindowYear;

public class Controller {

	private static DatabaseAccessObject dao;

	public static void main(String[] args) {
		try {
			dao = new DatabaseAccessObject();
			Application.launch(WindowYear.class);
		} catch (Exception e) {
			System.err.println("EXCEPTION CAUGHT BY Controller.main()!");
			e.printStackTrace();
		}
	}

	public static DatabaseAccessObject getDAO() {
		return dao;
	}
}
