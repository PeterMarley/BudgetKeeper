package controller;

import java.util.List;

import model.domain.Month;
import model.domain.Utility;

public class Exporter {
	private List<Month> months;
	
	public Exporter(List<Month> months) {
		this.months = Utility.nullCheck(months);
	}
	
	public static String export;
}
