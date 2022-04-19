package view.controllers;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import model.db.Constants;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;

public class WindowMonthController {
	private Month month;

	@FXML
	private TableView<Transaction> transactionsCash;
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

		public TransactionModel(Transaction t) {
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
