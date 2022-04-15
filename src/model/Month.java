package model;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import static model.Validators.*;

public class Month {
	private int month;
	private List<Transaction> transactions;
	
	public Month(LocalDate date) {
		this.setDate(date);
		this.transactions = new LinkedList<Transaction>();
	}

	private void setDate(LocalDate date) {
		this.month = nullCheck(date).getMonthValue();
	}
}
