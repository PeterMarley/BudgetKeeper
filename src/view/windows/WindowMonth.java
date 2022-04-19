package view.windows;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.domain.Month;
import view.controllers.WindowMonthController;
import view.controllers.WindowYearController;

public class WindowMonth {

	private Month m;
	
	
	private FXMLLoader loader;
	private Parent root;
	private Stage stage;
	private Scene scene;
	private WindowMonthController controller;

	private static final String FXML = "../fxml/WindowMonth.fxml";
	
	public WindowMonth(Month m) {
		try {
			this.m = m;
			this.loader = new FXMLLoader(getClass().getResource(FXML)); 
			this.root = loader.load();
			this.controller = loader.getController();

			this.scene = new Scene(root);
			this.stage = new Stage();

			this.stage.setScene(scene);
			

			
		} catch (IOException e) {
			System.err.println("FXMLLoader.load IOException:");
			e.printStackTrace();
		}
	}
	
	public void show() {
		this.controller.initialise(m); // default to showing the current year
		this.stage.show();
	}
	
	public void hide() {
		this.stage.hide();
	}
	
	
}