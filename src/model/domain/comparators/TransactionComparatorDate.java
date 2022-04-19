package model.domain.comparators;

import java.util.Comparator;

import model.domain.Transaction;

public class TransactionComparatorDate implements Comparator<Transaction>{

	@Override
	public int compare(Transaction o1, Transaction o2) {
		return o1.getDate().compareTo(o2.getDate());
	}
	
}
