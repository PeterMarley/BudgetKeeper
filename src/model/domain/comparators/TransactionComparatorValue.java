package model.domain.comparators;

import static model.domain.Utility.nullCheck;

import java.util.Comparator;

import model.domain.Transaction;

public class TransactionComparatorValue implements Comparator<Transaction> {

	private enum Sort {
		ASCENDING, DESCENDING;
	}

	private Sort sort;

	/**
	 * Create a Comparator for the {@link Transaction} class. Imposing a natural ordering by the {@code value} double field, in ascending or descending
	 * order depending on {@code Sort} parameter.
	 * 
	 * @param type ASCENDING or DESCENDING
	 * @throws IllegalArgumentException if {@code type} parameter is {@code null}.
	 */
	public TransactionComparatorValue(Sort type) throws IllegalArgumentException {
		this.sort = nullCheck(type);
	}

	/**
	 * Create a Comparator for the {@link Transaction} class. Imposing a natural ascending order by the {@code value} double field.
	 */
	public TransactionComparatorValue() {
		this(Sort.ASCENDING);
	}

	@Override
	public int compare(Transaction o1, Transaction o2) throws IllegalArgumentException {
		nullCheck(o1);
		nullCheck(o2);
		double compare = (sort == Sort.ASCENDING) ? o1.getValue() - o2.getValue() : o2.getValue() - o1.getValue();
		if (compare < 0) {
			return -1;
		} else if (compare > 0) {
			return 1;
		} else {
			return 0;
		}
	}

}
