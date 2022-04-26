package log;

import java.time.LocalDate;

public class Logger implements Runnable{
	
	private final String FILEPATH;
	private Logger logger;
	
	private Logger() {
		FILEPATH = "./log/logs/";
	}
	
	@Override
	public void run() {
		logger = new Logger();
	}
	
	public static void log(Exception e) {
		
	}
	
	public static void log(String s) {
		
	}
	
	public static void log(String[] s) {
		
	}
	
}
