package log;

public class Log implements Runnable {

	private final String FILEPATH;
	private final String FILENAME;
	private Log logger;

	private Log() {
		FILEPATH = "./log/logs/";
		FILENAME = "log";
	}

	@Override
	public void run() {
		logger = new Log();
	}

	public static void log(Exception e) {

	}

	public static void log(String s) {
		System.out.println("logging " + s);
	}

	public static void log(String[] s) {

	}

}