package view;

import javafx.application.Application;
import javafx.stage.Stage;
import view.windows.WindowMonth;
import view.windows.WindowYear;

public class View extends Application {

	private WindowYear windowYear;
	private WindowMonth[] windowMonth;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		windowYear = new WindowYear();
		windowYear.show();
	}

}
