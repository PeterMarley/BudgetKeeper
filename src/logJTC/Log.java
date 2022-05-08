package logJTC;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Singleton Log JTC
 * 
 * @author Peter Marley
 * @StudentNumber 13404067
 * @Email pmarley03@qub.ac.uk
 * @GitHub https://github.com/PeterMarley
 *
 */
public class Log implements Runnable {

	private Log log;
	private Queue<Message> q;
	private final File FILE;
	private Thread main;

	boolean configured;

	private Log(Thread main, String filename) throws IllegalArgumentException {
		if (main == null || filename == null) {
			throw new IllegalArgumentException("null parameter [filename == " + filename + ", main == " + main + "]");
		}
		q = new LinkedList<Message>();
		this.main = main;
		this.FILE = new File(filename +
			LocalDateTime.now().format(new DateTimeFormatterBuilder()
				.appendPattern("yyyy-MM-dd.kk:mm.ss")
				.toFormatter()) +
			".csv");
		this.configured = true;
	}

	@Override
	public void run() throws IllegalStateException {
		try {
			if (!configured)
				throw new IllegalStateException("Log not configured.");
			while (main.isAlive()) {
				Thread.sleep(2500);
				writeQueue();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public synchronized boolean log(Message toLog) {
		return q.add(toLog);
	}

	private synchronized void writeQueue() {

	}

}
