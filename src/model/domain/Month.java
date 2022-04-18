package model.domain;

import static model.domain.Utility.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import model.domain.Transaction.Type;
/**
 * Represents a Month of Transactions for the BudgetKeeper program
 * @author Peter Marley
 * @StudentNumber 13404067
 * @Email pmarley03@qub.ac.uk
 * @GitHub https://github.com/PeterMarley
 *
 */
public class Month implements Comparable<Month> {

	// instance fields
	private LocalDate date;
	private SortedSet<Transaction> transactions;

	//------------------------------\
	//	Construction				|
	//------------------------------/

	public Month(LocalDate date) {
		nullCheck(date);
		this.setDate(date);
		this.setTransactions();
	}

	//------------------------------\
	//	Transactions				|
	//------------------------------/

	/**
	 * Add a transaction to this month. Transaction must have same year and month as this {@code Month}.
	 * 
	 * @param t
	 * @throws IllegalArgumentException if {@code t} is not the correct month and year, or is null, or is a duplicate of a transaction already in the
	 *                                  list.
	 */
	public void addTransaction(Transaction t) throws IllegalArgumentException {
		nullCheck(t);
		if (monthDifferential(this.getDate(), t.getDate()) == 0) {
			if (transactions.contains(t)) {
				throw new IllegalArgumentException("This transaction already exists in this month's data.");
			} else {
				transactions.add(t);
			}
		} else {
			throw new IllegalArgumentException("This transaction is for another month.");
		}
	}
	
	public void addTransactions(List<Transaction> transactions) {
		for (Transaction t : transactions) {
			addTransaction(t);
		}
	}

	/**
	 * Remove any {@code Transaction} that is {@link Transaction#equals(Object) equal} to this {@code Transaction}, from {@code transactions} list.
	 * 
	 * @param t
	 * @throws IllegalArgumentException if {@code t} is null.
	 */
	public boolean removeTransaction(Transaction t) {
		nullCheck(t);
		return transactions.remove(t);

	}

	//------------------------------\
	//	Setters						|
	//------------------------------/

	/**
	 * Set the {@code date} using validation by {@link Utility#nullCheck(Object)}.
	 * 
	 * @param date
	 * @throws IllegalArgumentException if {@link Utility#nullCheck(Object)} fails to validate {@code date}.
	 */
	public void setDate(LocalDate date) {
		nullCheck(date);
		this.date = date;
	}

	/**
	 * Instantiate {@code transactions} field as a SortedSet/TreeSet.
	 */
	public void setTransactions() {
		this.transactions = new TreeSet<Transaction>();
	}

	//------------------------------\
	//	Getters						|
	//------------------------------/

	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return this.date;
	}

	/**
	 * Get the {@code transactions} field
	 * 
	 * @return a {@code SortedSet<Transaction>}.
	 */
	public SortedSet<Transaction> getTransactions() {
		return this.transactions;
	}
	
	public int getNumberOfTransactions() {
		return this.transactions.size();
	}

	//------------------------------\
	//	Overrides & Interface impl	|
	//------------------------------/

	/**
	 * Compare the distance chronologically between two months. Get the number of months that separates them (positive or negative).
	 */
	@Override
	public int compareTo(Month m) {
		nullCheck(m);
		return monthDifferential(this.getDate(), m.getDate());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// EQUAL if memory location same
		if (this == obj)
			return true;
		// UNEQUAL if obj is null
		if (obj == null)
			return false;
		// UNEQUAL if class is not the same
		if (getClass() != obj.getClass())
			return false;
		Month other = (Month) obj;
		// UNEQUAL if date is null, but obj.date is not
		if (date == null) {
			if (other.date != null)
				return false;
			// UNEQUAL if dates are not the same
		} else if (!date.equals(other.date))
			return false;
		// otherwise EQUAL
		return true;
	}

	public String toString() {
		return this.getDate().getMonth().toString() + " " + this.getDate().getYear();
	}

}
