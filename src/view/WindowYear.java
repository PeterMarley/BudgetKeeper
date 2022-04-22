package view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import controller.Controller;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.db.Constants;
import model.domain.Month;

public class WindowYear extends Application {

	//**********************************\
	//									|
	//	Scene Graph objects				|
	//									|
	//**********************************/

	private FXMLLoader loader;
	private Parent root;
	private Stage stage;
	private Scene scene;

	//**********************************\
	//									|
	//	Instance Fields					|
	//									|
	//**********************************/

	private HashMap<Integer, Month> monthMap;
	private int lastSelectedYear = DEFAULT_YEAR;

	//**********************************\
	//									|
	//	Constants						|
	//									|
	//**********************************/

	private static final String FXML = "./fxml/WindowYear.fxml";
	private static final int DEFAULT_YEAR = LocalDate.now().getYear();

	//**********************************\
	//									|
	//	JavaFX Controls					|
	//									|
	//**********************************/

	@FXML private TextField year;
	@FXML private Button monthJan;
	@FXML private Button monthFeb;
	@FXML private Button monthMar;
	@FXML private Button monthMay;
	@FXML private Button monthApr;
	@FXML private Button monthJun;
	@FXML private Button monthJul;
	@FXML private Button monthAug;
	@FXML private Button monthSep;
	@FXML private Button monthOct;
	@FXML private Button monthNov;
	@FXML private Button monthDec;
	@FXML private Button yearSelect;

	//**********************************\
	//									|
	//	Construction/ Start				|
	//									|
	//**********************************/

	/**
	 * Initialise and configure this {@code WindowYear} object, and show.
	 */
	@Override
	public void start(Stage generatedStage) throws Exception {
		// assign generatedStage to instance field stage
		this.stage = generatedStage;
		try {

			// load FXML file
			this.loader = new FXMLLoader(getClass().getResource(FXML));

			// set controller
			this.loader.setController(this);

			// add scene-graph to stage
			this.root = loader.load();
			this.scene = new Scene(root);
			this.stage.setScene(scene);

		} catch (IOException e) {
			System.err.println("FXMLLoader.load IOException:");
			e.printStackTrace();
		}

		// show this window
		refresh(); // default to showing the current year
		this.stage.show();
	}

	//**********************************\
	//									|
	//	JavaFX Application Methods		|
	//									|
	//**********************************/

	/**
	 * Refresh the Window
	 */
	public void refresh() {
		// Place all the JavaFX Button controls into an array for simple iteration
		Button[] buttons = new Button[] { monthJan, monthFeb, monthMar, monthApr, monthMay, monthJun, monthJul, monthAug, monthSep, monthOct, monthNov, monthDec };

		// configure the currently selected year
		if (this.year.getText().isBlank()) {
			this.year.setText(Integer.toString(DEFAULT_YEAR));
		}
		int selectedYear;
		try {
			selectedYear = Integer.valueOf(this.year.getText());
			lastSelectedYear = selectedYear;
		} catch (NumberFormatException badYearStringEx) {
			selectedYear = lastSelectedYear;
		}

		// get Months data for this year from database, and map data for simple confirmation of existence below
		List<Month> months = Controller.getDAO().pullMonthsForYear(selectedYear);
		monthMap = new HashMap<Integer, Month>(12);
		for (Month m : months) {
			int thisMonth = m.getDate().getMonthValue() - 1;
			monthMap.put(thisMonth, m);
		}

		// set properties of Month Button controls
		for (int i = 0; i < buttons.length; i++) {
			if (monthMap.containsKey(i)) {
				buttons[i].setStyle("-fx-background-color: blue;");
				buttons[i].setDisable(false);
			} else {
				buttons[i].setStyle(null);
				buttons[i].setDisable(true);
			}
			final int tmp = i;
			buttons[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					WindowMonth wm = new WindowMonth(monthMap.get(tmp));
					wm.show();
				}
			});
		}

		// set action handle for yearSelect Button control
		yearSelect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				refresh();
			}
		});

	}

}
