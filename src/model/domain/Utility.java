package model.domain;

import static model.domain.Utility.nullCheck;

import java.time.LocalDate;

/**
 * A class containing static utility methods.<br>
 * <br>
 * {@link Utility#nullCheck(Object) null check}.
 * <br>
 * {@link Utility#validate(double, Double, Double) validate double}.
 * <br>
 * {@link Utility#validate(int, Integer, Integer) validate int}.
 * <br>
 * {@link Utility#validate(String, Integer, Integer) validate String}.
 * 
 * 
 * 
 * @author Peter Marley
 *
 */
public class Utility {

	private static final int STACK_FRAME_METHOD_INDEX = 3;;

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
			throw new IllegalArgumentException(traceCall() + " method: Cannot validate a numeric value with no bounds.");
		} else if (min != null && max != null && min > max) {
			throw new IllegalArgumentException(traceCall() + " method: Impossible to validate, as minimum acceptable value (" + min + ") is greater than maximum acceptable value (" + max + ").");
		}

		if (min != null && toValidate < min) {
			throw new IllegalArgumentException(traceCall() + " method: numeric parameter cannot be less than " + min + ", but is " + toValidate + ".");
		} else if (max != null && toValidate > max) {
			throw new IllegalArgumentException(traceCall() + " method: numeric parameter cannot be greater than " + max + ", but is " + toValidate + ".");
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
		Double value = Double.valueOf(toValidate);
		return (int) validate(value, minD, maxD);
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
		toValidate = nullCheck(toValidate).trim();
		if (min != null && toValidate.length() < min) {
			throw new IllegalArgumentException(traceCall() + " method: String parameter cannot be less than " + min + " characters long, but was " + toValidate.length() + " characters long.");
		} else if (max != null && toValidate.length() > max) {
			throw new IllegalArgumentException(traceCall() + " method: String parameter cannot be greater than " + max + " characters long, but was " + toValidate.length() + " characters long.");
		}
		return toValidate.trim();
	}

	/**
	 * gets the calling method at index 3 in the current Threads stack trace. This method is to be use exclusively in this class, and may
	 * give inconsistent results if used otherwise.
	 * 
	 * @return the StackTraceElement {@code getMethodName()} at index {@value #STACK_FRAME_METHOD_INDEX} in the current threads stack trace. The method
	 *         that called the validator.
	 */
	private static String traceCall() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		return (stackTrace.length >= STACK_FRAME_METHOD_INDEX + 1) ? stackTrace[STACK_FRAME_METHOD_INDEX].getMethodName() : "";
	}

	/**
	 * Gets the number of months difference between two {@link LocalDate} objects.
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int monthDifferential(LocalDate d1, LocalDate d2) {
		nullCheck(d1);
		nullCheck(d2);
		int months, years;
		years = 12 * (d1.getYear() - d2.getYear());
		months = (d1.getMonthValue() - d2.getMonthValue());
		return years + months;
	}
}
