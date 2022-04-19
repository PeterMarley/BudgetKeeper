package view.controllers;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.db.Constants;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;

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

	// cash table
	@FXML
	private TableView<Transaction> transactionsCash;
	@FXML
	private TableColumn<Transaction, LocalDate> transactionsCash_date;
	@FXML
	private TableColumn<Transaction, String> transactionsCash_name;
	@FXML
	private TableColumn<Transaction, Double> transactionsCash_value;
	@FXML
	private TableColumn<Transaction, Boolean> transactionsCash_paid;
	
	// direct debit table
	@FXML
	private TableView<Transaction> transactionsDirectDebit;
	@FXML
	private TableView<Transaction> transactionsStandingOrder;
	@FXML
	private TableView<Transaction> transactionsBankTransfer;

	public void initialise(Month m) {
		this.month = m;

		initCash(getTransactionsOfType(Type.CASH));
		initDirectDebit(getTransactionsOfType(Type.DIRECT_DEBIT));
		initStandingOrder(getTransactionsOfType(Type.STANDING_ORDER));
		initBankTransfer(getTransactionsOfType(Type.BANK_TRANSFER));
	}

	private void initCash(List<Transaction> transactions) {

	}

	private void initDirectDebit(List<Transaction> transactions) {

	}

	private void initStandingOrder(List<Transaction> transactions) {

	}

	private void initBankTransfer(List<Transaction> transactions) {

	}

	private List<Transaction> getTransactionsOfType(Type type) {
		List<Transaction> results = new LinkedList<Transaction>();
		for (Transaction t : this.month.getTransactions()) {
			if (t.getType() == type) {
				results.add(t);
			}
		}
		return results;
	}

	private class TransactionModel {

		private SimpleBooleanProperty income;
		private SimpleStringProperty date;
		private SimpleStringProperty type;
		private SimpleDoubleProperty value;

		private SimpleStringProperty name;
		private SimpleBooleanProperty paid;

		protected TransactionModel(Transaction t) {
			this.setIncome(t.isIncome());
			this.setDate(t.getDate());
			this.setType(t.getType());
			this.setValue(t.getValue());
			this.setName(t.getName());
			this.setPaid(t.isPaid());
		}

		/**
		 * @param income the income to set
		 */
		public void setIncome(boolean income) {
			this.income = new SimpleBooleanProperty(income);
		}

		/**
		 * @param date the date to set
		 */
		public void setDate(LocalDate date) {
			this.date = new SimpleStringProperty(date.format(Constants.FORMAT_YYYYMMDD));
		}

		/**
		 * @param type the type to set
		 */
		public void setType(Type type) {
			this.type = new SimpleStringProperty(type.toString());
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(double value) {
			this.value = new SimpleDoubleProperty(value);
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = new SimpleStringProperty(name);
		}

		/**
		 * @param paid the paid to set
		 */
		public void setPaid(boolean paid) {
			this.paid = new SimpleBooleanProperty(paid);
		}

		/**
		 * @return the transaction
		 */
		public Transaction getTransaction() {
			return new Transaction(this.getName(), this.getPaid(), this.getDate(), this.getIncome(), this.getType(), this.getValue());
		}

		/**
		 * @return the income
		 */
		public boolean getIncome() {
			return income.get();
		}

		/**
		 * @return the date
		 */
		public LocalDate getDate() {
			return LocalDate.parse(date.get(), Constants.FORMAT_YYYYMMDD);
		}

		/**
		 * @return the type
		 */
		public Type getType() {
			return Type.valueOf(type.get());
		}

		/**
		 * @return the value
		 */
		public double getValue() {
			return value.get();
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name.get();
		}

		/**
		 * @return the paid
		 */
		public boolean getPaid() {
			return paid.get();
		}

	}
}
