package view;

import java.io.IOException;
import java.nio.file.FileSystemLoopException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import model.domain.comparators.Sort;
import model.domain.comparators.TransactionComparatorDate;
import model.domain.comparators.TransactionComparatorIncome;
import model.domain.comparators.TransactionComparatorType;
import model.domain.comparators.TransactionComparatorValue;

public class WindowMonth {

	//**********************************\
	//									|
	//	JavaFX Controls					|
	//									|
	//**********************************/

	@FXML private TableView<Transaction> transactionsTable;
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
	@FXML private CheckBox filterIncome;
	@FXML private CheckBox filterOutgoing;

	@FXML private Button sortButtonInOut;
	@FXML private Button sortButtonType;
	@FXML private Button sortButtonDate;
	@FXML private Button sortButtonValue;
	@FXML private Button sortButtonClear;

	@FXML private Button opTransactionAdd;
	@FXML private Button opTransactionEdit;
	@FXML private Button opTransactionDelete;
	@FXML private Button opSave;
	@FXML private Button opCancel;

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

	private int monthID;

	private Sort sortLatchInOut;
	private Sort sortLatchType;
	private Sort sortLatchDate;
	private Sort sortLatchValue;

	private HashMap<String, Boolean> filterMap;
	private HashMap<String, Boolean> filtersIncome;

	private ObservableList<Transaction> tActive;
	private LinkedList<Transaction> tFiltered;

	/**
	 * map of<br>
	 * <b>Key:</b> Transaction <br>
	 * <b>Value:</b> Integer Transaction ID
	 */
	private HashMap<Transaction, Integer> mapOftIDs;

	private boolean unsavedChanges = false;

	//**********************************\
	//									|
	//	Constants						|
	//									|
	//**********************************/

	private static final String FXML = "./fxml/WindowMonth.fxml";
	private static final String CSS = "./css/WindowMonth.css";
	private static final String ICON = "./img/icons/document.png";

	private static final String SORT_BUTTON_DEFAULT_DATE = "Date";
	private static final String SORT_BUTTON_DEFAULT_TYPE = "Type";
	private static final String SORT_BUTTON_DEFAULT_VALUE = "Value";
	private static final String SORT_BUTTON_DEFAULT_IN_OUT = "In/ Out";

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
	 * @category Construction
	 * @param month
	 * @throws IOException              if FXMLLoader fails to load the FXML file.
	 * @throws IllegalArgumentException if month is null or monthID is negative.
	 */
	public WindowMonth(Month month, int monthID) throws IOException, IllegalArgumentException {
		setSelectedMonth(month, monthID);
		setTransactionIDs();
		setRoot();
		setScene();
		setStage();
		initialise();
		Controller.setWindowMonth(this);
	}

	/**
	 * Set the {@code selectedMonth} and {@code monthID} fields.
	 * 
	 * @category Construction *
	 * @param month
	 * @param monthID
	 */
	private void setSelectedMonth(Month month, int monthID) {
		this.selectedMonth = month;
		this.monthID = monthID;
	}

	private void setTransactionIDs() {
		mapOftIDs = Controller.getDatabaseAccessObject().mapTransactionIDs(monthID, new LinkedList<Transaction>(selectedMonth.getTransactions()));
	}

	/**
	 * Set the {@code FXMLLoader}, and load the FXML. Additionally, set the controller for this scene-graph.
	 * 
	 * @category Construction
	 * @throws IOException if an error occurs during FXML loading during {@link FXMLLoader#load() loading}.
	 */
	private void setRoot() throws IOException {
		this.loader = new FXMLLoader(getClass().getResource(FXML));
		this.loader.setController(this);
		this.root = loader.load();

	}

