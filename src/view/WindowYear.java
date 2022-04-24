package view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.db.Constants;
import model.domain.Month;

/**
 * Icons:<br>
 * <a href="https://www.flaticon.com/free-icons/calendar" title="calendar icons">Calendar icons created by Freepik - Flaticon</a><br>
 * <a href="https://www.flaticon.com/free-icons/accounting" title="accounting icons">Accounting icons created by kerismaker - Flaticon</a>
 * 
 * @author Peter Marley
 * @StudentNumber 13404067
 * @Email pmarley03@qub.ac.uk
 * @GitHub https://github.com/PeterMarley
 *
 */
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
	private static final String CSS = "./css/WindowYear.css";
	private static final int DEFAULT_YEAR = LocalDate.now().getYear();
	private static final String ENABLED_MONTH_BUTTON_CSS = "-fx-background-color: #3446eb;" +
			"-fx-opacity: 1.0;" +
			"-fx-text-fill:white;" +
			"-fx-font-weight:bold;";
	private static final String DISABLED_MONTH_BUTTON_CSS = "";//"-fx-background-color: white;-fx-opacity: 0.5;";
	private static final String ICON = "./img/icons/calendar.png";

	//**********************************\
	//									|
	//	JavaFX Controls					|
	//									|
	//**********************************/

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

	@FXML private ComboBox<Integer> yearComboBox;

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
		this.stage = generatedStage;
		try {
			// load FXML
			this.loader = new FXMLLoader(getClass().getResource(FXML));
			this.loader.setController(this);
			this.root = loader.load();

			// configure scene
			this.scene = new Scene(root);
			this.scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());

			// configure stage
			this.stage.setScene(scene);
			this.stage.setResizable(false);
			this.stage.setTitle("Months for " + lastSelectedYear);
			this.stage.getIcons().add(new Image(getClass().getResource(ICON).toExternalForm()));
			this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					stage.close();
					Platform.exit();
				}
			});

		} catch (IOException e) {
			System.err.println("FXMLLoader.load IOException:");
			e.printStackTrace();
		}
		lastSelectedYear = DEFAULT_YEAR;

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

		ObservableList<Integer> yearsList = FXCollections.observableArrayList(Controller.getDAO().getYears());
		yearComboBox.setItems(yearsList);
		yearComboBox.setValue(lastSelectedYear);
		this.stage.setTitle("Months for " + lastSelectedYear);
		// get Months data for this year from database, and map data for simple confirmation of existence below
		List<Month> months = Controller.getDAO().pullMonthsForYear(lastSelectedYear);
		monthMap = new HashMap<Integer, Month>(12);
		for (Month m : months) {
			int thisMonth = m.getDate().getMonthValue() - 1;
			monthMap.put(thisMonth, m);
		}

		// set properties of Month Button controls
		for (int i = 0; i < buttons.length; i++) {
			String styleToApply = "";
			if (monthMap.containsKey(i)) {
				//buttons[i].setStyle(ENABLED_MONTH_BUTTON_CSS);
				styleToApply = ENABLED_MONTH_BUTTON_CSS;
				buttons[i].setDisable(false);
			} else {
				//buttons[i].setStyle(DISABLED_MONTH_BUTTON_CSS);
				styleToApply = DISABLED_MONTH_BUTTON_CSS;
				buttons[i].setDisable(true);
			}
			buttons[i].setStyle(styleToApply);
			final int index = i;
			buttons[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					Month monthToShow = monthMap.get(index);
					Runnable windowMonth = new Runnable() {
						@Override
						public void run() {
							try {
								WindowMonth wm = new WindowMonth(monthToShow);
								wm.show();
							} catch (IOException e1) {
								System.err.println("Attempt to instantiate and show a WindowMonth object for month failed!");
								System.err.println(monthToShow.toString());
								e1.printStackTrace();
							}
						}
					};
					windowMonth.run();
				}
			});
		}

		// set action handle for yearSelect Button control
		yearSelect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				lastSelectedYear = yearComboBox.getValue();
				refresh();
			}
		});

	}

}
