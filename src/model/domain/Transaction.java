package model.domain;

import static model.domain.Utility.*;

import java.time.LocalDate;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import model.db.Constants;

/**
 * An object class representing a monetary transaction. Parameter validations are provided by {@link model.domain.Utility} class.<br>
 * Natural order is {@link #compareTo(Transaction) chronological}.
 * 
 * @author Peter Marley
 *
 */
public class Transaction implements Comparable<Transaction> {

	public enum Type {
		CASH("Cash", "-fx-background-color: #bbbbbb"),
		BANKTRANSFER("Bank Transfer", "-fx-background-color: #aaaaaa"),
		DIRECTDEBIT("Direct Debit", "-fx-background-color: #999999"),
		STANDINGORDER("Standing Order", "-fx-background-color: #888888");

		private String text;
		private String cssStyle;

		private Type(String text, String cssStyle) {
			this.text = text;
			this.cssStyle = cssStyle;
		}

		public String toString() {
			return this.text;
		}

		public String getCssStyle() {
			return this.cssStyle;
		}
	}

	//	private boolean income;
	//	private LocalDate date;
	//	private Type type;
	//	private double value;
	//
	//	private String name;
	//	private boolean paid;

	private SimpleBooleanProperty income;
	private SimpleStringProperty date;
	private SimpleStringProperty type;
	private SimpleDoubleProperty value;

	private SimpleStringProperty name;
	private SimpleBooleanProperty isPaid;

	//------------------------------\
	//	Construction				|
	//------------------------------/

	/**
	 * Create a Transaction object
	 * 
	 * @param date     the date of this transaction
	 * @param isIncome true = income, false = outgoing
	 * @param type
	 * @param value    the value of this transaction
	 */

	public Transaction(String name, boolean isPaid, LocalDate date, boolean isIncome, Type type, double value) {
		this.setName(name);
		this.setPaid(isPaid);
		this.setDate(date);
		this.setType(type);
		this.setValue(value);
		this.setIncome(isIncome);
	}

	//------------------------------\
	//	Setters						|
	//------------------------------/

	/**
	 * @param isIncome the isIncome to set
	 */
	private void setIncome(boolean isIncome) {
		this.income = new SimpleBooleanProperty(isIncome);

	}

	/**
	 * @param isPaid the isPaid to get
	 */
	private void setPaid(boolean isPaid) {
		this.isPaid = new SimpleBooleanProperty(isPaid);

	}

	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = new SimpleStringProperty(nullCheck(date).format(Constants.FORMAT_YYYYMMDD));
	}

	/**
	 * @param name the name to set
	 */
	private void setName(String name) {
		this.name = new SimpleStringProperty(validate(name, 1, null));
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = new SimpleStringProperty(nullCheck(type).name());
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = new SimpleDoubleProperty(validate(value, 0.0, null));
	}

	//------------------------------\
	//	Getters						|
	//------------------------------/

	/**
	 * @return the income
	 */
	public Boolean isIncome() {
		return income.get();
	}

	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return LocalDate.parse(date.get(), Constants.FORMAT_YYYYMMDD);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * 
	 * @return the paid
	 */
	public boolean isPaid() {
		return isPaid.get();
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return Type.valueOf(type.get());
	}

	/**
	 * Get the value of this Transaction, appropriate positive or negative, depending on whether this Transaction {@code isIncome()}.
	 * 
	 * @return the value, appropriately positive or negative depending on income field
	 */
	public double getValue() {
		if (isIncome()) {
			return value.get();
		} else {
			return 0 - value.get();
		}
	}

	/**
	 * Get the absolute value of this transaction, always positive.
	 * 
	 * @return the value, always positive
	 */
	public double getAbsoluteValue() {
		return this.value.get();
	}

	//------------------------------\
	// Overrides & Interface Impl	|
	//------------------------------/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (isIncome() ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		long temp;
		temp = Double.doubleToLongBits(getValue());
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * An object is equal to this object if the memory position of the objects is equal or the class, type, and date (month and year only) are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		// EQUAL it is the same object in memory
		if (this == obj)
			return true;

		// UNEQUAL if obj is null
		if (obj == null)
			return false;

		// UNEQUAL if RuntimeClass is different
		if (getClass() != obj.getClass())
			return false;

		Transaction other = (Transaction) obj;
		// UNEQUAL if this date is null, and other date is not null
		if (date == null) {
			if (other.date != null)
				return false;

			// UNEQUAL if date fields are not equal
			//} else if (!date.equals(other.date))
		} else if (getDate().getYear() != other.getDate().getYear() && getDate().getMonth() != other.getDate().getMonth())
			return false;
		// UNEQUAL if income fields are not equal
		if (isIncome() != other.isIncome())
			return false;
		// UNEQUAL if type fields are not equal
		if (getType() != other.getType())
			return false;
		// UNEQUAL if value fields are not equal
		if (Double.doubleToLongBits(getValue()) != Double.doubleToLongBits(other.getValue()))
			return false;
		if (!this.getName().equals(other.getName()))
			return false;
		// OTHERWISE EQUAL
		return true;
	}

	/**
	 * A {@code Transaction} object's total ordering is chronologically by difference in months. If months are chronologically equal, then all fields are
	 * compared, if they are equal the value is compared? (is this working as intended?)
	 */
	@Override
	public int compareTo(Transaction t) {
		int diff = monthDifferential(this.getDate(), t.getDate());
		if (diff == 0) { // if same month chronologically

			if (this.equals(t)) { // if object fields are equal
				return 0;
			} else {				// otherwise return value difference
				if (this.getValue() - t.getValue() < 0) {
					return -1;
				} else if (this.getValue() - t.getValue() > 0) {
					return 1;
				}
			}

		}
		return diff;
	}

	@Override
	public String toString() {
		return "Transaction [income=" + isIncome() + ", date=" + getDate() + ", type=" + getType() + ", value=" + getValue() + "]";
	}

}
