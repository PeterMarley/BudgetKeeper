package view;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
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

	/**
	 * This HashMap is used to associate month values (0-11) with Month objects, so the GUI can select, for example, December, in a year, attempting
	 * to get from the HashMap using the month value 11.<br>
	 * <hr>
	 * Key: month of year (0 to 11).<br>
	 * Value: Month object.
	 */
	private HashMap<Integer, Month> mapOfSingleYear;
	private int lastSelectedYear = DEFAULT_YEAR;
	//private ObservableList<Integer> years;
	private Integer yearToAdd;

	//**********************************\
	//									|
	//	Constants						|
	//									|
	//**********************************/

	private static final String FXML = "./fxml/WindowYear.fxml";
	private static final String CSS = "./css/WindowYear.css";
	private static final int DEFAULT_YEAR = LocalDate.now().getYear();
	private static final String ENABLED_MONTH_BUTTON_CSS = "-fx-background-color: #3446eb; -fx-opacity: 1.0; -fx-text-fill:white; -fx-font-weight:bold;";
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
	@FXML private Button newYearButton;
	@FXML private ComboBox<Integer> yearComboBox;

	// import/ export buttons
	@FXML private Button importButton;
	@FXML private Button exportButton;


	// operations nodes
	//@FXML private Button addMonth;

	//**********************************\
	//									|
	//	Construction/ Start				|
	//									|
	//**********************************/

	public WindowYear() throws IOException {
		//start(new Stage());
	}

	public WindowYear(Boolean notInitLaunch) throws IOException {
		start(null);
	}

	/**
	 * Initialise and configure this {@code WindowYear} object, then show.
	 */
	@Override
	public void start(Stage generatedStage) throws IOException {
		stage = generatedStage != null ? generatedStage : new Stage();
		// configure root
		this.loader = new FXMLLoader(getClass().getResource(FXML));
		this.loader.setController(this);
		this.root = loader.load();

		// configure scene
		this.scene = new Scene(root);
		this.scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());
		this.yearComboBox.requestFocus();

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
				System.exit(0);
			}
		});
	}

	/**
	 * Initialise JavaFX Nodes.
	 */
	@FXML
	private void initialize() {

		// set lastSelectedYear
		lastSelectedYear = DEFAULT_YEAR;
		//years = buildData();
		// refresh data
		refresh();

		// pass this to Controller
		Controller.setWindowYear(this);


		// show stage
		stage.show();
	}

	/**
	 * Fills mapOfSingle year with data from controller, and returns the list of years as an
	 * {@link FXCollections#observableArrayList(java.util.Collection) ObservableList}.
	 * 
	 * @return an ObservableList of Integers, representing each year found in the data
	 */
	private ObservableList<Integer> buildData() {
		// get data from Controller
		List<Month> obsData = Controller.getData();

		// build mapOfSingleYear and list of years
		mapOfSingleYear = new HashMap<Integer, Month>();
		Set<Integer> years = new HashSet<Integer>();
		for (Month month : obsData) {
			int monthVal = month.getDate().getMonthValue() - 1;
			if (month.getDate().getYear() == lastSelectedYear && !mapOfSingleYear.containsKey(monthVal)) {
				mapOfSingleYear.put(monthVal, month);
			}
			years.add(month.getDate().getYear());
		}

		// Get all years that have Month objects stored in database
		ObservableList<Integer> yearsList = FXCollections.observableArrayList(years);
		Collections.sort(yearsList);

		// set ComboBox drop down menu to hold values of all years from database.
		return yearsList;
	}

	private void configExportImport() {
		exportButton.setOnAction(event -> {
			String contentMsg;
			boolean success;
			try {
				String exportedFilename = Controller.exportData();
				success = true;
				contentMsg = "You can find your file @ \n" + exportedFilename;
			} catch (IllegalArgumentException exportFailedEx) {
				success = false;
				contentMsg = "You cannot export if there is no data!";
			}
			Alert exportConfirmed = new Alert(AlertType.INFORMATION);
			exportConfirmed.setHeaderText("Your Budget data was " + ((!success) ? "not" : "") + " exported.");
			exportConfirmed.setContentText(contentMsg);
			exportConfirmed.show();
		});
		importButton.setOnAction(event -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setHeaderText("Confirm deletion of current data?");
			alert.setContentText("Would you like to replace the current data with imported data, or keep both?");
			ButtonType buttonReplace = new ButtonType("Replace");
			ButtonType buttonKeep = new ButtonType("Keep");
			ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().clear();
			alert.getButtonTypes().addAll(buttonKeep, buttonReplace, buttonCancel);
			Optional<ButtonType> choice = alert.showAndWait();
			if (choice.isPresent()) {
				Boolean keep = null;
				if (choice.get() == buttonKeep)
					keep = true;
				if (choice.get() == buttonReplace)
					keep = false;
				if (keep != null) {
					FileChooser fx = new FileChooser();
					fx.setInitialDirectory(new File("./"));
					fx.getExtensionFilters().add(new ExtensionFilter("Budget Keeper Export", "*.bke"));
					File selectedFile = fx.showOpenDialog(this.stage);
					Controller.importData(selectedFile, keep);
					refresh();
				}
			}
		});
	}

	/**
	 * Configure the 12 buttons that open the {@link view.WindowMonth WindowMonth} for a particular {@link model.domain.Month Month}.
	 */
	private void configMonthButtons() {

		// get Months data for this year from Controller
		Button[] buttons = new Button[] { monthJan, monthFeb, monthMar, monthApr, monthMay, monthJun, monthJul, monthAug, monthSep, monthOct,
				monthNov, monthDec
		};
		for (int month = 0; month < buttons.length; month++) {
			String styleToApply = null;
			// set button style and enable/ disable depending on whether monthMap had that specific month
			boolean monthExists;
			if (mapOfSingleYear.containsKey(month)) {
				styleToApply = ENABLED_MONTH_BUTTON_CSS;
				monthExists = true;
			} else {
				styleToApply = DISABLED_MONTH_BUTTON_CSS;
				monthExists = false;
			}
			buttons[month].setStyle(styleToApply);
			//buttons[month].setDisable(monthExists);

			/**
			 * set action handler for button
			 * 
			 * When Button for a particular Month is fired, a Runnable created as an anonymouse inner class and run on a new Thread, which displays
			 * the WindowMonth JavaFX scene-graph/
			 */
			final int monthIndex = month;
			if (monthExists) {
				buttons[month].setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							WindowMonth wm = new WindowMonth(mapOfSingleYear.get(monthIndex));
							hide();
							wm.show();
						} catch (IllegalArgumentException | IOException wmInstantiationFailureEx) {
							wmInstantiationFailureEx.printStackTrace();
						}
					}
				});
			} else {
				buttons[month].setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							Month m = new Month(
									LocalDate.parse(String.format("%4d/%02d", lastSelectedYear, monthIndex + 1), Constants.FORMAT_YYYYMM));
							mapOfSingleYear.put(monthIndex, m);
							WindowMonth wm = new WindowMonth(m, true);
							hide();
							wm.show();
						} catch (IllegalArgumentException | IOException wmInstantiationFailureEx) {
							wmInstantiationFailureEx.printStackTrace();
						}
					}
				});
			}
		}
	}

	/**
	 * Configure the {@link #selectYearButton year selection button} and the {@link #yearComboBox year choice combo box}.
	 */
	private void configYearSelection() {

		// set event handler for yearSelect Button control
		//selectYearButton.setOnAction((actionEvent) -> changeYearAction());

		// set event handler for selecting a new year in the yearComboBox control
		yearComboBox.setOnAction((actionEvent) -> changeYearAction());

		// set the yearComboBox to fire selectYearButton if Enter key is pressed when focus is on ComboBox.
		yearComboBox.setOnKeyPressed((keyEvent) -> changeYearAction());

		newYearButton.setOnAction((actionEvent) -> newYearAction(false));
	}

	private void newYearAction(boolean retry) {
		TextInputDialog requestYearDialog = new TextInputDialog();
		String header;
		if (retry) {
			header = "Please try again";
		} else {
			header = "A new year yeaaaassss?";

		}
		requestYearDialog.setContentText("New Year:");
		requestYearDialog.setHeaderText(header);
		requestYearDialog.setGraphic(null);
		Optional<String> result = requestYearDialog.showAndWait();
		if (result.isPresent() && !result.get().isBlank()) {
			try {
				int y = Integer.valueOf(result.get());
				ObservableList<Integer> updatedYears = yearComboBox.getItems();
				updatedYears.add(y);
				Collections.sort(updatedYears);
				lastSelectedYear = y;
				yearToAdd = y;
				refresh();
			} catch (NumberFormatException e) {
				newYearAction(true);
			}

		}
	}

	/**
	 * This is invoked when an action is intended to change the selected year in the WindowYear View
	 */
	private void changeYearAction() {
		lastSelectedYear = yearComboBox.getValue() != null ? yearComboBox.getValue() : LocalDate.now().getYear();
		stage.setTitle("Months for " + lastSelectedYear);
		buildData();
		configMonthButtons();
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
		// set new title for stage depending on the year selected

		// set the currently selected year of the ComboBox control to the lastSelectedYear field
		yearComboBox.setValue(lastSelectedYear);

		ObservableList<Integer> years = buildData();
		if (yearToAdd != null) {
			years.add(yearToAdd);
			Collections.sort(years);
			yearToAdd = null;
		}
		yearComboBox.setItems(years);

		configExportImport();
		configMonthButtons();
		configYearSelection();
	}

	/**
	 * Show the WindowYear Stage.
	 */
	public void show() {
		refresh();
		this.stage.show();
	}

	/**
	 * Hide the WindowYear Stage.
	 */
	public void hide() {
		this.stage.hide();
	}

	public void addNewMonth(Month m) {
		mapOfSingleYear.put(m.getDate().getMonthValue() - 1, m);
	}

	public void removeMonth(Month m) {
		mapOfSingleYear.remove(m.getDate().getMonthValue() - 1);

	}

}
