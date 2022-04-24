package model.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.synth.SynthStyleFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import model.domain.Utility;

class UtilityTest {

	private double doubleLowerBound1, doubleLowerBound2, doubleLowerBound3, doubleLowerBound4;
	private double doubleMid1, doubleMid2, doubleMid3, doubleMid4;
	private double doubleUpperBound1, doubleUpperBound2, doubleUpperBound3, doubleUpperBound4;

	private int intLowerBound1, intLowerBound2, intLowerBound3, intLowerBound4;
	private int intMid1, intMid2, intMid3, intMid4;
	private int intUpperBound1, intUpperBound2, intUpperBound3, intUpperBound4;

	private int strLowerBound1, strLowerBound2, strLowerBound3, strLowerBound4;
	private int strMidBound1, strMidBound2, strMidBound3, strMidBound4;
	private int strHigherBound1, strHigherBound2, strHigherBound3, strHigherBound4;

	private String strLenLow1, strLenLow2Whitespace, strLenLow3, strLenLow4;
	private String strLenMid1, strLenMid2Whitespace, strLenMid3, strLenMid4;
	private String strLenHigh1, strLenHigh2Whitespace, strLenHigh3, strLenHigh4;

	private Object notNull1;
	private String notNull2;
	private BigDecimal notNull3;
	private Integer notNull4;
	private Double notNull5;
	private List<String> notNull6;

	private LocalDate d1, d2, d3, d4, d5, d6;

	@BeforeEach
	void beforeEach() {

		// not null objects
		notNull1 = new Object();
		notNull2 = "not a null string";
		notNull3 = new BigDecimal(16);
		notNull4 = 25;
		notNull5 = 66.99;
		notNull6 = new ArrayList<String>(1);

		// double
		doubleLowerBound1 = 0;
		doubleMid1 = 6.678;
		doubleUpperBound1 = 15.0;

		doubleLowerBound2 = -100.7;
		doubleMid2 = 1.1;
		doubleUpperBound2 = 101.6;

		doubleLowerBound3 = 63.0;
		doubleMid3 = 75.8;
		doubleUpperBound3 = 99.9;

		doubleLowerBound4 = 150.1;
		doubleMid4 = 150.5;
		doubleUpperBound4 = 150.9;

		// int
		intLowerBound1 = 0;
		intMid1 = 6;
		intUpperBound1 = 15;

		intLowerBound2 = -100;
		intMid2 = 1;
		intUpperBound2 = 101;

		intLowerBound3 = 63;
		intMid3 = 75;
		intUpperBound3 = 99;

		intLowerBound4 = 150;
		intMid4 = 150;
		intUpperBound4 = 150;

		// String
		strLowerBound1 = 1;
		strMidBound1 = 2;
		strHigherBound1 = 3;

		strLowerBound2 = 10;
		strMidBound2 = 10;
		strHigherBound2 = 10;

		strLowerBound3 = 25;
		strMidBound3 = 50;
		strHigherBound3 = 75;

		strLowerBound4 = 67;
		strMidBound4 = 1000;
		strHigherBound4 = 2000;

		strLenLow1 = "a".repeat(strLowerBound1);
		strLenMid1 = "b".repeat(strMidBound1);
		strLenHigh1 = "c".repeat(strHigherBound1);

		strLenLow2Whitespace = "   " + "d".repeat(strLowerBound2);
		strLenMid2Whitespace = "e".repeat(strMidBound2) + "\t";
		strLenHigh2Whitespace = "\n" + "f".repeat(strHigherBound2);

		strLenLow3 = "g".repeat(strLowerBound3);
		strLenMid3 = "h".repeat(strMidBound3);
		strLenHigh3 = "i".repeat(strHigherBound3);

		strLenLow4 = "j".repeat(strLowerBound4);
		strLenMid4 = "k".repeat(strMidBound4);
		strLenHigh4 = "l".repeat(strHigherBound4);

		// LocalDate
		d1 = LocalDate.of(2015, 1, 1);
		d2 = LocalDate.of(2015, 2, 15);
		d3 = LocalDate.of(2015, 3, 25);
		d4 = LocalDate.of(2016, 1, 1);
		d5 = LocalDate.of(2016, 6, 1);
		d6 = LocalDate.of(2010, 12, 29);

	}

