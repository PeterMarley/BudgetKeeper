package view.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import controller.Controller;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import model.db.Constants;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;
import model.domain.comparators.TransactionComparatorDate;
import model.domain.comparators.TransactionComparatorIncome;
import model.domain.comparators.TransactionComparatorType;

public class WindowMonthController {

	/*
	           <TableColumn fx:id="transactionsCash_date" prefWidth="75.0" text="C1" />
	      <TableColumn fx:id="transactionsCash_name" prefWidth="75.0" text="C2" />
	        <TableColumn fx:id="transactionsCash_value" prefWidth="75.0" text="C2" />
	        <TableColumn fx:id="transactionsCash_paid" prefWidth="75.0" text="C2" />
	    </columns>
	  </TableView>
	  <TableView fx:id="transactionsDirectDebit" layoutX="25.0" layoutY="566.0" prefHeight="113.0" prefWidth="825.0">
	     <columns>
	        <TableColumn fx:id="transactionsDirectDebit_date" prefWidth="75.0" text="C1" />
	        <TableColumn fx:id="transactionsDirectDebit_name" prefWidth="75.0" text="C2" />
	        <TableColumn fx:id="transactionsDirectDebit_value" prefWidth="75.0" text="C2" />
	        <TableColumn fx:id="transactionsDirectDebit_paid" prefWidth="75.0" text="C2" />
	     </columns>
	  </TableView>
	  <TableView fx:id="transactionsStandingOrder" layoutX="33.0" layoutY="699.0" prefHeight="106.0" prefWidth="825.0">
	     <columns>
	        <TableColumn fx:id="transactionsStandingOrder_date" prefWidth="75.0" text="C1" />
	        <TableColumn fx:id="transactionsStandingOrder_name" prefWidth="75.0" text="C2" />
	        <TableColumn fx:id="transactionsStandingOrder_value" prefWidth="75.0" text="C2" />
	        <TableColumn fx:id="transactionsStandingOrder_paid" prefWidth="75.0" text="C2" />
	     </columns>
	  </TableView>
	  <TableView fx:id="transactionsBankTransfer" layoutX="25.0" layoutY="445.0" prefHeight="106.0" prefWidth="825.0">
	     <columns>
	        <TableColumn fx:id="transactionsBankTransfer_date" prefWidth="75.0" text="C1" />
	        <TableColumn fx:id="transactionsBankTransfer_name" prefWidth="75.0" text="C2" />
	        <TableColumn fx:id="transactionsBankTransfer_value" prefWidth="75.0" text="C2" />
	        <TableColumn fx:id="transactionsBankTransfer_paid" prefWidth="75.0" text="C2" />
	     </columns> 
	 */
	private Month month;

	/*
	 * Table
	 */
	@FXML
	private TableView<Transaction> transactions;
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

	/*
	 * totals
	 */
	@FXML
	private TextField totalIn;
	@FXML
	private TextField totalOut;
	@FXML
	private TextField totalBalance;

	public void initialise(Month m) {
		this.month = m;
		refresh(month.getTransactions());
	}

	private void refresh(Collection<Transaction> transactions) {
		fillTable(transactions);
		fillTotals(transactions);

		//		FXCollections.observableArrayList(Controller.getDAO().pullTransactionsForMonth(Controller.getDAO().pullMonthID(month))));
		//		SortedList<Transaction> sl = new SortedList<Transaction>();
	}

	private void fillTotals(Collection<Transaction> transactions) {
		double totalIn = 0, totalOut = 0, totalBalance = 0;

		for (Transaction t : transactions) {
			if (t.isIncome()) {
				totalIn += t.getValue();
			} else {
				totalOut += t.getValue();
			}
		}
		totalBalance = totalIn - totalOut;

		this.totalIn.setText(String.format("%.2f", totalIn));
		this.totalOut.setText(String.format("%.2f", totalOut));
		this.totalBalance.setText(String.format("%s%.2f", (totalBalance > 0) ? "+" : "", totalBalance));
	}

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

		this.transactions.setItems(FXCollections.observableArrayList(sortedTransactions));
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
	}

	//	private class Transaction {
	//
	//		private SimpleBooleanProperty income;
	//		private SimpleStringProperty date;
	//		private SimpleStringProperty type;
	//		private SimpleDoubleProperty value;
	//
	//		private SimpleStringProperty name;
	//		private SimpleBooleanProperty paid;
	//
	//		protected Transaction(Transaction t) {
	//			this.setIncome(t.isIncome());
	//			this.setDate(t.getDate());
	//			this.setType(t.getType());
	//			this.setValue(t.getValue());
	//			this.setName(t.getName());
	//			this.setPaid(t.isPaid());
	//		}
	//
	//		/**
	//		 * @param income the income to set
	//		 */
	//		public void setIncome(boolean income) {
	//			this.income = new SimpleBooleanProperty(income);
	//		}
	//
	//		/**
	//		 * @param date the date to set
	//		 */
	//		public void setDate(LocalDate date) {
	//			this.date = new SimpleStringProperty(date.format(Constants.FORMAT_YYYYMMDD));
	//		}
	//
	//		/**
	//		 * @param type the type to set
	//		 */
	//		public void setType(Type type) {
	//			this.type = new SimpleStringProperty(type.toString());
	//		}
	//
	//		/**
	//		 * @param value the value to set
	//		 */
	//		public void setValue(double value) {
	//			this.value = new SimpleDoubleProperty(value);
	//		}
	//
	//		/**
	//		 * @param name the name to set
	//		 */
	//		public void setName(String name) {
	//			this.name = new SimpleStringProperty(name);
	//		}
	//
	//		/**
	//		 * @param paid the paid to set
	//		 */
	//		public void setPaid(boolean paid) {
	//			this.paid = new SimpleBooleanProperty(paid);
	//		}
	//
	//		/**
	//		 * @return the transaction
	//		 */
	//		public Transaction getTransaction() {
	//			return new Transaction(this.getName(), this.getPaid(), this.getDate(), this.getIncome(), this.getType(), this.getValue());
	//		}
	//
	//		/**
	//		 * @return the income
	//		 */
	//		public boolean getIncome() {
	//			return income.get();
	//		}
	//
	//		/**
	//		 * @return the date
	//		 */
	//		public LocalDate getDate() {
	//			return LocalDate.parse(date.get(), Constants.FORMAT_YYYYMMDD);
	//		}
	//
	//		/**
	//		 * @return the type
	//		 */
	//		public Type getType() {
	//			return Type.valueOf(type.get());
	//		}
	//
	//		/**
	//		 * @return the value
	//		 */
	//		public double getValue() {
	//			return value.get();
	//		}
	//
	//		/**
	//		 * @return the name
	//		 */
	//		public String getName() {
	//			return name.get();
	//		}
	//
	//		/**
	//		 * @return the paid
	//		 */
	//		public boolean getPaid() {
	//			return paid.get();
	//		}
	//
	//	}
}
