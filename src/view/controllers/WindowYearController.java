package view.controllers;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.domain.Month;

public class WindowYearController {

	private static int selectedYear = LocalDate.now().getYear();

	@FXML
	private TextField year;
	@FXML
	private Button monthJan;
	@FXML
	private Button monthFeb;
	@FXML
	private Button monthMar;
	@FXML
	private Button monthMay;
	@FXML
	private Button monthApr;
	@FXML
	private Button monthJun;
	@FXML
	private Button monthJul;
	@FXML
	private Button monthAug;
	@FXML
	private Button monthSep;
	@FXML
	private Button monthOct;
	@FXML
	private Button monthNov;
	@FXML
	private Button monthDec;

	public void initialise(int year) {
		monthJan.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				
			}
		});
		refresh(year);
	}
	
	public void refresh(int year) {
		Button[] buttons = new Button[] { monthJan, monthFeb, monthMar, monthApr, monthMay, monthJun, monthJul, monthAug, monthSep, monthOct, monthNov, monthDec };
		this.year.setText(String.valueOf(year));
		List<Month> months = Controller.getDAO().pullMonthsForYear(year);
		HashMap<Integer, Month> monthMap = new HashMap<Integer, Month>(12);
		for (Month m : months) {
			int thisMonth = m.getDate().getMonthValue() - 1;
			monthMap.put(thisMonth, m);
		}
		for (int i = 0; i < buttons.length; i++) {
			if (monthMap.containsKey(i)) {
				buttons[i].setStyle("-fx-background-color: blue;");
			} else {
				buttons[i].setStyle(null);
				buttons[i].setDisable(true);
			}
		}
	}
	
	public void changeYear() {
		int year = Integer.valueOf(this.year.getText());
		initialise(year);
	}
	
	public void openMonth() {
		
	}
}