	@Test
	void testMonthDifferential() {

		assertEquals(-1, Utility.monthDifferential(d1, d2));
		assertEquals(-12, Utility.monthDifferential(d1, d4));

		assertEquals(1, Utility.monthDifferential(d2, d1));
		assertEquals(12, Utility.monthDifferential(d4, d1));

		assertEquals(0, Utility.monthDifferential(d1, d1));

		assertEquals(-17, Utility.monthDifferential(d1, d5));
		assertEquals(17, Utility.monthDifferential(d5, d1));

		assertEquals((4 * 12) + 1, Utility.monthDifferential(d1, d6));
	}

	@Test
	void testNullCheck() {
		assertNotNull(Utility.nullCheck(notNull1));
		assertNotNull(Utility.nullCheck(notNull2));
		assertNotNull(Utility.nullCheck(notNull3));
		assertNotNull(Utility.nullCheck(notNull4));
		assertNotNull(Utility.nullCheck(notNull5));
		assertNotNull(Utility.nullCheck(notNull6));
		assertNotNull(Utility.nullCheck(d4));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
			Utility.nullCheck(null);
		});
		//System.out.println(e.getMessage());
		//assertTrue(e.getMessage().endsWith("nullCheck method: Parameter cannot be null."));
		assertTrue(e.getMessage().endsWith("Parameter cannot be null."));
	}

	@Test
	void testValidateDouble_validArgs() {
		// upper and lower bounds specified
		assertEquals(Utility.validate(doubleLowerBound1, doubleLowerBound1, doubleUpperBound1), doubleLowerBound1);
		assertEquals(Utility.validate(doubleMid1, doubleLowerBound1, doubleUpperBound1), doubleMid1);
		assertEquals(Utility.validate(doubleUpperBound1, doubleLowerBound1, doubleUpperBound1), doubleUpperBound1);

		assertEquals(Utility.validate(doubleLowerBound2, doubleLowerBound2, doubleUpperBound2), doubleLowerBound2);
		assertEquals(Utility.validate(doubleMid1, doubleLowerBound2, doubleUpperBound2), doubleMid1);
		assertEquals(Utility.validate(doubleUpperBound2, doubleLowerBound2, doubleUpperBound2), doubleUpperBound2);

		assertEquals(Utility.validate(doubleLowerBound3, doubleLowerBound3, doubleUpperBound3), doubleLowerBound3);
		assertEquals(Utility.validate(doubleMid3, doubleLowerBound3, doubleUpperBound3), doubleMid3);
		assertEquals(Utility.validate(doubleUpperBound3, doubleLowerBound3, doubleUpperBound3), doubleUpperBound3);

		assertEquals(Utility.validate(doubleLowerBound4, doubleLowerBound4, doubleUpperBound4), doubleLowerBound4);
		assertEquals(Utility.validate(doubleMid4, doubleLowerBound4, doubleUpperBound4), doubleMid4);
		assertEquals(Utility.validate(doubleUpperBound4, doubleLowerBound4, doubleUpperBound4), doubleUpperBound4);

		// lower bound only specified
		assertEquals(Utility.validate(doubleLowerBound1, doubleLowerBound1, null), doubleLowerBound1);
		assertEquals(Utility.validate(doubleMid1, doubleLowerBound1, null), doubleMid1);
		assertEquals(Utility.validate(doubleUpperBound1, doubleLowerBound1, null), doubleUpperBound1);

		assertEquals(Utility.validate(doubleLowerBound2, doubleLowerBound2, null), doubleLowerBound2);
		assertEquals(Utility.validate(doubleMid1, doubleLowerBound2, null), doubleMid1);
		assertEquals(Utility.validate(doubleUpperBound2, doubleLowerBound2, null), doubleUpperBound2);

		assertEquals(Utility.validate(doubleLowerBound3, doubleLowerBound3, null), doubleLowerBound3);
		assertEquals(Utility.validate(doubleMid3, doubleLowerBound3, null), doubleMid3);
		assertEquals(Utility.validate(doubleUpperBound3, doubleLowerBound3, null), doubleUpperBound3);

		assertEquals(Utility.validate(doubleLowerBound4, doubleLowerBound4, null), doubleLowerBound4);
		assertEquals(Utility.validate(doubleMid4, doubleLowerBound4, null), doubleMid4);
		assertEquals(Utility.validate(doubleUpperBound4, doubleLowerBound4, null), doubleUpperBound4);

		Double arbitraryD = 700000.0;
		assertEquals(Utility.validate(arbitraryD, arbitraryD - 1, null), arbitraryD);

		// upper bounds only specified
		assertEquals(Utility.validate(doubleLowerBound1, null, doubleUpperBound1), doubleLowerBound1);
		assertEquals(Utility.validate(doubleMid1, null, doubleUpperBound1), doubleMid1);
		assertEquals(Utility.validate(doubleUpperBound1, null, doubleUpperBound1), doubleUpperBound1);

		assertEquals(Utility.validate(doubleLowerBound2, null, doubleUpperBound2), doubleLowerBound2);
		assertEquals(Utility.validate(doubleMid1, null, doubleUpperBound2), doubleMid1);
		assertEquals(Utility.validate(doubleUpperBound2, null, doubleUpperBound2), doubleUpperBound2);

		assertEquals(Utility.validate(doubleLowerBound3, null, doubleUpperBound3), doubleLowerBound3);
		assertEquals(Utility.validate(doubleMid3, null, doubleUpperBound3), doubleMid3);
		assertEquals(Utility.validate(doubleUpperBound3, null, doubleUpperBound3), doubleUpperBound3);

		assertEquals(Utility.validate(doubleLowerBound4, null, doubleUpperBound4), doubleLowerBound4);
		assertEquals(Utility.validate(doubleMid4, null, doubleUpperBound4), doubleMid4);
		assertEquals(Utility.validate(doubleUpperBound4, null, doubleUpperBound4), doubleUpperBound4);

		assertEquals(Utility.validate(arbitraryD, null, arbitraryD + 1), arbitraryD);

	}

	@Test
	void testValidateDouble_invalidArgs() {
		IllegalArgumentException e;
		String expectedErrMsg;

		// value less than lower
		expectedErrMsg = "numeric parameter cannot be less than " + doubleMid1 + ", but is " + doubleLowerBound1 + ".";
		e = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(doubleLowerBound1, doubleMid1, doubleUpperBound1);
		});
		//System.out.println(e.getMessage());
		//System.out.println(expectedErrMsg);
		assertTrue(e.getMessage().endsWith(expectedErrMsg));

		// value greater than upper

		// min greater than max

		// two null params
	}

	@Test
	void testValidateInteger_validArgs() {
		// upper and lower bounds specified
		assertEquals(Utility.validate(intLowerBound1, intLowerBound1, intUpperBound1), intLowerBound1);
		assertEquals(Utility.validate(intMid1, intLowerBound1, intUpperBound1), intMid1);
		assertEquals(Utility.validate(intUpperBound1, intLowerBound1, intUpperBound1), intUpperBound1);

		assertEquals(Utility.validate(intLowerBound2, intLowerBound2, intUpperBound2), intLowerBound2);
		assertEquals(Utility.validate(intMid1, intLowerBound2, intUpperBound2), intMid1);
		assertEquals(Utility.validate(intUpperBound2, intLowerBound2, intUpperBound2), intUpperBound2);

		assertEquals(Utility.validate(intLowerBound3, intLowerBound3, intUpperBound3), intLowerBound3);
		assertEquals(Utility.validate(intMid3, intLowerBound3, intUpperBound3), intMid3);
		assertEquals(Utility.validate(intUpperBound3, intLowerBound3, intUpperBound3), intUpperBound3);

		assertEquals(Utility.validate(intLowerBound4, intLowerBound4, intUpperBound4), intLowerBound4);
		assertEquals(Utility.validate(intMid4, intLowerBound4, intUpperBound4), intMid4);
		assertEquals(Utility.validate(intUpperBound4, intLowerBound4, intUpperBound4), intUpperBound4);

		// lower bound only specified
		assertEquals(Utility.validate(intLowerBound1, intLowerBound1, null), intLowerBound1);
		assertEquals(Utility.validate(intMid1, intLowerBound1, null), intMid1);
		assertEquals(Utility.validate(intUpperBound1, intLowerBound1, null), intUpperBound1);

		assertEquals(Utility.validate(intLowerBound2, intLowerBound2, null), intLowerBound2);
		assertEquals(Utility.validate(intMid1, intLowerBound2, null), intMid1);
		assertEquals(Utility.validate(intUpperBound2, intLowerBound2, null), intUpperBound2);

		assertEquals(Utility.validate(intLowerBound3, intLowerBound3, null), intLowerBound3);
		assertEquals(Utility.validate(intMid3, intLowerBound3, null), intMid3);
		assertEquals(Utility.validate(intUpperBound3, intLowerBound3, null), intUpperBound3);

		assertEquals(Utility.validate(intLowerBound4, intLowerBound4, null), intLowerBound4);
		assertEquals(Utility.validate(intMid4, intLowerBound4, null), intMid4);
		assertEquals(Utility.validate(intUpperBound4, intLowerBound4, null), intUpperBound4);

		int arbitraryI = 700000;
		assertEquals(Utility.validate(arbitraryI, arbitraryI - 1, null), arbitraryI);

		// upper bounds only specified
		assertEquals(Utility.validate(intLowerBound1, null, intUpperBound1), intLowerBound1);
		assertEquals(Utility.validate(intMid1, null, intUpperBound1), intMid1);
		assertEquals(Utility.validate(intUpperBound1, null, intUpperBound1), intUpperBound1);

		assertEquals(Utility.validate(intLowerBound2, null, intUpperBound2), intLowerBound2);
		assertEquals(Utility.validate(intMid1, null, intUpperBound2), intMid1);
		assertEquals(Utility.validate(intUpperBound2, null, intUpperBound2), intUpperBound2);

		assertEquals(Utility.validate(intLowerBound3, null, intUpperBound3), intLowerBound3);
		assertEquals(Utility.validate(intMid3, null, intUpperBound3), intMid3);
		assertEquals(Utility.validate(intUpperBound3, null, intUpperBound3), intUpperBound3);

		assertEquals(Utility.validate(intLowerBound4, null, intUpperBound4), intLowerBound4);
		assertEquals(Utility.validate(intMid4, null, intUpperBound4), intMid4);
		assertEquals(Utility.validate(intUpperBound4, null, intUpperBound4), intUpperBound4);

		assertEquals(Utility.validate(arbitraryI, null, arbitraryI + 1), arbitraryI);

	}

	@Test
	void testValidateInteger_invalidArgs() {
		IllegalArgumentException illArgEx;
		String exMsgTxtFormatLess, exMsgTxtFormatGreater, expectedErrMsg;
		String[] response;
		exMsgTxtFormatLess = " numeric parameter cannot be less than %d, but is %d.";
		exMsgTxtFormatGreater = " numeric parameter cannot be greater than %d, but is %d.";
		//
		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intLowerBound1 - 1, intLowerBound1, intLowerBound1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intLowerBound1, intLowerBound1 - 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intLowerBound1 + 1, intLowerBound1, intLowerBound1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatGreater, intLowerBound1, intLowerBound1 + 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intLowerBound1, intMid1, intUpperBound1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intMid1, intLowerBound1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intMid1 - 1, intMid1, intUpperBound1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intMid1, intMid1 - 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intUpperBound1 - 1, intUpperBound1, intUpperBound1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intUpperBound1, intUpperBound1 - 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intUpperBound1 + 1, intUpperBound1, intUpperBound1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatGreater, intUpperBound1, intUpperBound1 + 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);
		//======================================================================
		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intLowerBound2 - 1, intLowerBound2, intLowerBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intLowerBound2, intLowerBound2 - 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intLowerBound2 + 1, intLowerBound2, intLowerBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatGreater, intLowerBound2, intLowerBound2 + 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intLowerBound2, intMid2, intUpperBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intMid2, intLowerBound2);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intMid2 - 1, intMid2, intUpperBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intMid2, intMid2 - 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intUpperBound2 - 1, intUpperBound2, intUpperBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intUpperBound2, intUpperBound2 - 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intUpperBound2 + 1, intUpperBound2, intUpperBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatGreater, intUpperBound2, intUpperBound2 + 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);
		//======================================================================
		//======================================================================
		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intLowerBound2 - 1, intLowerBound2, intLowerBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intLowerBound2, intLowerBound2 - 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intLowerBound2 + 1, intLowerBound2, intLowerBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatGreater, intLowerBound2, intLowerBound2 + 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intLowerBound3, intMid3, intUpperBound3);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intMid3, intLowerBound3);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intMid3 - 1, intMid3, intUpperBound3);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intMid3, intMid3 - 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intUpperBound3 - 1, intUpperBound3, intUpperBound3);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, intUpperBound3, intUpperBound3 - 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(intUpperBound3 + 1, intUpperBound3, intUpperBound3);
		});
		expectedErrMsg = String.format(exMsgTxtFormatGreater, intUpperBound3, intUpperBound3 + 1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);
		//======================================================================
	}

	@Test
	void testValidateStr_validArgs() {
		assertEquals(strLenLow1, Utility.validate(strLenLow1, strLowerBound1, strHigherBound1));
		assertEquals(strLenMid1, Utility.validate(strLenMid1, strLowerBound1, strHigherBound1));
		assertEquals(strLenHigh1, Utility.validate(strLenHigh1, strLowerBound1, strHigherBound1));

		assertEquals(strLenLow2Whitespace.trim(), Utility.validate(strLenLow2Whitespace, strLowerBound2, strHigherBound2));
		assertEquals(strLenMid2Whitespace.trim(), Utility.validate(strLenMid2Whitespace, strLowerBound2, strHigherBound2));
		assertEquals(strLenHigh2Whitespace.trim(), Utility.validate(strLenHigh2Whitespace, strLowerBound2, strHigherBound2));

		assertEquals(strLenLow1, Utility.validate(strLenLow1, strLowerBound1, strHigherBound1));
		assertEquals(strLenMid1, Utility.validate(strLenMid1, strLowerBound1, strHigherBound1));
		assertEquals(strLenHigh1, Utility.validate(strLenHigh1, strLowerBound1, strHigherBound1));

		assertEquals(strLenLow1, Utility.validate(strLenLow1, strLowerBound1, strHigherBound1));
		assertEquals(strLenMid1, Utility.validate(strLenMid1, strLowerBound1, strHigherBound1));
		assertEquals(strLenHigh1, Utility.validate(strLenHigh1, strLowerBound1, strHigherBound1));
	}

	@Test
	void testValidateStr_invalidArgs() {
		IllegalArgumentException illArgEx;
		String exMsgTxtFormatLess, exMsgTxtFormatGreater, expectedErrMsg;
		String[] response;
		exMsgTxtFormatLess = " String parameter cannot be less than %d characters long, but was %d characters long.";
		exMsgTxtFormatGreater = " String parameter cannot be greater than %d characters long, but was %d characters long.";
		//
		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenLow1, strLowerBound1 + 1, strHigherBound1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strLowerBound1 + 1, strLowerBound1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenLow1, strMidBound1 + 1, strHigherBound1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strMidBound1 + 1, strLowerBound1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenMid1, strMidBound1 + 1, strHigherBound1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strMidBound1 + 1, strMidBound1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenHigh1, strHigherBound1 + 1, strHigherBound1 + 1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strHigherBound1 + 1, strHigherBound1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenHigh1, strHigherBound1 - 1, strHigherBound1 - 1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatGreater, strHigherBound1 - 1, strHigherBound1);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);
		//==============================================================================================
		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenLow2Whitespace.trim(), strLowerBound2 + 1, strHigherBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strLowerBound2 + 1, strLowerBound2);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenLow2Whitespace.trim(), strMidBound2 + 1, strHigherBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strMidBound2 + 1, strLowerBound2);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenMid2Whitespace.trim(), strMidBound2 + 1, strHigherBound2);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strMidBound2 + 1, strMidBound2);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenHigh2Whitespace.trim(), strHigherBound2 + 1, strHigherBound2 + 1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strHigherBound2 + 1, strHigherBound2);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenHigh2Whitespace.trim(), strHigherBound2 - 1, strHigherBound2 - 1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatGreater, strHigherBound2 - 1, strHigherBound2);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);
		//==============================================================================================
		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenLow3, strLowerBound3 + 1, strHigherBound3);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strLowerBound3 + 1, strLowerBound3);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenLow3, strMidBound3 + 1, strHigherBound3);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strMidBound3 + 1, strLowerBound3);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenMid3, strMidBound3 + 1, strHigherBound3);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strMidBound3 + 1, strMidBound3);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenHigh3, strHigherBound3 + 1, strHigherBound3 + 1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strHigherBound3 + 1, strHigherBound3);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenHigh3, strHigherBound3 - 1, strHigherBound3 - 1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatGreater, strHigherBound3 - 1, strHigherBound3);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);
		//==============================================================================================
		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenLow4, strLowerBound4 + 1, strHigherBound4);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strLowerBound4 + 1, strLowerBound4);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenLow4, strMidBound4 + 1, strHigherBound4);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strMidBound4 + 1, strLowerBound4);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenMid4, strMidBound4 + 1, strHigherBound4);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strMidBound4 + 1, strMidBound4);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenHigh4, strHigherBound4 + 1, strHigherBound4 + 1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatLess, strHigherBound4 + 1, strHigherBound4);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);

		illArgEx = assertThrows(IllegalArgumentException.class, () -> {
			Utility.validate(strLenHigh4, strHigherBound4 - 1, strHigherBound4 - 1);
		});
		expectedErrMsg = String.format(exMsgTxtFormatGreater, strHigherBound4 - 1, strHigherBound4);
		response = illArgEx.getMessage().split("method:");
		assertEquals(expectedErrMsg, response[1]);
		//==============================================================================================
	}
}
