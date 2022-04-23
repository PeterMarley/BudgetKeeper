package view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;
import model.domain.Utility;
import model.domain.comparators.TransactionComparatorDate;
import model.domain.comparators.TransactionComparatorIncome;
import model.domain.comparators.TransactionComparatorType;

public class WindowMonth {

	//**********************************\
	//									|
	//	Scene Graph objects				|
	//									|
	//**********************************/

	@FXML private TableView<Transaction> transactions;
	@FXML private TableColumn<Transaction, LocalDate> transactionsDate;
	@FXML private TableColumn<Transaction, String> transactionsName;
	@FXML private TableColumn<Transaction, Double> transactionsValue;
	@FXML private TableColumn<Transaction, Boolean> transactionsPaid;
	@FXML private TableColumn<Transaction, Type> transactionsType;

	@FXML private TextField totalIn;
	@FXML private TextField totalOut;
	@FXML private TextField totalBalance;

	@FXML private CheckBox filterCash;
	@FXML private CheckBox filterDirectDebit;
	@FXML private CheckBox filterStandingOrder;
	@FXML private CheckBox filterBankTransfer;

	//**********************************\
	//									|
	//	Instance Fields					|
	//									|
	//**********************************/

	private FXMLLoader loader;
	private Parent root;
	private Stage stage;
	private Scene scene;

	private Month selectedMonth;
	HashMap<String, Boolean> transactionFilters;

	//	private boolean showTransactionsCash;
	//	private boolean showTransactionsDirectDebit;
	//	private boolean showTransactionsStandingOrder;
	//	private boolean showTransactionsBankTransfer;

	//**********************************\
	//									|
	//	Constants						|
	//									|
	//**********************************/

	private static final String FXML = "./fxml/WindowMonth.fxml";
	private static final String CSS = "./css/WindowMonth.css";
	private static final String ICON = "./img/icons/document.png";

	//**********************************\
	//									|
	//	Construction					|
	//									|
	//**********************************/

	/**
	 * Create a WindowMonth JavaFX scene-graph. This scene-graph is used to render the {@link model.domain.Transaction Transaction}s held in a specific
	 * {@link model.domain.Month Month} object.<br>
	 * <br>
	 * <b>FXML</b><br>
	 * The FXML file containing mark-up located at {@value #FXML}.<br>
	 * <br>
	 * <b>CSS</b><br>
	 * The CSS file containing the cascading style sheet for this JavaFX scene-graph is located at {@value #CSS}.
	 * 
	 * @param month
	 * @throws IOException              if FXMLLoader fails to load the FXML file.
	 * @throws IllegalArgumentException if month is null
	 */
	public WindowMonth(Month month) throws IOException, IllegalArgumentException {

		this.selectedMonth = Utility.nullCheck(month);

		// load FXML
		this.loader = new FXMLLoader(getClass().getResource(FXML));
		this.loader.setController(this);
		this.root = loader.load();

		// configure Scene
		this.scene = new Scene(root);
		this.scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());

