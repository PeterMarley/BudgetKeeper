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

	// x12 Month buttons
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

	// year selection nodes
	@FXML private Button selectYearButton;
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
	public void start(Stage generatedStage) throws IOException {
		this.stage = generatedStage;
		try {
			setRoot();
			setScene();
			setStage();
			lastSelectedYear = DEFAULT_YEAR;
			refresh(); // default to showing the current year
			
			Controller.setWindowYear(this);
			// show this window
			this.stage.show();
		} catch (IOException e) {
			System.err.println("FXMLLoader.load IOException:");
			e.printStackTrace();
		}

	}

	/**
	 * Set the {@code FXMLLoader}, and load the FXML. Additionally, set the controller for this scene-graph.
	 * 
	 * @throws IOException if an error occurs during FXML loading during {@link FXMLLoader#load() loading}.
	 */
	private void setRoot() throws IOException {
		this.loader = new FXMLLoader(getClass().getResource(FXML));
		this.loader.setController(this);
		this.root = loader.load();
	}

	/**
	 * Set the {@code Scene} of this scene-graph, and apply CSS style sheets to the scene.
	 */
	private void setScene() {
		this.scene = new Scene(root);
		this.scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());
	}

	/**
	 * Set the {@code Stage} of this scene-graph. Title, icon and various configurations are applied here.
	 */
	private void setStage() {
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.setTitle("Months for " + lastSelectedYear);
		this.stage.getIcons().add(new Image(getClass().getResource(ICON).toExternalForm()));
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				stage.close();
				Platform.exit();
				System.exit(0);
			}
		});
	}

	//**********************************\
	//									|
	//	JavaFX Application Methods		|
	//									|
	//**********************************/

	/**
	 * Refresh the Nodes in this scene-graph
	 */
	public void refresh() {

		// Get all years that have Month objects stored in database
		ObservableList<Integer> yearsList = FXCollections.observableArrayList(Controller.getDatabaseAccessObject().getYears());

		// set ComboBox drop down menu to hold values of all years from database.
		yearComboBox.setItems(yearsList);

		// set the currently selected year of the ComboBox control to the lastSelectedYear field
		yearComboBox.setValue(lastSelectedYear);

		// set new title for stage depending on the year selected
		this.stage.setTitle("Months for " + lastSelectedYear);
		
		configMonthButtons();
		configYearSelection();
	}

	/**
	 * Show the WindowYear Stage.
	 */
	public void show() {
		this.stage.show();
	}

	/**
	 * Hide the WindowYear Stage.
	 */
	public void hide() {
		this.stage.hide();
	}

	/**
	 * Configure the 12 buttons that open the {@link view.WindowMonth WindowMonth} for a particular {@link model.domain.Month Month}.
	 */
	private void configMonthButtons() {
		// get Months data for this year from database, and map data for simple confirmation of existence below
		List<Month> months = Controller.getDatabaseAccessObject().pullMonthsForYear(lastSelectedYear);
		monthMap = new HashMap<Integer, Month>(12);
		for (Month m : months) {
			int thisMonth = m.getDate().getMonthValue() - 1;
			monthMap.put(thisMonth, m);
		}

		Button[] buttons = new Button[] { monthJan, monthFeb, monthMar, monthApr, monthMay, monthJun, monthJul, monthAug, monthSep, monthOct, monthNov, monthDec };
		for (int i = 0; i < buttons.length; i++) {
			String styleToApply = "";
			// set button style and enable/ disable depending on whether monthMap had that specific month
			boolean disabled;
			if (monthMap.containsKey(i)) {
				styleToApply = ENABLED_MONTH_BUTTON_CSS;
				disabled = false;
			} else {
				styleToApply = DISABLED_MONTH_BUTTON_CSS;
				disabled = true;
			}
			buttons[i].setStyle(styleToApply);
			buttons[i].setDisable(disabled);

			/**
			 * set action handler for button
			 * 
			 * When Button for a particular Month is fired, a Runnable created as an anonymouse inner class and run on a new Thread,
			 * which displays the WindowMonth JavaFX scene-graph/
			 */
			final int index = i;
			buttons[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					Month monthToShow = monthMap.get(index);
					try {
						WindowMonth wm = new WindowMonth(monthToShow);
						hide();
						wm.show();
					} catch (IllegalArgumentException | IOException wmInstantiationFailureEx) {
						wmInstantiationFailureEx.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * Configure the {@link #selectYearButton year selection button} and the {@link #yearComboBox year choice combo box}.
	 */
	private void configYearSelection() {
		// set action handle for yearSelect Button control
		selectYearButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				lastSelectedYear = yearComboBox.getValue();
				refresh();
			}
		});

		// set the yearComboBox to fire selectYearButton if Enter key is pressed when focus is on ComboBox.
		yearComboBox.setOnKeyPressed(key -> {
			if (key.getCode() == KeyCode.ENTER)
				selectYearButton.fire();
		});
	}
}
