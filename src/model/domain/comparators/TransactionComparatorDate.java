package model.domain.comparators;

import static model.domain.Utility.nullCheck;

import java.util.Comparator;

import model.domain.Transaction;

public class TransactionComparatorDate implements Comparator<Transaction>{

	private Sort sort;

	/**
	 * Create a Comparator for the {@link Transaction} class. Imposing a natural ordering by the {@code Date} double field, in ascending or descending
	 * order depending on {@code Sort} parameter.
	 * 
	 * @param type ASCENDING or DESCENDING
	 * @throws IllegalArgumentException if {@code type} parameter is {@code null}.
	 */
	public TransactionComparatorDate(Sort type) throws IllegalArgumentException {
		this.sort = nullCheck(type);
	}

	/**
	 * Create a Comparator for the {@link Transaction} class. Imposing a natural ascending order by the {@code Date} double field.
	 */
	public TransactionComparatorDate() {
		this(Sort.ASCENDING);
	}

	@Override
	public int compare(Transaction o1, Transaction o2) throws IllegalArgumentException {
		nullCheck(o1);
		nullCheck(o2);
		double compare = (sort == Sort.ASCENDING) ? o1.getDate().compareTo(o2.getDate()) : o2.getDate().compareTo(o1.getDate());
		if (compare < 0) {
			return -1;
		} else if (compare > 0) {
			return 1;
		} else {
			return 0;
		}
	}
	
	
}
