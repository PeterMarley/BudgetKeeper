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

	public static final int NEW_ID = -1;

	
	/**
	 * The various types of Transaction available.
	 * 
	 * @author Peter Marley
	 * @StudentNumber 13404067
	 * @Email pmarley03@qub.ac.uk
	 * @GitHub https://github.com/PeterMarley
	 *
	 */
	public enum Type {
		CASH("Cash", "-fx-background-color: #E1F6FF"),
		BANKTRANSFER("Bank Transfer", "-fx-background-color: #B9EBFF"),
		DIRECTDEBIT("Direct Debit", "-fx-background-color: #91E0FF"),
		STANDINGORDER("Standing Order", "-fx-background-color: #55CEFF");

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

	private SimpleBooleanProperty income;
	private SimpleStringProperty date;
	private SimpleStringProperty type;
	private SimpleDoubleProperty value;

	private SimpleStringProperty name;
	private SimpleBooleanProperty isPaid;

	private int transactionID;

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
		this.setTransactionID(this.hashCode());
	}

	/**
	 * Create a Transaction object and set its transactionID specifically
	 * 
	 * @param date     the date of this transaction
	 * @param isIncome true = income, false = outgoing
	 * @param type
	 * @param value    the value of this transaction
	 * @param transID
	 */

	public Transaction(String name, boolean isPaid, LocalDate date, boolean isIncome, Type type, double value, int transID) {
		this.setName(name);
		this.setPaid(isPaid);
		this.setDate(date);
		this.setType(type);
		this.setValue(value);
		this.setIncome(isIncome);
		this.setTransactionID(transID);
		//System.out.println(this.toString());
		//System.out.println(hashCode());

	}

	//------------------------------\
	//	Setters						|
	//------------------------------/

	/**
	 * @param isIncome the isIncome to set
	 */
	public void setIncome(boolean isIncome) {
		this.income = new SimpleBooleanProperty(isIncome);

	}

	/**
	 * @param isPaid the isPaid to get
	 */
	public void setPaid(boolean isPaid) {
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
	public void setName(String name) {
		this.name = new SimpleStringProperty(validate(name, 1, null));
	}

	/**
	 * Update the transactionID of this object to its current hashCode.
	 */
	public void setTransactionID(int transID) {
		//System.out.println("========================");
		//System.out.println("Setting tID in updateTransactionID " + transID);
		this.transactionID = transID;

		//System.out.println(this.toString());
		//System.out.println("========================");

	}

	public void updateTransactionID() {
		setTransactionID(hashCode()); // TODO implement a tID seed generator in DAO (or somewhere else)
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
	 * If this object has been changed since construction, then the transactionID (that is set on construction to the hashCode()) will not equal the
	 * current hashCode();
	 * 
	 * @return
	 */
	public boolean hasChanged() {
		return this.hashCode() != this.transactionID;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return Type.valueOf(type.get());
	}

	public int getTransactionID() {
		return this.transactionID;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.get().hashCode());
		result = prime * result + ((income == null) ? 0 : (income.get()) ? 2 : 4);
		result = prime * result + ((isPaid == null) ? 0 : (isPaid.get()) ? 3 : 6);
		result = prime * result + ((name == null) ? 0 : name.get().hashCode());
		result = prime * result + ((type == null) ? 0 : type.get().hashCode());
		result = prime * result + ((value == null) ? 0 : (int) (value.get() * 10));
		return result;
	}

	/**
	 * An object is equal to this object if the memory position of the objects is equal or the class, type, and date (month and year only) are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		//		// EQUAL it is the same object in memory
		if (this == obj)
			return true;

		//		// UNEQUAL if obj is null
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

		if (this.getName().equals(other.getName())
			&& this.getDate().equals(other.getDate())
			&& this.getType().equals(other.getType())
			&& this.getAbsoluteValue() == other.getAbsoluteValue()) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String toString() {
		return "Transaction [name=" + getName() + ", income=" + isIncome() + ", date=" + getDate() + ", type=" + getType() + ", value=" + getValue() + ", transID=" + getTransactionID() + ", hashCode="
			+ hashCode() + ", hasChanged=" + hasChanged() + "]";
	}

}
