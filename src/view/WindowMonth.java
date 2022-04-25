package view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	//	Scene Graph Nodes				|
	//									|
	//**********************************/

	@FXML
	private TableView<Transaction> transactionsTable;
	@FXML
	private TableColumn<Transaction, LocalDate> transactionsDate;
	@FXML
	private TableColumn<Transaction, String> transactionsName;
	@FXML
	private TableColumn<Transaction, Double> transactionsValue;
	@FXML
	private TableColumn<Transaction, Boolean> transactionsPaid;
	@FXML
	private TableColumn<Transaction, Type> transactionsType;

	@FXML
	private TextField totalIn;
	@FXML
	private TextField totalOut;
	@FXML
	private TextField totalBalance;

	@FXML
	private CheckBox filterCash;
	@FXML
	private CheckBox filterDirectDebit;
	@FXML
	private CheckBox filterStandingOrder;
	@FXML
	private CheckBox filterBankTransfer;
	@FXML
	private CheckBox filterIncome;
	@FXML
	private CheckBox filterOutgoing;

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
	HashMap<String, Boolean> transactionTypeFilters;
	HashMap<String, Boolean> transactionInOutFilters;
	private ObservableList<Transaction> transactionsObsList;
	private List<Transaction> transactionsFilteredOut;

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
		setSelectedMonth(month);
		setRoot();
		setScene();
		setStage();
		setFilterCheckBoxes();
		configJavaFXControls();
	}

	/**
	 * Set the {@code selectedMonth} field.
	 * 
	 * @param month
	 * @throws IllegalArgumentException if {@code month} parameter is null.
	 */
	private void setSelectedMonth(Month month) throws IllegalArgumentException {
		this.selectedMonth = Utility.nullCheck(month);
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
		this.scene = new Scene(this.root);
		this.scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());
	}

	/**
	 * Set the {@code Stage} of this scene-graph. Title, icon and various configurations are applied here.
	 */
	private void setStage() {
		this.stage = new Stage();
		this.stage.getIcons().add(new Image(getClass().getResource(ICON).toExternalForm()));
		this.stage.setTitle(selectedMonth.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + " " + selectedMonth.getDate().getYear());
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				stage.close();
			}
		});
	}

	/**
	 * Generate the {@code HashMap} that contains key value pairs for the filter {@code CheckBox} nodes. The key is the {@code name} of the
	 * {@link Transaction.Type Type} enum in the {@link model.domain.Transaction Transaction} class, and the value is a {@code Boolean}, representing
	 * "should this
	 * transaction type be displayed in the {@code transactionsTable} Node?"
	 */
	private void setFilterCheckBoxes() {
		// Transaction type filters
		this.transactionTypeFilters = new HashMap<String, Boolean>();
		for (Type type : Type.values()) {
			transactionTypeFilters.put(type.name(), true);
		}
		this.filterCash.setSelected(true);
		this.filterDirectDebit.setSelected(true);
		this.filterStandingOrder.setSelected(true);
		this.filterBankTransfer.setSelected(true);

		// Transaction in/out filters
		this.transactionInOutFilters = new HashMap<String, Boolean>();
		this.transactionInOutFilters.put("Income", true);
		this.transactionInOutFilters.put("Outgoing", true);

		this.filterIncome.setSelected(true);
		this.filterOutgoing.setSelected(true);
	}

	/**
	 * Configure the TableView control.
	 */
	private void configJavaFXControls() {
		// set the CellValueFactory attributes of the TableColumns in the TableView
		this.transactionsDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
		this.transactionsName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		this.transactionsValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
		this.transactionsPaid.setCellValueFactory(new PropertyValueFactory<>("Paid"));
		this.transactionsType.setCellValueFactory(new PropertyValueFactory<>("Type"));

		// apply appropriate formatting to the rows depending on the Transaction Type field
		this.transactionsTable.setRowFactory(row -> new TableRow<Transaction>() {
			@Override
			public void updateItem(Transaction t, boolean empty) {
				if (t == null || empty) {
					setStyle("");
				} else {
					setStyle(t.getType().getCssStyle());
				}
			}
		});

		// disable user sorting of TableView by column header
		this.transactionsTable.getColumns().forEach(element -> {
			element.setSortable(false);
		});

		// apply appropriate formatting to the Transaction value cells, depending on if the Transaction isIncome()
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

		// set TableColumn widths
		this.transactionsType.setPrefWidth(150.0);

		// configure the TableViewSelectionModel for TableView
		TableViewSelectionModel<Transaction> selectionModel = this.transactionsTable.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		selectionModel.setCellSelectionEnabled(false);
		this.transactionsTable.setSelectionModel(selectionModel);

		// define and set EventHandlers for the Type filter CheckBox controls
		EventHandler<ActionEvent> checkBoxTypeFilterAction = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// capture CheckBox
				CheckBox c = (CheckBox) event.getSource();
				String s = c.getId().substring("filter".length()).toUpperCase();
				// set this filter to user's selection.
				transactionTypeFilters.put(s, c.isSelected());
				// refresh this window.
				refresh();
			}
		};
		filterCash.setOnAction(checkBoxTypeFilterAction);
		filterDirectDebit.setOnAction(checkBoxTypeFilterAction);
		filterStandingOrder.setOnAction(checkBoxTypeFilterAction);
		filterBankTransfer.setOnAction(checkBoxTypeFilterAction);

		// define and set EventHandlers for the In/Out filter CheckBox controls
		EventHandler<ActionEvent> checkBoxInOutFilterAction = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				CheckBox c = (CheckBox) event.getSource();
				String s = c.getId().substring("filter".length());
				transactionInOutFilters.put(s, c.isSelected());
				refresh();
			}
		};
		filterIncome.setOnAction(checkBoxInOutFilterAction);
		filterOutgoing.setOnAction(checkBoxInOutFilterAction);

		// retrieve transactions from database
		transactionsObsList = FXCollections.observableList(new ArrayList<Transaction>(this.selectedMonth.getTransactions()));
		transactionsFilteredOut = new LinkedList<Transaction>();
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
		refresh(); // default to showing the current year
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
	 * @param transactionsTable
	 */
	private void refresh() {
		fillTable();
		fillTotals();
	}

	/**
	 * Fill totals TextFields with Transaction data summary for this Month/
	 * 
	 * @param transactionsTable
	 */
	private void fillTotals() {
		// declare calculation variables
		double in = 0, out = 0, balance = 0;

		// total the income and outgoings for all Transactions in this Month
		for (Transaction t : selectedMonth.getTransactions()) {
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
	 * Fill TableView with Transactions in the {@code transactionObsList} ObservableList
	 * 
	 */
	private void fillTable() {
		filterTransactions();
		this.transactionsTable.setItems(transactionsObsList);
		this.transactionsTable.refresh();

	}

	/**
	 * Filters the {@code Transaction} objects in the {@code transactionObsList}, depending on whether {@code Transaction Type} for each element has a
	 * true or false key in the {@code transactionFilters}.
	 */
	private void filterTransactions() {
		transactionsObsList.addAll(transactionsFilteredOut);
		transactionsFilteredOut.clear();
		List<Transaction> toRemove = new LinkedList<Transaction>();
		for (Transaction t : transactionsObsList) {
			String inOut = t.isIncome() ? "Income" : "Outgoing";
			if (!transactionTypeFilters.get(t.getType().name()) || !transactionInOutFilters.get(inOut)) {
				toRemove.add(t);
			}
		}
		transactionsObsList.removeAll(toRemove);
		transactionsFilteredOut.addAll(toRemove);
	}
}