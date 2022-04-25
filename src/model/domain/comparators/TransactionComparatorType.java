package model.domain.comparators;

import static model.domain.Utility.nullCheck;

import java.util.Comparator;

import model.domain.Transaction;

public class TransactionComparatorType implements Comparator<Transaction>{

	private Sort sort;
	
	/**
	 * Create a Comparator for the {@link Transaction} class. Imposing a natural ordering by the {@code Type} double field, in ascending or descending
	 * order depending on {@code Sort} parameter.
	 * 
	 * @param type ASCENDING or DESCENDING
	 * @throws IllegalArgumentException if {@code type} parameter is {@code null}.
	 */
	public TransactionComparatorType(Sort type) throws IllegalArgumentException {
		this.sort = nullCheck(type);
	}

	/**
	 * Create a Comparator for the {@link Transaction} class. Imposing a natural ascending order by the {@code Type} double field.
	 */
	public TransactionComparatorType() {
		this(Sort.ASCENDING);
	}
	
	@Override
	public int compare(Transaction o1, Transaction o2) {
		nullCheck(o1);
		nullCheck(o2);
		double compare = (sort == Sort.ASCENDING) ? o1.getType().compareTo(o2.getType()): o2.getType().compareTo(o1.getType());
		if (compare < 0) {
			return -1;
		} else if (compare > 0) {
			return 1;
		} else {
			return 0;
		}
	}

}
