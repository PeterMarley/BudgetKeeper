package view.windows;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.controllers.WindowYearController;

public class WindowYear {

	private FXMLLoader loader;
	private Parent root;
	private Stage stage;
	private Scene scene;
	private WindowYearController controller;

	private static final String FXML = "../fxml/WindowYear.fxml";
	
	public WindowYear() {
		try {
			
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
		this.controller.initialise(LocalDate.now().getYear()); // default to showing the current year
		this.stage.show();
	}
	
	public void hide() {
		this.stage.hide();
	}
	
	
}
