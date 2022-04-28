package view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.domain.Transaction;
import model.domain.Transaction.Type;
import model.domain.Utility;

public class WindowTransaction {

	public enum Operation {
		ADD,
		EDIT;
	}

	//**********************************\
	//									|
	//	JavaFX Controls					|
	//									|
	//**********************************/

	@FXML private TextField name;
	@FXML private DatePicker date;
	@FXML private ComboBox<String> type;
	@FXML private TextField value;
	@FXML private ComboBox<String> income;
	@FXML private CheckBox paid;
	@FXML private Button buttonSave;
	@FXML private Button buttonCancel;

	//**********************************\
	//									|
	//	Instance Fields					|
	//									|
	//**********************************/

	private FXMLLoader loader;
	private Parent root;
	private Scene scene;
	private Stage stage;

	private Transaction t;
	private Operation operation;

	private HashMap<String, String> typeMap;
	private HashMap<String, Boolean> incomeMap;

	private EventHandler<Event> closeHandler;

	//**********************************\
	//									|
	//	Constants						|
	//									|
	//**********************************/

	private static final String FXML = "./fxml/WindowTransaction.fxml";
	private static final String CSS = "./css/WindowTransaction.css";
	private static final String ICON = "./img/icons/calendar.png";

	//**********************************\
	//									|
	//	Construction					|
	//									|
	//**********************************/

	/**
	 * Open a WindowTransaction scene-graph to edit an existing Transaction for a month.
	 * 
	 * @param t
	 * @throws IllegalArgumentException if t is null
	 * @throws IOException
	 */
	public WindowTransaction(Transaction t) throws IllegalArgumentException, IOException {
		this(Utility.nullCheck(t), Operation.EDIT);
	}

	/**
	 * Open a WindowTransaction scene-graph, to create a new Transaction for month.
	 * 
	 * @throws IOException
	 */
	public WindowTransaction() throws IOException {
		this(null, Operation.ADD);
	}

	/**
	 * Consolidated private constructor
	 * 
	 * @param t
	 * @param op
	 * @throws IOException
	 */
	private WindowTransaction(Transaction t, Operation op) throws IOException {
		this.t = t;
		this.operation = op;

		typeMap = new HashMap<String, String>();
		for (Type type : Type.values()) {
			typeMap.put(type.toString(), type.name());
		}

		setRoot();
		setScene();
		setStage();

		initialize();

		Controller.setWindowTransaction(this);
	}

	private void setRoot() throws IOException {
		this.loader = new FXMLLoader(getClass().getResource(FXML));
		this.loader.setController(this);
		this.root = loader.load();
	}

	private void setScene() {
		this.scene = new Scene(this.root);
		this.scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());

	}

	private void setStage() {
		this.stage = new Stage();
		this.stage.getIcons().add(new Image(getClass().getResource(ICON).toExternalForm()));
		this.stage.setTitle((operation == Operation.ADD) ? "Add Transaction" : "Edit Transaction");
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.setOnCloseRequest(event -> close(event));
		this.stage.initModality(Modality.APPLICATION_MODAL);

	}

	//**********************************\
	//									|
	//	Initialisations					|
	//									|
	//**********************************/

	private void close(Event event) {
		boolean toClose = false;
		//new Transaction(name, isPaid, date, isIncome, type, value)
		Transaction validate = null;
		try {
			validate = new Transaction(
					name.getText(),
					paid.isSelected(),
					date.getValue(),
					incomeMap.get(income.getValue()),
					Type.valueOf(typeMap.get(type.getValue())),
					Double.valueOf(value.getText()));

		} catch (IllegalArgumentException e) {
			toClose = true;
		}

		if (!toClose && validate != null && !validate.equals(t)) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("Are you sure you want to cancel " +
					((operation == Operation.ADD) ? "Adding" : "Editing") +
					" this Transaction?");
			alert.setHeaderText("You are about to abandon this transaction!");
			alert.setTitle("Please confirm cancellation.");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				toClose = true;
			} else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
				event.consume();
			}
		} else {
			toClose = true;
		}

		if (toClose) {
			stage.close();
			Controller.setWindowTransaction(null);
			Controller.getWindowMonth().show();
		}
	}
	
	private void initialize() {
		
		

		// name
		name.setText((t != null) ? t.getName() : "");

		// date
		date.setValue((t != null) ? t.getDate() : LocalDate.now());
		date.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (event.getSource() instanceof DatePicker) {
					DatePicker dp = (DatePicker) event.getSource();
					if (dp.getValue().getMonthValue() != t.getDate().getMonthValue()
							|| dp.getValue().getYear() != t.getDate().getYear()) {
						dp.setValue(LocalDate.of(t.getDate().getYear(), t.getDate().getMonthValue(), t.getDate().getDayOfMonth()));
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setContentText("You must select a day in " + t.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + " " + t.getDate().getYear());
						alert.show();
					}
				}
			}
		});

		// type
		Type[] types = Type.values();
		List<String> typeNames = new ArrayList<String>(types.length);
		for (Type t : types) {
			typeNames.add(t.toString());
		}
		SortedSet<String> keys = new TreeSet<String>(typeMap.keySet());
		type.setItems(FXCollections.observableList(typeNames));
		type.setValue((t != null) ? t.getType().toString() : typeNames.get(0));

		// income
		List<String> incomeNames = new ArrayList<String>(2);
		incomeNames.add("Income");
		incomeNames.add("Outgoing");
		incomeMap = new HashMap<String, Boolean>(2);
		incomeMap.put("Income", true);
		incomeMap.put("Outgoing", false);
		income.setItems(FXCollections.observableList(incomeNames));
		income.setValue((t != null) ? ((t.isIncome()) ? "Income" : "Outgoing") : "Income");

		// value
		value.setText((t != null) ? String.format("%.02f", t.getAbsoluteValue()) : "");

		// paid
		paid.setSelected((t != null) ? t.isPaid() : false);
		
		// close handlers
		buttonCancel.setOnAction(request -> close(request));
		buttonSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("button save not implemented yet");
			}
		});
	}

	//**********************************\
	//									|
	//	JavaFX Application Methods		|
	//									|
	//**********************************/

	public void show() {
		this.stage.show();
	}

}
