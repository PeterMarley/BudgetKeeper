package model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.synth.SynthStyleFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidatorsTest {

	private double doubleLowerBound1, doubleLowerBound2, doubleLowerBound3, doubleLowerBound4;
	private double doubleMid1, doubleMid2, doubleMid3, doubleMid4;
	private double doubleUpperBound1, doubleUpperBound2, doubleUpperBound3, doubleUpperBound4;

	private int intLowerBound1, intLowerBound2, intLowerBound3, intLowerBound4;
	private int intMid1, intMid2, intMid3, intMid4;
	private int intUpperBound1, intUpperBound2, intUpperBound3, intUpperBound4;

	private int strLowerBound1, strLowerBound2, strLowerBound3, strLowerBound4;
	private int strMidBound1, strMidBound2, strMidBound3, strMidBound4;
	private int strHigherBound1, strHigherBound2, strHigherBound3, strHigherBound4;

	private String strLowLen1, strLowLen2, strLowLen3, strLowLen4;
	private String strMidLen1, strMidLen2, strMidLen3, strMidLen4;
	private String strHighLen1, strHighLen2, strHighLen3, strHighLen4;

	private Object notNull1;
	private String notNull2;
	private BigDecimal notNull3;
	private Integer notNull4;
	private Double notNull5;
	private List<String> notNull6;

	private static Validators validators;

	@BeforeAll
	static void beforeAll() {
		validators = new Validators(2);
	}

	@BeforeEach
	void beforeEach() {

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

		// str
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

		strLowLen1 = "a".repeat(strLowerBound1);
		strMidLen1 = "b".repeat(strMidBound1);
		strHighLen1 = "c".repeat(strHigherBound1);

		strLowLen2 = "d".repeat(strLowerBound2);
		strMidLen2 = "e".repeat(strMidBound2);
		strHighLen2 = "f".repeat(strHigherBound2);

		strLowLen3 = "g".repeat(strLowerBound3);
		strMidLen3 = "h".repeat(strMidBound3);
		strHighLen3 = "i".repeat(strHigherBound3);

		strLowLen4 = "j".repeat(strLowerBound4);
		strMidLen4 = "k".repeat(strMidBound4);
		strHighLen4 = "l".repeat(strHigherBound4);

	}

	@Test
	void testNullCheck() {
		assertNotNull(Validators.nullCheck(notNull1));
		assertNotNull(Validators.nullCheck(notNull2));
		assertNotNull(Validators.nullCheck(notNull3));
		assertNotNull(Validators.nullCheck(notNull4));
		assertNotNull(Validators.nullCheck(notNull5));
		assertNotNull(Validators.nullCheck(notNull6));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
			Validators.nullCheck(null);
		});
		//System.out.println(e.getMessage());
		//assertTrue(e.getMessage().endsWith("nullCheck method: Parameter cannot be null."));
		assertEquals("nullCheck method: Parameter cannot be null.", e.getMessage());
	}

	@Test
	void testValidateDouble_validArgs() {
		// upper and lower bounds specified
		assertEquals(Validators.validate(doubleLowerBound1, doubleLowerBound1, doubleUpperBound1), doubleLowerBound1);
		assertEquals(Validators.validate(doubleMid1, doubleLowerBound1, doubleUpperBound1), doubleMid1);
		assertEquals(Validators.validate(doubleUpperBound1, doubleLowerBound1, doubleUpperBound1), doubleUpperBound1);

		assertEquals(Validators.validate(doubleLowerBound2, doubleLowerBound2, doubleUpperBound2), doubleLowerBound2);
		assertEquals(Validators.validate(doubleMid1, doubleLowerBound2, doubleUpperBound2), doubleMid1);
		assertEquals(Validators.validate(doubleUpperBound2, doubleLowerBound2, doubleUpperBound2), doubleUpperBound2);

		assertEquals(Validators.validate(doubleLowerBound3, doubleLowerBound3, doubleUpperBound3), doubleLowerBound3);
		assertEquals(Validators.validate(doubleMid3, doubleLowerBound3, doubleUpperBound3), doubleMid3);
		assertEquals(Validators.validate(doubleUpperBound3, doubleLowerBound3, doubleUpperBound3), doubleUpperBound3);

		assertEquals(Validators.validate(doubleLowerBound4, doubleLowerBound4, doubleUpperBound4), doubleLowerBound4);
		assertEquals(Validators.validate(doubleMid4, doubleLowerBound4, doubleUpperBound4), doubleMid4);
		assertEquals(Validators.validate(doubleUpperBound4, doubleLowerBound4, doubleUpperBound4), doubleUpperBound4);

		// lower bound only specified
		assertEquals(Validators.validate(doubleLowerBound1, doubleLowerBound1, null), doubleLowerBound1);
		assertEquals(Validators.validate(doubleMid1, doubleLowerBound1, null), doubleMid1);
		assertEquals(Validators.validate(doubleUpperBound1, doubleLowerBound1, null), doubleUpperBound1);

		assertEquals(Validators.validate(doubleLowerBound2, doubleLowerBound2, null), doubleLowerBound2);
		assertEquals(Validators.validate(doubleMid1, doubleLowerBound2, null), doubleMid1);
		assertEquals(Validators.validate(doubleUpperBound2, doubleLowerBound2, null), doubleUpperBound2);

		assertEquals(Validators.validate(doubleLowerBound3, doubleLowerBound3, null), doubleLowerBound3);
		assertEquals(Validators.validate(doubleMid3, doubleLowerBound3, null), doubleMid3);
		assertEquals(Validators.validate(doubleUpperBound3, doubleLowerBound3, null), doubleUpperBound3);

		assertEquals(Validators.validate(doubleLowerBound4, doubleLowerBound4, null), doubleLowerBound4);
		assertEquals(Validators.validate(doubleMid4, doubleLowerBound4, null), doubleMid4);
		assertEquals(Validators.validate(doubleUpperBound4, doubleLowerBound4, null), doubleUpperBound4);

		Double arbitraryD = 700000.0;
		assertEquals(Validators.validate(arbitraryD, arbitraryD - 1, null), arbitraryD);

		// upper bounds only specified
		assertEquals(Validators.validate(doubleLowerBound1, null, doubleUpperBound1), doubleLowerBound1);
		assertEquals(Validators.validate(doubleMid1, null, doubleUpperBound1), doubleMid1);
		assertEquals(Validators.validate(doubleUpperBound1, null, doubleUpperBound1), doubleUpperBound1);

		assertEquals(Validators.validate(doubleLowerBound2, null, doubleUpperBound2), doubleLowerBound2);
		assertEquals(Validators.validate(doubleMid1, null, doubleUpperBound2), doubleMid1);
		assertEquals(Validators.validate(doubleUpperBound2, null, doubleUpperBound2), doubleUpperBound2);

		assertEquals(Validators.validate(doubleLowerBound3, null, doubleUpperBound3), doubleLowerBound3);
		assertEquals(Validators.validate(doubleMid3, null, doubleUpperBound3), doubleMid3);
		assertEquals(Validators.validate(doubleUpperBound3, null, doubleUpperBound3), doubleUpperBound3);

		assertEquals(Validators.validate(doubleLowerBound4, null, doubleUpperBound4), doubleLowerBound4);
		assertEquals(Validators.validate(doubleMid4, null, doubleUpperBound4), doubleMid4);
		assertEquals(Validators.validate(doubleUpperBound4, null, doubleUpperBound4), doubleUpperBound4);

		assertEquals(Validators.validate(arbitraryD, null, arbitraryD + 1), arbitraryD);

	}

	@Test
	void testValidateDouble_invalidArgs() {
		IllegalArgumentException e;
		String expectedErrMsg;

		// value less than lower
		expectedErrMsg = "integer parameter cannot be less than " + doubleMid1 + " but was " + doubleLowerBound1 + ".";
		e = assertThrows(IllegalArgumentException.class, () -> {
			Validators.validate(doubleLowerBound1, doubleMid1, doubleUpperBound1);
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
		assertEquals(Validators.validate(intLowerBound1, intLowerBound1, intUpperBound1), intLowerBound1);
		assertEquals(Validators.validate(intMid1, intLowerBound1, intUpperBound1), intMid1);
		assertEquals(Validators.validate(intUpperBound1, intLowerBound1, intUpperBound1), intUpperBound1);

		assertEquals(Validators.validate(intLowerBound2, intLowerBound2, intUpperBound2), intLowerBound2);
		assertEquals(Validators.validate(intMid1, intLowerBound2, intUpperBound2), intMid1);
		assertEquals(Validators.validate(intUpperBound2, intLowerBound2, intUpperBound2), intUpperBound2);

		assertEquals(Validators.validate(intLowerBound3, intLowerBound3, intUpperBound3), intLowerBound3);
		assertEquals(Validators.validate(intMid3, intLowerBound3, intUpperBound3), intMid3);
		assertEquals(Validators.validate(intUpperBound3, intLowerBound3, intUpperBound3), intUpperBound3);

		assertEquals(Validators.validate(intLowerBound4, intLowerBound4, intUpperBound4), intLowerBound4);
		assertEquals(Validators.validate(intMid4, intLowerBound4, intUpperBound4), intMid4);
		assertEquals(Validators.validate(intUpperBound4, intLowerBound4, intUpperBound4), intUpperBound4);

		// lower bound only specified
		assertEquals(Validators.validate(intLowerBound1, intLowerBound1, null), intLowerBound1);
		assertEquals(Validators.validate(intMid1, intLowerBound1, null), intMid1);
		assertEquals(Validators.validate(intUpperBound1, intLowerBound1, null), intUpperBound1);

		assertEquals(Validators.validate(intLowerBound2, intLowerBound2, null), intLowerBound2);
		assertEquals(Validators.validate(intMid1, intLowerBound2, null), intMid1);
		assertEquals(Validators.validate(intUpperBound2, intLowerBound2, null), intUpperBound2);

		assertEquals(Validators.validate(intLowerBound3, intLowerBound3, null), intLowerBound3);
		assertEquals(Validators.validate(intMid3, intLowerBound3, null), intMid3);
		assertEquals(Validators.validate(intUpperBound3, intLowerBound3, null), intUpperBound3);

		assertEquals(Validators.validate(intLowerBound4, intLowerBound4, null), intLowerBound4);
		assertEquals(Validators.validate(intMid4, intLowerBound4, null), intMid4);
		assertEquals(Validators.validate(intUpperBound4, intLowerBound4, null), intUpperBound4);

		int arbitraryI = 700000;
		assertEquals(Validators.validate(arbitraryI, arbitraryI - 1, null), arbitraryI);

		// upper bounds only specified
		assertEquals(Validators.validate(intLowerBound1, null, intUpperBound1), intLowerBound1);
		assertEquals(Validators.validate(intMid1, null, intUpperBound1), intMid1);
		assertEquals(Validators.validate(intUpperBound1, null, intUpperBound1), intUpperBound1);

		assertEquals(Validators.validate(intLowerBound2, null, intUpperBound2), intLowerBound2);
		assertEquals(Validators.validate(intMid1, null, intUpperBound2), intMid1);
		assertEquals(Validators.validate(intUpperBound2, null, intUpperBound2), intUpperBound2);

		assertEquals(Validators.validate(intLowerBound3, null, intUpperBound3), intLowerBound3);
		assertEquals(Validators.validate(intMid3, null, intUpperBound3), intMid3);
		assertEquals(Validators.validate(intUpperBound3, null, intUpperBound3), intUpperBound3);

		assertEquals(Validators.validate(intLowerBound4, null, intUpperBound4), intLowerBound4);
		assertEquals(Validators.validate(intMid4, null, intUpperBound4), intMid4);
		assertEquals(Validators.validate(intUpperBound4, null, intUpperBound4), intUpperBound4);

		assertEquals(Validators.validate(arbitraryI, null, arbitraryI + 1), arbitraryI);

	}

	@Test
	void testValidateInteger_invalidArgs() {
		IllegalArgumentException e;

		e = assertThrows(IllegalArgumentException.class, () -> {
			Validators.validate(intLowerBound1, intMid1, intUpperBound1);
		});
		String expectedErrMsg = "integer parameter cannt be less than " + intMid1 + " but was " + intLowerBound1;
		System.out.println(expectedErrMsg);
		System.out.println(e.getMessage());
		assertTrue(e.getMessage().endsWith(expectedErrMsg));
	}

	@Test
	void testValidateStr_validArgs() {

	}

	@Test
	void testValidateStr_invalidArgs() {

	}
}