	/**
	 * Set the {@code Scene} of this scene-graph, and apply CSS style sheets to the scene.
	 * 
	 * @category Construction
	 */
	private void setScene() {
		this.scene = new Scene(this.root);
		this.scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());

	}

	/**
	 * Set the {@code Stage} of this scene-graph. Title, icon and various configurations are applied here.
	 * 
	 * @category Construction
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
				Controller.getWindowYear().show();
			}
		});
	}

	//**********************************\
	//									|
	//	Initialisations					|
	//									|
	//**********************************/

	/**
	 * Configure the TableView control.
	 */
	private void initialise() {
		initTableView();
		initTotals();
		initFilters();
		initSorts();
		initOperations();
		initData();

	}

	/**
	 * Generate the {@code HashMap} that contains key value pairs for the filter {@code CheckBox} nodes. The key is the {@code name} of the
	 * {@link Transaction.Type Type} enum in the {@link model.domain.Transaction Transaction} class, and the value is a {@code Boolean}, representing
	 * "should this
	 * transaction type be displayed in the {@code transactionsTable} Node?"
	 * 
	 * @category JavaFXControlConfiguration
	 */
	private void initFilters() {
		// Transaction type filters
		this.filterMap = new HashMap<String, Boolean>();
		for (Type type : Type.values()) {
			filterMap.put(type.name(), true);
		}
		this.filterCash.setSelected(true);
		this.filterDirectDebit.setSelected(true);
		this.filterStandingOrder.setSelected(true);
		this.filterBankTransfer.setSelected(true);

		// Transaction in/out filters
		this.filtersIncome = new HashMap<String, Boolean>();
		this.filtersIncome.put("Income", true);
		this.filtersIncome.put("Outgoing", true);

		this.filterIncome.setSelected(true);
		this.filterOutgoing.setSelected(true);

		// define and set EventHandlers for the Type filter CheckBox controls
		EventHandler<ActionEvent> checkBoxTypeFilterAction = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent filterToggled) {
				if (filterToggled.getSource() instanceof CheckBox) {

					// capture CheckBox and filterName
					CheckBox filterCheckBox = (CheckBox) filterToggled.getSource();
					String filterName = filterCheckBox.getId().substring("filter".length()).toUpperCase();

					// set this filter to if currently selected
					filterMap.put(filterName, filterCheckBox.isSelected());

					// refresh this window.
					refresh();
				}
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
				if (event.getSource() instanceof CheckBox) {

					// Capture CheckBox and filterName
					CheckBox c = (CheckBox) event.getSource();
					String s = c.getId().substring("filter".length());

					// set this filter to if currently selected
					filtersIncome.put(s, c.isSelected());

					// refresh this window.
					refresh();
				}
			}
		};
		filterIncome.setOnAction(checkBoxInOutFilterAction);
		filterOutgoing.setOnAction(checkBoxInOutFilterAction);
	}

	private void initTableView() {
		/*
		 * TableView
		 */

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
				super.updateItem(t, empty);
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
		this.transactionsName.setPrefWidth(150.0);

	}

	private void initTotals() {
		this.totalIn.setDisable(true);
		this.totalOut.setDisable(true);
		this.totalBalance.setDisable(true);
		fillTotals();
	}

	private void initSorts() {
		/*
		 * Sorts
		 */

		sortButtonInOut.setText(SORT_BUTTON_DEFAULT_IN_OUT);
		sortButtonInOut.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				sortLatchInOut = (sortLatchInOut == null) ? Sort.DESCENDING : ((sortLatchInOut == Sort.ASCENDING) ? Sort.DESCENDING : Sort.ASCENDING);
				sortButtonInOut.setText(SORT_BUTTON_DEFAULT_IN_OUT + " " + sortLatchInOut.toString());
				tActive.sort(new TransactionComparatorIncome(sortLatchInOut));
				transactionsTable.refresh();
			}
		});
		sortButtonType.setText(SORT_BUTTON_DEFAULT_TYPE);
		sortButtonType.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				sortLatchType = (sortLatchType == null) ? Sort.DESCENDING : ((sortLatchType == Sort.ASCENDING) ? Sort.DESCENDING : Sort.ASCENDING);
				sortButtonType.setText(SORT_BUTTON_DEFAULT_TYPE + " " + sortLatchType.toString());
				tActive.sort(new TransactionComparatorType(sortLatchType));
				transactionsTable.refresh();
			}
		});
		sortButtonDate.setText(SORT_BUTTON_DEFAULT_DATE);
		sortButtonDate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				sortLatchDate = (sortLatchDate == null) ? Sort.ASCENDING : ((sortLatchDate == Sort.ASCENDING) ? Sort.DESCENDING : Sort.ASCENDING);
				sortButtonDate.setText(SORT_BUTTON_DEFAULT_DATE + " " + sortLatchDate.toString());
				tActive.sort(new TransactionComparatorDate(sortLatchDate));
				transactionsTable.refresh();
			}
		});
		sortButtonValue.setText(SORT_BUTTON_DEFAULT_VALUE);
		sortButtonValue.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				sortLatchValue = (sortLatchValue == null) ? Sort.DESCENDING : ((sortLatchValue == Sort.ASCENDING) ? Sort.DESCENDING : Sort.ASCENDING);
				sortButtonValue.setText(SORT_BUTTON_DEFAULT_VALUE + " " + sortLatchValue.toString());
				tActive.sort(new TransactionComparatorValue(sortLatchValue));
				transactionsTable.refresh();
			}
		});
		sortButtonClear.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				sortLatchDate = null;
				sortLatchInOut = null;
				sortLatchType = null;
				sortLatchValue = null;
				sortButtonDate.setText(SORT_BUTTON_DEFAULT_DATE);
				sortButtonInOut.setText(SORT_BUTTON_DEFAULT_IN_OUT);
				sortButtonType.setText(SORT_BUTTON_DEFAULT_TYPE);
				sortButtonValue.setText(SORT_BUTTON_DEFAULT_VALUE);
				defaultSortTransactions();
				transactionsTable.refresh();
			}
		});
	}

	private void initData() {
		// retrieve transactions from database
		tActive = FXCollections.observableList(new ArrayList<Transaction>(mapOftIDs.keySet()));

		// instantiate list for holding filtered Transactions
		tFiltered = new LinkedList<Transaction>();

		// apply default sort
		defaultSortTransactions();

	}

	private void initOperations() {
		/*
		 * Operations
		 */

		// save Transactions
		// TODO not finalised OR tested!!!!!
		opSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Controller.getDatabaseAccessObject().updateMonth(selectedMonth, monthID, mapOftIDs);
				unsavedChanges = false;
				refresh();
			}
		});

		opTransactionAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					WindowTransaction wt = new WindowTransaction(monthID);
					wt.show();
				} catch (IOException e) {
					System.err.println("WindowTransaction to Add Transaction instantiation failed!");
					e.printStackTrace();
				}
			}
		});
		opTransactionEdit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Transaction selected = transactionsTable.getSelectionModel().getSelectedItem();
				int transactionID = -1;
				if (selected != null) {
					try {
						for (Map.Entry<Transaction, Integer> entry : mapOftIDs.entrySet()) {
							int tID = entry.getValue();
							Transaction t = entry.getKey();
							if (t.equals(selected)) {
								transactionID = tID;
								break;
							}
						}
						if (transactionID != -1) {
							WindowTransaction wt = new WindowTransaction(transactionID, selected);
							wt.show();
						} else {
							System.out.println("Transaction not found!");
						}
					} catch (IllegalArgumentException | IOException e) {
						System.err.println("Failed to instantiate a WindowTransaction with Transaction :" + selected.toString());
						e.printStackTrace();
					}
				}
			}
		});

		/*
		 * Get Transactions from database and store as ObservableList, sorted via the defaultSortTransactions() method
		 */
	}

	//**********************************\
	//									|
	//	JavaFX Application Methods		|
	//									|
	//**********************************/

	/**
	 * Update a Transaction for this Month.
	 * 
	 * @param transactionID
	 * @param original
	 * @param edited
	 * @throws IllegalArgumentException if transactionID is negative, either Transaction argument is null, or if tActive or tFiltered fields are null.
	 */
	public void updateTransaction(int transactionID, Transaction original, Transaction edited) throws IllegalArgumentException {
		Utility.nullCheck(original);
		Utility.nullCheck(edited);
		Utility.nullCheck(tActive);
		Utility.nullCheck(tFiltered);
		Utility.validate(transactionID, 0, null);

		tActive.addAll(tFiltered);
		tFiltered.clear();
		mapOftIDs.remove(original);
		mapOftIDs.put(edited, transactionID);

		if (tActive.contains(original)) {
			tActive.remove(original);
			tActive.add(edited);
			unsavedChanges = true;
		}
		defaultSortTransactions();
		refresh();
	}

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
	public void refresh() {
		try {
			fillTable();
			fillTotals();
			colorUnsavedChanges();
		} catch (Exception e) {
			System.out.println("WindowMonth.refresh() did not like that one bit...");
			e.printStackTrace();
		}
	}

	private void colorUnsavedChanges() {
		String style;
		if (unsavedChanges) {
			style = "-fx-text-fill: red";
		} else {
			style = "-fx-text-fill: black";
		}
		opSave.setStyle(style);

	}

	/**
	 * Fill totals TextFields with Transaction data summary for this Month/
	 * 
	 * @param transactionsTable
	 */
	private void fillTotals() {
		// declare calculation variables
		double balance = selectedMonth.getBalance();

		// set the relevant buttons to show the calculations
		this.totalIn.setText(String.format("%.2f", selectedMonth.getIncome()));
		this.totalOut.setText(String.format("%.2f", selectedMonth.getOutgoing()));
		this.totalBalance.setText(String.format("%.2f", balance));

		StringBuilder style = new StringBuilder();
		if (balance >= 0) {
			style.append("-fx-text-fill: green;");
		} else {
			style.append("-fx-text-fill: red;");
		}
		this.totalBalance.setStyle(style.toString());

	}

	/**
	 * Fill TableView with Transactions in the {@code transactionObsList} ObservableList
	 * 
	 */
	private void fillTable() {
		filterTransactions();
		this.transactionsTable.setItems(tActive);
		// configure the TableViewSelectionModel for TableView
		TableViewSelectionModel<Transaction> selectionModel = this.transactionsTable.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		//selectionModel.setCellSelectionEnabled(false);
		this.transactionsTable.setSelectionModel(selectionModel);
		this.transactionsTable.requestFocus();
		this.transactionsTable.setDisable(false);
		this.transactionsTable.refresh();

	}

	/**
	 * Filters the {@code Transaction} objects in the {@code transactionObsList}, depending on whether {@code Transaction Type} for each element has a
	 * true or false key in the {@code transactionFilters}.
	 */
	private void filterTransactions() {
		tActive.addAll(tFiltered);
		tFiltered.clear();
		List<Transaction> toRemove = new LinkedList<Transaction>();
		for (Transaction t : tActive) {
			String inOut = t.isIncome() ? "Income" : "Outgoing";
			if (!filterMap.get(t.getType().name()) || !filtersIncome.get(inOut)) {
				toRemove.add(t);
			}
		}
		tActive.removeAll(toRemove);
		tFiltered.addAll(toRemove);
	}

	/**
	 * This method is called whenever the "default" sorting of the {@code transactionsObsList} must be initially set, or restored.
	 */
	private void defaultSortTransactions() {
		tActive.sort(new TransactionComparatorDate());
		tActive.sort(new TransactionComparatorValue(Sort.DESCENDING));
		tActive.sort(new TransactionComparatorType());
		tActive.sort(new TransactionComparatorIncome(Sort.DESCENDING));
	}
}