package model;

/**
 * A class containing only static methods used to standardise parameter validation in the BudgetKeeper program.
 * 
 * @author Peter Marley
 *
 */
public class Validators {

	private static int stackFrameMethodIndex = 3;

	public Validators(int stackFrameMethodIndex) {
		this.stackFrameMethodIndex = validate(stackFrameMethodIndex, 0, null);
	}

	/**
	 * Checks any object for null. Intended for use in validating method parameters.
	 * 
	 * @param <T>    any reference type.
	 * @param object
	 * @return object
	 * @throws IllegalArgumentException if {@code object} is null.
	 */
	public static <T> T nullCheck(T object) {
		if (object == null) {
			throw new IllegalArgumentException(traceCall() + " method: Parameter cannot be null.");
		}
		return object;
	}

	/**
	 * Checks a double for acceptable ranges specified by {@code min} and {@code max}. These checks are carried out if the relevant Double parameter is
	 * NOT
	 * null, otherwise the checks are not carried out.
	 * 
	 * @param toValidate double
	 * @param min        minimum {@code toValidate} value (inclusive).
	 * @param max        maximum {@code toValidate} value (inclusive).
	 * @return {@code toValidate}
	 * @throws IllegalArgumentException if:<br>
	 *                                  - min is NOT null and toValidate < min.<br>
	 *                                  - max is NOT null and toValidate > max.<br>
	 */
	public static double validate(double toValidate, Double min, Double max) {
		// reject no bounds or if min > max
		if (min == null && max == null) {
			throw new IllegalArgumentException("Cannot validate a numeric value with no bounds.");
		} else if (min != null && max != null && min > max) {
			throw new IllegalArgumentException("Impossible to validate, as minimum acceptable value (" + min + ") is greater than maximum acceptable value (" + max + ").");
		}

		if (min != null && toValidate < min) {
			throw new IllegalArgumentException(traceCall() + " method: integer parameter cannot be less than " + min + " but was " + toValidate + ".");
		} else if (max != null && toValidate > max) {
			throw new IllegalArgumentException(traceCall() + " method: integer parameter cannot be greater than " + max + " but was " + toValidate + ".");
		}
		return toValidate;
	}

	/**
	 * Checks an int for acceptable ranges specified by {@code min} and {@code max}. These checks are carried out if the relevant Integer parameter is NOT
	 * null, otherwise the checks are not carried out.
	 * 
	 * @param toValidate int
	 * @param min        minimum {@code toValidate} value (inclusive).
	 * @param max        maximum {@code toValidate} value (inclusive).
	 * @return {@code toValidate}
	 * @throws IllegalArgumentException if:<br>
	 *                                  - min is NOT null and toValidate < min.<br>
	 *                                  - max is NOT null and toValidate > max.<br>
	 */
	public static int validate(int toValidate, Integer min, Integer max) {
		Double minD = (min == null) ? null : Double.valueOf(min);
		Double maxD = (max == null) ? null : Double.valueOf(max);
		return (int) validate(Double.valueOf(toValidate), minD, maxD);
	}

	/**
	 * Checks a String for acceptable length ranges specified by {@code min} and {@code max}.These checks are carried out if the relevant Integer
	 * parameter is NOT null, otherwise the checks are not carried out.
	 * 
	 * @param toValidate int
	 * @param min        minimum {@code toValidate} length (inclusive).
	 * @param max        maximum {@code toValidate} length (inclusive).
	 * @return {@code toValidate} trimmed of leading and trailing whitespace.
	 */
	public static String validate(String toValidate, Integer min, Integer max) {
		if (min != null && toValidate.length() < min) {
			throw new IllegalArgumentException(traceCall() + " method: integer parameter cannot be less than " + min + " but was " + toValidate + ".");
		} else if (max != null && toValidate.length() > max) {
			throw new IllegalArgumentException(traceCall() + " method: integer parameter cannot be greater than " + max + " but was " + toValidate + ".");
		}
		return toValidate.trim();
	}

	/**
	 * gets the calling method at index 3 in the current Threads stack trace. This method is to be use exclusively to validate setter parameters, and may
	 * give inconsisent results if used otherwise.
	 * 
	 * @return the StackTraceElement.toString() @ element
	 */
	private static String traceCall() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		return (stackTrace.length >= stackFrameMethodIndex + 1) ? stackTrace[stackFrameMethodIndex].getMethodName() : "";
	}
}
