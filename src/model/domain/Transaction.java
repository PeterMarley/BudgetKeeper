package model.domain;

import static model.domain.Utility.*;

import java.time.LocalDate;

/**
 * An object class representing a monetary transaction. Parameter validations are provided by {@link model.domain.Utility} class.<br>
 * Natural order is {@link #compareTo(Transaction) chronological}.
 * 
 * @author Peter Marley
 *
 */
public class Transaction implements Comparable<Transaction> {

	public enum Type {
		CASH,
		DIRECT_DEBIT,
		STANDING_ORDER,
		BANK_TRANSFER;
	}

	private boolean income;
	private LocalDate date;
	private Type type;
	private double value;

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

	public Transaction(LocalDate date, boolean isIncome, Type type, double value) {
		this.setDate(date);
		this.setType(type);
		this.setValue(value);
		this.income = isIncome;
	}

	//------------------------------\
	//	Setters						|
	//------------------------------/

	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = nullCheck(date);
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = nullCheck(type);
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = validate(value, 0.0, null);
	}

	//------------------------------\
	//	Getters						|
	//------------------------------/

	/**
	 * @return the income
	 */
	public Boolean getIncome() {
		return income;
	}

	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	//------------------------------\
	// Overrides & Interface Impl	|
	//------------------------------/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (income ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * An object is equal to this object if: <br>
	 * Evaluates the memory position of the objects, the class, the type, and all fields together to determine equality.
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
		} else if (date.getYear() != other.date.getYear() && date.getMonth() != other.date.getMonth())
			return false;
		// UNEQUAL if income fields are not equal
		if (income != other.income)
			return false;
		// UNEQUAL if type fields are not equal
		if (type != other.type)
			return false;
		// UNEQUAL if value fields are not equal
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
			return false;

		// OTHERWISE EQUAL
		return true;
	}

	/**
	 * A {@code Transaction} object's total ordering is chronologically by difference in months.
	 */
	@Override
	public int compareTo(Transaction t) {
		int diff = monthDifferential(this.date, t.getDate());
		if (diff == 0) { // if same month chronologically

			if (this.equals(t)) { // if object fields are equal
				return 0;
			} else {				// otherwise return value difference
				if (this.value - t.value < 0) {
					return -1;
				} else if (this.value - t.value > 0) {
					return 1;
				}
			}

		}
		return diff;
	}

	@Override
	public String toString() {
		return "Transaction [income=" + income + ", date=" + date + ", type=" + type + ", value=" + value + "]";
	}

}
