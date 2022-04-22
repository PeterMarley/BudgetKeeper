package view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;
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

	//**********************************\
	//									|
	//	Instance Fields					|
	//									|
	//**********************************/

	// TODO which one to keep?
	//private Month m;
	private Month month;

	private FXMLLoader loader;
	private Parent root;
	private Stage stage;
	private Scene scene;

	//**********************************\
	//									|
	//	Constants						|
	//									|
	//**********************************/

	private static final String FXML = "./fxml/WindowMonth.fxml";

	//**********************************\
	//									|
	//	Construction					|
	//									|
	//**********************************/

	public WindowMonth(Month m) {
		try {
			this.month = m;
			this.loader = new FXMLLoader(getClass().getResource(FXML));
			this.loader.setController(this);
			this.root = loader.load();

			this.scene = new Scene(root);
			this.stage = new Stage();

			this.stage.setScene(scene);

		} catch (IOException e) {
			System.err.println("FXMLLoader.load IOException:");
			e.printStackTrace();
		}
	}

	//**********************************\
	//									|
	//	JavaFX Application Methods		|
	//									|
	//**********************************/

	public void show() {
		initialise(); // default to showing the current year
		this.stage.show();
	}

	public void hide() {
		this.stage.hide();
	}

	/*
	 * Controller Configuration
	 */

	public void initialise() {
		refresh(month.getTransactions());
	}

	private void refresh(Collection<Transaction> transactions) {
		fillTable(transactions);
		fillTotals(transactions);

		//		FXCollections.observableArrayList(Controller.getDAO().pullTransactionsForMonth(Controller.getDAO().pullMonthID(month))));
		//		SortedList<Transaction> sl = new SortedList<Transaction>();
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
		if (balance > 0) {
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
		List<Transaction> sortedTransactions = new ArrayList<Transaction>(transactions);

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
		/*
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

	}
}