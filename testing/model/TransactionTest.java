package model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.domain.Transaction;
import model.domain.Transaction.Type;

class TransactionTest {

	// test objects
	Transaction t1, t2, t3, t4, t5, t6, t7SameAsT1, t8SameAsT6;

	// valid data
	LocalDate d1, d2, d3, d4, d5, d6;
	double valueGood1Low1, valueGood1Low2, valueGood2Low2, valueGood3, valueGood4, valueGood5, valueGood6;
	Type typeCash, typeDirectDebit, typeStandingOrder, typeBankTransfer;

	// invalid data
	double valueBad1, valueBad2, valueBad3;

	@BeforeEach
	void setUp() throws Exception {
		d1 = LocalDate.of(2015, 1, 1);
		d2 = LocalDate.of(2015, 1, 10);
		d3 = LocalDate.of(2015, 2, 1);
		d4 = LocalDate.of(2015, 2, 15);
		d5 = LocalDate.of(2016, 4, 9);
		d6 = LocalDate.of(2015, 4, 1);

		valueGood1Low1 = 0;
		valueGood2Low2 = -0.0;
		valueGood3 = 29.64;
		valueGood4 = 150.69;
		valueGood5 = 2000.29;
		valueGood6 = 59666.1769;

		typeCash = Type.CASH;
		typeDirectDebit = Type.DIRECT_DEBIT;
		typeStandingOrder = Type.STANDING_ORDER;
		typeBankTransfer = Type.BANK_TRANSFER;

		t1 = new Transaction(d1, true, typeCash, valueGood1Low1);
		t2 = new Transaction(d2, true, typeDirectDebit, valueGood2Low2);
		t3 = new Transaction(d3, true, typeStandingOrder, valueGood3);

		t4 = new Transaction(d4, false, typeCash, valueGood4);
		t5 = new Transaction(d5, false, typeDirectDebit, valueGood5);
		t6 = new Transaction(d6, false, typeBankTransfer, valueGood6);

		t7SameAsT1 = new Transaction(t1.getDate(), t1.getIncome(), t1.getType(), t1.getValue());
		t8SameAsT6 = new Transaction(t6.getDate(), t6.getIncome(), t6.getType(), t6.getValue());

		valueBad1 = -0.01;
		valueBad2 = -0.6;
		valueBad3 = -1000000.59;
	}

	@Test
	void test_constructor_getters_setters_validArgs() {
		assertEquals(d1, t1.getDate());
		assertEquals(true, t1.getIncome());
		assertEquals(typeCash, t1.getType());
		assertEquals(valueGood1Low1, t1.getValue());

		assertEquals(d2, t2.getDate());
		assertEquals(true, t2.getIncome());
		assertEquals(typeDirectDebit, t2.getType());
		assertEquals(valueGood2Low2, t2.getValue());

		assertEquals(d3, t3.getDate());
		assertEquals(true, t3.getIncome());
		assertEquals(typeStandingOrder, t3.getType());
		assertEquals(valueGood3, t3.getValue());

		assertEquals(d4, t4.getDate());
		assertEquals(false, t4.getIncome());
		assertEquals(typeCash, t4.getType());
		assertEquals(valueGood4, t4.getValue());

		assertEquals(d5, t5.getDate());
		assertEquals(false, t5.getIncome());
		assertEquals(typeDirectDebit, t5.getType());
		assertEquals(valueGood5, t5.getValue());

		assertEquals(d6, t6.getDate());
		assertEquals(false, t6.getIncome());
		assertEquals(typeBankTransfer, t6.getType());
		assertEquals(valueGood6, t6.getValue());
	}

	@Test
	void test_constructor_setters_invalidArgs() {
		IllegalArgumentException e;

		// null date
		e = assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(null, true, typeCash, valueGood3);
		});
		assertEquals("setDate method: Parameter cannot be null.", e.getMessage());

		// null type
		e = assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(d3, true, null, valueGood3);
		});
		assertEquals("setType method: Parameter cannot be null.", e.getMessage());

		// negative values
		e = assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(d3, true, typeCash, valueBad1);
		});
		assertEquals("setValue method: numeric parameter cannot be less than 0.0, but is " + valueBad1 + ".", e.getMessage());

		e = assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(d3, true, typeCash, valueBad2);
		});
		assertEquals("setValue method: numeric parameter cannot be less than 0.0, but is " + valueBad2 + ".", e.getMessage());

		e = assertThrows(IllegalArgumentException.class, () -> {
			new Transaction(d3, true, typeCash, valueBad3);
		});
		assertEquals("setValue method: numeric parameter cannot be less than 0.0, but is " + valueBad3 + ".", e.getMessage());
	}

	@Test
	void test_equals() {
		assertTrue(t7SameAsT1.equals(t1));
		assertTrue(t1.equals(t7SameAsT1));
		assertTrue(t8SameAsT6.equals(t6));
		assertTrue(t6.equals(t8SameAsT6));

		assertFalse(t1.equals(t2));
		assertFalse(t1.equals(t6));
		assertFalse(t1.equals(t3));
	}

	@Test
	void test_compareTo() {
		assertEquals(0, t1.compareTo(t7SameAsT1));
		assertEquals(0, t7SameAsT1.compareTo(t1));

		assertEquals(0, t8SameAsT6.compareTo(t6));
		assertEquals(0, t6.compareTo(t8SameAsT6));

		assertEquals(0, t1.compareTo(t2));
	}
}
