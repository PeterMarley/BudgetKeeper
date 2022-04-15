package model;

import java.time.LocalDate;
import static model.Validators.*;
/**
 * An object class representing a monetary transaction. Parameter validations are provided by {@link model.Validators} class.
 * @author Peter Marley
 *
 */
public class Transaction {

	public enum TransType {
		CASH,
		DIRECT_DEBIT,
		STANDING_ORDER,
		BANK_TRANSFER;
	}

	private Boolean income;
	private LocalDate date;
	private TransType type;
	private double value;

	/**
	 * Create a Transaction object
	 * 
	 * @param date     the date of this transction
	 * @param isIncome true = income, false = outgoing
	 * @param type
	 * @param value    the value of this transaction
	 */

	public Transaction(LocalDate date, boolean isIncome, TransType type, double value) {
		this.setDate(date);
		this.setIncome(isIncome);
	}

	//------------------------------\
	//	Setters						|
	//------------------------------/

	/**
	 * @param income the income to set
	 */
	public void setIncome(Boolean income) {
		this.income = income;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = nullCheck(date);
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TransType type) {
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
	public TransType getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

}
