package model.domain.comparators;

import java.util.Comparator;

import model.domain.Month;
import model.domain.Utility;

public class MonthComparatorDate implements Comparator<Month> {
	private Sort sort;

	public MonthComparatorDate(Sort sort) {
		this.sort = Utility.nullCheck(sort);
	}

	public MonthComparatorDate() {
		this(Sort.ASCENDING);
	}

	@Override
	public int compare(Month o1, Month o2) {
		int val = (sort == Sort.ASCENDING) ? o1.getDate().compareTo(o2.getDate()) : o2.getDate().compareTo(o1.getDate());
		return val;
	}
}
