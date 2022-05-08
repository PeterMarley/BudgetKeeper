package logJTC;

import java.util.Arrays;
import java.util.List;

public class Message {

	//----------------------------------\
	//									|
	//	Fields							|
	//									|
	//----------------------------------/

	private StackTraceElement[] stackTrace;
	private List<String> text;
	private Exception exception;

	//----------------------------------\
	//									|
	//	Construction					|
	//									|
	//----------------------------------/

	/**
	 * 
	 * @param stackTrace
	 * @param exception
	 * @param text
	 * @throws IllegalArgumentException is text is null.
	 */
	public Message(StackTraceElement[] stackTrace, Exception exception, List<String> text) throws IllegalArgumentException {

		if (text == null) {
			throw new IllegalArgumentException("text cannot be null");
		}

		this.stackTrace = stackTrace;
		this.exception = exception;
		this.text = text;
	}

	/**
	 * 
	 * @param stack
	 * @param text
	 * @throws IllegalArgumentException is text is null.
	 */
	public Message(StackTraceElement[] stack, List<String> text) {
		this(stack, null, text);
	}

	/**
	 * 
	 * @param text
	 * @throws IllegalArgumentException is text is null.
	 */
	public Message(List<String> text) {
		this(null, null, text);
	}

	//----------------------------------\
	//									|
	//	Setters							|
	//									|
	//----------------------------------/

	/**
	 * @param stackTrace the stackTrace to set
	 */
	public void setStackTrace(StackTraceElement[] stackTrace) {
		this.stackTrace = stackTrace;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(List<String> text) {
		this.text = text;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}

	//----------------------------------\
	//									|
	//	Getters							|
	//									|
	//----------------------------------/

	/**
	 * @return the stackTrace
	 */
	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	/**
	 * @return the text
	 */
	public List<String> getText() {
		return text;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	@Override
	public String toString() {
		return "Message [stackTrace=" + Arrays.toString(stackTrace) + ", text=" + text + ", exception=" + exception + "]";
	}

	@Override
	public int hashCode() {

		final int prime = 31;

		int result = 1;
		result = prime * result + ((exception == null) ? 0 : exception.getMessage().hashCode());
		result = prime * result + Arrays.hashCode(stackTrace);

		int num = 0;
		for (String line : text) {
			num += text.size();
			num++;
		}
		int textJumble = text.size() == 0 ? 0 : num + Integer.hashCode(Math.abs(text.get(0).hashCode())) - Math.abs(text.get(text.size() - 1).hashCode());
		result = prime * result + (text == null ? 0 : textJumble);
		return result;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (hashCode() == other.hashCode()) {
			return true;
		}
		return false;
	}

	//----------------------------------\
	//									|
	//	Overrides						|
	//									|
	//----------------------------------/

}
