package model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;

class MonthTest {

	Month m1, m2, m3, m4, m5Now, m6Compare1, m7Compare2, m8Compare3, m9CompareSame, m10CompareSame, m11Mar2012;

	LocalDate d1, d2, d3, d4, d5Now, d6Compare1, d7Compare2, d8Compare3, d9CompareSame, d10CompareSame, d11Mar2012, d12Mar2012, d13Mar2012;

	Transaction t1, t2, t3, t4, t5, t6;
	Type typeCash, typeDirectDebit, typeStandingOrder, typeBankTransfer;

	double valueGood1Low1, valueGood2Low2, valueGood3, valueGood4, valueGood5, valueGood6;

	String nameGood1Low, nameGood2, nameGood3, nameGood4, nameGood5, nameGood6;

	@BeforeEach
	void setUp() throws Exception {

		// Months

		d1 = LocalDate.of(2010, 03, 06);
		d2 = LocalDate.of(2012, 04, 15);
		d3 = LocalDate.of(2020, 06, 20);
		d4 = LocalDate.of(2022, 11, 29);
		d5Now = LocalDate.now();
		d6Compare1 = LocalDate.of(2010, 01, 10);
		d7Compare2 = LocalDate.of(2010, 05, 15);
		d8Compare3 = LocalDate.of(2020, 01, 01);
		d9CompareSame = LocalDate.of(2020, 01, 15);
		d10CompareSame = LocalDate.of(2020, 01, 15);

		d11Mar2012 = LocalDate.of(2012, 3, 15);
		d12Mar2012 = LocalDate.of(2012, 3, 29);
		d13Mar2012 = LocalDate.of(2012, 3, 18);

		m1 = new Month(d1);
		m2 = new Month(d2);
		m3 = new Month(d3);
		m4 = new Month(d4);
		m5Now = new Month(d5Now);
		m6Compare1 = new Month(d6Compare1);
		m7Compare2 = new Month(d7Compare2);
		m8Compare3 = new Month(d8Compare3);
		m9CompareSame = new Month(d9CompareSame);
		m10CompareSame = new Month(d10CompareSame);

		m11Mar2012 = new Month(d11Mar2012);

		// Transactions

		typeCash = Type.CASH;
		typeDirectDebit = Type.DIRECT_DEBIT;
		typeStandingOrder = Type.STANDING_ORDER;
		typeBankTransfer = Type.BANK_TRANSFER;

		valueGood1Low1 = 0;
		valueGood2Low2 = -0.0;
		valueGood3 = 29.64;
		valueGood4 = 150.69;
		valueGood5 = 2000.29;
		valueGood6 = 59666.1769;

		nameGood1Low = "a";
		nameGood2 = "ab";
		nameGood3 = "abc";
		nameGood4 = "abcd";
		nameGood5 = "abcde";
		nameGood6 = "x".repeat(150);

		t1 = new Transaction(nameGood1Low, true, d11Mar2012, true, typeCash, valueGood1Low1);
		t2 = new Transaction(nameGood2, false, d12Mar2012, true, typeDirectDebit, valueGood3);
		t3 = new Transaction(nameGood3, true, d13Mar2012, true, typeStandingOrder, valueGood4);

		t4 = new Transaction(nameGood4, false, d4, false, typeCash, valueGood4);
		t5 = new Transaction(nameGood5, true, d5Now, false, typeDirectDebit, valueGood5);
		t6 = new Transaction(nameGood6, false, d6Compare1, false, typeBankTransfer, valueGood6);
	}

	@Test
	void testEquals() {
		assertEquals(m1, new Month(d1));
		assertEquals(m2, new Month(d2));
		assertEquals(m3, new Month(d3));
		assertEquals(m4, new Month(d4));
		assertEquals(m5Now, new Month(d5Now));
		assertEquals(m6Compare1, new Month(d6Compare1));
		assertEquals(m7Compare2, new Month(d7Compare2));

		assertTrue(m9CompareSame.equals(m10CompareSame));
		assertTrue(m10CompareSame.equals(m9CompareSame));

		assertTrue(m6Compare1.equals(m6Compare1));
		assertTrue(m7Compare2.equals(m7Compare2));
		assertTrue(m8Compare3.equals(m8Compare3));
		assertFalse(m6Compare1.equals(m7Compare2));
		assertFalse(m7Compare2.equals(m8Compare3));
		assertFalse(m8Compare3.equals(m6Compare1));

		assertFalse(m8Compare3.equals(null));
	}

