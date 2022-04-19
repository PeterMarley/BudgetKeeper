package model.domain.comparators;

import java.util.Comparator;

import model.domain.Transaction;

public class TransactionComparatorType implements Comparator<Transaction>{

	@Override
	public int compare(Transaction o1, Transaction o2) {
		return o1.getType().compareTo(o2.getType());
	}

}
