package view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
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
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;
import model.domain.Utility;

public class WindowTransaction {

	public enum Operation {
		ADD("Add Transaction"),
		EDIT("Edit Transaction");

		private String windowTitle;

		private Operation(String windowTitle) {
			this.windowTitle = windowTitle;
		}

		public String toString() {
			return this.windowTitle;
		}
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
	private Month m;
	private Operation operation;

	/**
	 * <pre>
	 * KEY:   Type.toString()
	 * VALUE: Type.name()
	 * </pre>
	 */
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
	public WindowTransaction(Month m, Transaction t) throws IllegalArgumentException, IOException {
		this(m, Utility.nullCheck(t), Operation.EDIT);
	}

	/**
	 * Open a WindowTransaction scene-graph, to create a new Transaction for month.
	 * 
	 * @throws IOException
	 */
	public WindowTransaction(Month m) throws IOException {
		this(m, null, Operation.ADD);
	}

	/**
	 * Consolidated private constructor
	 * 
	 * @param t
	 * @param op
	 * @throws IOException
	 */
	private WindowTransaction(Month m, Transaction t, Operation op) throws IOException {
		this.t = t;
		this.m = m;
		this.operation = op;
		initialize();
		Controller.setWindowTransaction(this);
	}

	//**********************************\
	//									|
	//	Initialisations					|
	//									|
	//**********************************/

	private void initialize() throws IOException {

		/**
		 * Scene-graph components
		 */

		// Generate map for Transaction Types
		typeMap = new HashMap<String, String>();
		for (Type type : Type.values()) {
			typeMap.put(type.toString(), type.name());
		}

		// Set root
		this.loader = new FXMLLoader(getClass().getResource(FXML));
		this.loader.setController(this);
		this.root = loader.load();

		// Set Scene
		this.scene = new Scene(this.root);
		this.scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());

		// Set Stage
		this.stage = new Stage();
		this.stage.getIcons().add(new Image(getClass().getResource(ICON).toExternalForm()));
		this.stage.setTitle(operation.toString());
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.setOnCloseRequest(event -> {

			Transaction collectedTransaction = collectTransaction();

			if (collectedTransaction == null || collectedTransaction.equals(t)) {
				close();
			} else {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setContentText("Are you sure you want to cancel " +
					((operation == Operation.ADD) ? "Adding" : "Editing") +
					" this Transaction?");
				alert.setHeaderText("You are about to abandon this transaction!");
				alert.setTitle("Please confirm cancellation.");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.OK) {
					close();
				} else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
					event.consume();
				}
			}

		});
		this.stage.initModality(Modality.APPLICATION_MODAL);

		/**
		 * Nodes and controls
		 */

		// name
		name.setText((t != null) ? t.getName() : "");

		// date
		date.setValue((t != null) ? t.getDate() : (m != null) ? m.getDate() : LocalDate.now());
		date.setOnAction(event -> {

			boolean accepted = true;

			if (date.getValue().getMonthValue() != m.getDate().getMonthValue()
				|| date.getValue().getYear() != m.getDate().getYear()) {
				accepted = false;
			}

			if (!accepted) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText("You must select a day in " + m.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + " " + m.getDate().getYear());
				alert.setTitle("Bad date selection...");
				LocalDate dpValue = date.getValue();
				alert.setHeaderText("You selected a date in " + dpValue.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.UK) + " " + dpValue.getYear());
				alert.show();
				//dp.setValue(LocalDate.of(t.getDate().getYear(), t.getDate().getMonthValue(), t.getDate().getDayOfMonth()));
				date.setValue(m.getDate());
			}

		});

		// type
		List<String> typeNames = new ArrayList<String>();
		for (Type t : Type.values()) {
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
		buttonCancel.setOnAction(event -> closeAlertUnsaved(event));
		buttonSave.setOnAction(event -> save());
	}

	//**********************************\
	//									|
	//	JavaFX Application Methods		|
	//									|
	//**********************************/

	/**
	 * Close this {@code WindowTransaction}, and prompt user for confirmation if the {@link #collectTransaction() collected Transaction} if not null, and
	 * not equal to {@code t}.
	 * 
	 * @param event
	 */
	private void closeAlertUnsaved(Event event) {

		Transaction collectedTransaction = collectTransaction();

		boolean collectionSuccess;
		boolean transactionChanged;
		if (collectedTransaction == null || (t != null && collectedTransaction.equals(t))) {
			close();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("Are you sure you want to cancel " +
				((operation == Operation.ADD) ? "Adding" : "Editing") +
				" this Transaction?");
			alert.setHeaderText("You are about to abandon this transaction!");
			alert.setTitle("Please confirm cancellation.");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				close();
			} else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
				event.consume();
			}
		}
	}

	/**
	 * Close this {@code WindowTransaction}, and remove the reference to it from Controller with {@link Controller#setWindowTransaction(WindowTransaction)
	 * Controller.setWindowTransaction(null)}.
	 */
	private void close() {
		Controller.setWindowTransaction(null);
		stage.close();
	}

	/**
	 * Save this edited Transaction, overwritting the original Transaction. This is achieved by updating the Transaction in the data
	 * {@code Controller months} field, then passing the month with updated transactions to the {@link WindowMonth#update(Month) WindowMonth
	 * update(Month)} method.
	 */
	private void save() {
		Transaction collectedTransaction = collectTransaction();
		if (collectedTransaction != null && (t != null && collectedTransaction.isUnsaved())) { //update trans
			collectedTransaction.setUnsaved(true);
			m.getTransactions().remove(t);
			m.getTransactions().add(collectedTransaction);
			Controller.getWindowMonth().update(m);
			close();
		} else if (collectedTransaction != null && t == null) { // new trans
			m.getTransactions().add(collectedTransaction);
			Controller.getWindowMonth().update(m);
			close();
		}
	}

	/**
	 * Parse all input controls and attempt to create a Transaction object.
	 * 
	 * @return a Transaction object, or null if object instantiation failed.
	 */
	private Transaction collectTransaction() {
		Transaction tr = null;

		try {
			// capture parameters
			String paramName = name.getText();
			boolean paramPaid = paid.isSelected();
			boolean paramIncome = incomeMap.get(income.getValue());
			Type paramType = Type.valueOf(typeMap.get(type.getValue()));
			double paramValue = Math.abs(Double.valueOf(value.getText()));
			LocalDate paramDate = date.getValue();
			if (t != null) {
				// update transaction
				tr = new Transaction(paramName,
						paramPaid,
						paramDate,
						paramIncome,
						paramType,
						paramValue,
						t.getTransactionID());
				tr.setUnsaved(true);
			} else {
				// create transaction
				tr = new Transaction(paramName,
						paramPaid,
						paramDate,
						paramIncome,
						paramType,
						paramValue);
			}
			
		} catch (IllegalArgumentException instantiationFailEx) {
		}
		//int trHash = tr.hashCode();
		//int tHash = (t != null) ? t.hashCode() : Transaction.NEW_ID;
		//		if (tr != null && trHash != tHash) {
		//			t.setName(paramName);
		//			t.setIncome(paramIncome);
		//			t.setPaid(paramPaid);
		//			t.setValue(paramValue);
		//			t.setDate(paramDate);
		//			return t;
		//		} 
		return tr;

	}

	/**
	 * Show the stage of this WindowTransaction object.
	 */
	public void show() {
		this.stage.show();
	}

}