		// configure Stage
		this.stage = new Stage();
		this.stage.getIcons().add(new Image(getClass().getResource(ICON).toExternalForm()));
		this.stage.setTitle(month.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + " " + month.getDate().getYear());
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				stage.close();
			}
		});

		// set filters
		this.transactionFilters = new HashMap<String, Boolean>();
		for (Type type : Type.values()) {
			transactionFilters.put(type.name(), true);
		}
		this.filterCash.setSelected(true);
		this.filterDirectDebit.setSelected(true);
		this.filterStandingOrder.setSelected(true);
		this.filterBankTransfer.setSelected(true);
	}

	//**********************************\
	//									|
	//	JavaFX Application Methods		|
	//									|
	//**********************************/

	/**
	 * Refresh the data in the scene-graph and show stage.
	 */
	public void show() {
		refresh(selectedMonth.getTransactions()); // default to showing the current year
		this.stage.show();
	}

	/**
	 * Hide this scene-graph.
	 */
	public void hide() {
		this.stage.hide();
	}

	/**
	 * Refresh all the nodes in this scene-graph.
	 * 
	 * @param transactions
	 */
	private void refresh(Collection<Transaction> transactions) {
		fillTable(transactions);
		fillTotals(transactions);
	}

	/**
	 * Fill totals TextFields with Transaction data summary for this Month/
	 * 
	 * @param transactions
	 */
	private void fillTotals(Collection<Transaction> transactions) {
		// declare calculation variables
		double in = 0, out = 0, balance = 0;

		// total the income and outgoings for all Transactions in this Month
		for (Transaction t : transactions) {
			if (t.isIncome()) {
				in += t.getAbsoluteValue();
			} else {
				out += t.getAbsoluteValue();
			}
		}
		balance = in - out;

		// set the relevant buttons to show the calculations
		this.totalIn.setText(String.format("%.2f", in));
		this.totalOut.setText(String.format("%.2f", out));
		this.totalBalance.setText(String.format("%s%.2f", (balance > 0) ? "+" : "", balance));
		if (balance >= 0) {
			this.totalBalance.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
		} else {
			this.totalBalance.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
		}

		TextField[] totals = new TextField[] { this.totalIn, this.totalOut, this.totalBalance };

		for (TextField t : totals) {
			t.setDisable(true);
			t.setStyle(t.getStyle() + "-fx-opacity: 1.0;");
		}

	}

	/**
	 * Fill TableView with Transactions for selected Month
	 * 
	 * @param transactions
	 */
	private void fillTable(Collection<Transaction> transactions) {
		// Create an ArrayList of Transactions that we can use from the interface type Collection
		List<Transaction> sortedTransactions = filterTransactions(transactions);

		// sort with two comparators to get a "nested" sort, first by date, then by type.
		Collections.sort(sortedTransactions, new TransactionComparatorDate());
		Collections.sort(sortedTransactions, new TransactionComparatorType());
		Collections.sort(sortedTransactions, new TransactionComparatorIncome());

		/*
		 *  set the cell value factories of the model, in this case Transaction. The model has its instance fields as 
		 *  type SimpleStringProperty instread of String, SimpleBooleanProperty instead of boolean, etc. 
		 *  The arguments passed into the PropertyValueFactory constructor allows the object to 
		 *  reflectively collect the instance field data and apply it to the table column
		 */

		transactionsDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
		transactionsName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		transactionsValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
		transactionsPaid.setCellValueFactory(new PropertyValueFactory<>("Paid"));
		transactionsType.setCellValueFactory(new PropertyValueFactory<>("Type"));

		this.transactions.setRowFactory(row -> new TableRow<Transaction>() {
			@Override
			public void updateItem(Transaction t, boolean empty) {
				if (t == null || empty) {
					setStyle("");
				} else {
					setStyle(t.getType().getCssStyle());
				}
			}
		});
		/**
		 * Callback signature:
		 * Callback c = new Callback<P, R>() {
		 * };
		 * Callback is a functional interface, and its single abstract method is .call() that a parameter of type P, and return an object of type R.
		 * 
		 * P is the type being passed in, and R is the type to return.
		 * In this case we pass in a TableColumn, and use it to instantiate a TableRow and return that.
		 */
		this.transactionsValue.setCellFactory(new Callback<TableColumn<Transaction, Double>, TableCell<Transaction, Double>>() {
			@Override
			public TableCell<Transaction, Double> call(TableColumn<Transaction, Double> param) {
				return new TableCell<Transaction, Double>() {
					@Override
					public void updateItem(Double t, boolean empty) {
						super.updateItem(t, empty);

						if (!empty && t < 0) {
							this.setStyle("-fx-text-fill: red;");
							this.setText(String.format("%.2f", t));
						} else if (!empty && t >= 0) {
							this.setStyle("-fx-text-fill: green;");
							this.setText(String.format("%.2f", t));
						}
					}
				};
			}
		});
		this.transactionsType.setCellFactory(new Callback<TableColumn<Transaction, Type>, TableCell<Transaction, Type>>() {

			@Override
			public TableCell<Transaction, Type> call(TableColumn<Transaction, Type> param) {
				// TODO Auto-generated method stub
				return new TableCell<Transaction, Type>() {
					@Override
					public void updateItem(Type t, boolean empty) {
						super.updateItem(t, empty);
						if (!empty && t != null) {
							this.setText(t.toString());
						}
					}
				};
			}

		});
		this.transactionsType.setPrefWidth(150.0);

		TableViewSelectionModel<Transaction> selectionModel = this.transactions.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		selectionModel.setCellSelectionEnabled(false);
		this.transactions.setSelectionModel(selectionModel);

		this.transactions.setItems(FXCollections.observableArrayList(sortedTransactions));

		CheckBox[] filters = new CheckBox[] { filterCash, filterDirectDebit, filterStandingOrder, filterBankTransfer };

		// assign action handlers to each of the filtering CheckBox JavaFX controls
		for (CheckBox checkBox : filters) {
			checkBox.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// capture checkbox
					CheckBox c = (CheckBox) event.getSource();
					String s = c.getId().substring("filter".length()).toUpperCase();
					transactionFilters.put(s, c.isSelected());
					refresh(transactions);
				}
			});
		}
	}

	/**
	 * Filter transactions
	 * 
	 * @param transactions
	 * @return
	 */
	private List<Transaction> filterTransactions(Collection<Transaction> transactions) {
		List<Transaction> filtered = new LinkedList<Transaction>();
		for (Transaction t : transactions) {
			if (transactionFilters.get(t.getType().name())) {
				filtered.add(t);
			}
		}
		return filtered;

	}
}