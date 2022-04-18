package controller;
import javafx.application.Application;
import model.db.DatabaseAccessObject;
import view.View;

public class Controller {
	
	private static DatabaseAccessObject dao;
	
	public static void main(String[] args) {
		dao = new DatabaseAccessObject();
		Application.launch(View.class);
	}
	
	public static DatabaseAccessObject getDAO() {
		return dao;
	}
}