	@Test
	void test_compareTo() {
		// compareTo returning month differential
		assertTrue(m6Compare1.compareTo(m6Compare1) == 0);
		assertTrue(m6Compare1.compareTo(m7Compare2) == -4);
		assertTrue(m6Compare1.compareTo(m8Compare3) == -120);

		assertTrue(m7Compare2.compareTo(m6Compare1) == 4);
		assertTrue(m8Compare3.compareTo(m6Compare1) == 120);

		assertTrue(m7Compare2.compareTo(m7Compare2) == 0);
		assertTrue(m7Compare2.compareTo(m8Compare3) == -116);

		assertTrue(m8Compare3.compareTo(m7Compare2) == 116);

		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
			m8Compare3.compareTo(null);
		});
		assertEquals("compareTo method: Parameter cannot be null.", e.getMessage());
		// equals

	}

	@Test
	void test_constructor_validArgs() {
		assertNotNull(m1.getDate());
		assertEquals(d1, m1.getDate());
		assertNotNull(m1.getTransactions());
		assertEquals(0, m1.getNumberOfTransactions());
		assertEquals(m1.getTransactions().size(), m1.getNumberOfTransactions());

	}

	@Test
	void test_addTransaction_validArgs() {
		assertNotNull(m11Mar2012.getTransactions());
		assertEquals(0, m11Mar2012.getNumberOfTransactions());
		m11Mar2012.addTransaction(t1);
		m11Mar2012.addTransaction(t2);
		m11Mar2012.addTransaction(t3);
		assertEquals(3, m11Mar2012.getNumberOfTransactions());
	}

	@Test
	void test_addTransaction_invalidArgs() {
		IllegalArgumentException e;

		// add existing transaction
		m11Mar2012.addTransaction(t1);
		e = assertThrows(IllegalArgumentException.class, () -> {
			m11Mar2012.addTransaction(t1);
		});
		assertEquals("This transaction already exists in this month's data.", e.getMessage());

		m11Mar2012.addTransaction(t2);
		e = assertThrows(IllegalArgumentException.class, () -> {
			m11Mar2012.addTransaction(t2);
		});
		assertEquals("This transaction already exists in this month's data.", e.getMessage());

		// add null transaction
		e = assertThrows(IllegalArgumentException.class, () -> {
			m11Mar2012.addTransaction(null);
		});
		assertEquals("addTransaction method: Parameter cannot be null.", e.getMessage());

		// add wrong month transaction
		e = assertThrows(IllegalArgumentException.class, () -> {
			m11Mar2012.addTransaction(t4);
		});
		assertEquals("This transaction is for another month.", e.getMessage());

		e = assertThrows(IllegalArgumentException.class, () -> {
			m11Mar2012.addTransaction(t5);
		});
		assertEquals("This transaction is for another month.", e.getMessage());

		e = assertThrows(IllegalArgumentException.class, () -> {
			m11Mar2012.addTransaction(t6);
		});
		assertEquals("This transaction is for another month.", e.getMessage());

	}

	@Test
	void test_removeTransaction() {
		assertNotNull(m11Mar2012.getTransactions());
		assertEquals(0, m11Mar2012.getNumberOfTransactions());
		m11Mar2012.addTransaction(t1);
		m11Mar2012.addTransaction(t2);
		m11Mar2012.addTransaction(t3);
		assertEquals(3, m11Mar2012.getNumberOfTransactions());

		// remove existing transactions
		m11Mar2012.removeTransaction(t1);
		assertEquals(2, m11Mar2012.getNumberOfTransactions());

		m11Mar2012.removeTransaction(t2);
		assertEquals(1, m11Mar2012.getNumberOfTransactions());

		m11Mar2012.removeTransaction(t3);
		assertEquals(0, m11Mar2012.getNumberOfTransactions());

		// remove transations not in month
		assertFalse(m11Mar2012.removeTransaction(t1));
		assertFalse(m11Mar2012.removeTransaction(t2));
		assertFalse(m11Mar2012.removeTransaction(t3));
		assertFalse(m11Mar2012.removeTransaction(t4));
		assertFalse(m11Mar2012.removeTransaction(t5));
		assertFalse(m11Mar2012.removeTransaction(t6));

		// remove null transaction
		IllegalArgumentException nullParamEx = assertThrows(IllegalArgumentException.class, () -> {
			m11Mar2012.removeTransaction(null);
		});
		assertEquals("removeTransaction method: Parameter cannot be null.", nullParamEx.getMessage());

	}

}
