package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.db.Constants;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Transaction.Type;
import model.domain.Utility;

/**
 * This class is part of the Budget Keeper program and allows the importing and exporting of data into {@value #FILE_EXTENSION} format text files.<br>
 * <br>
 * 
 * Example {@value #FILE_EXTENSION} file:
 * 
 * <pre>
 * 
 * </pre>
 * 
 * @author Peter Marley
 * @StudentNum 13404067
 * @email pmarley03@qub.ac.uk
 * @GitHub BigJeffTheChef
 *
 */
public class ImportExport {

	public static final String FILE_EXTENSION = ".bke";
	private static final String DELIMITER = ",";
	private static final String MARKER_MONTH = ":M:-";
	private static final String MARKER_TRANSACTION = ":T:-";

	/**
	 * Each {@link model.domain.Month Month} object field is written to a line prefixed with {@value #MARKER_MONTH}.<br>
	 * Each {@link model.domain.Transaction Transaction} object belonging to a Month is written directly after that month, and is prefixed with
	 * {@value #MARKER_TRANSACTION}.<br>
	 * Fields for all objects are delimited with {@value #DELIMITER}.<br>
	 * @param months
	 * @return the filename of the exported file.
	 * @throws IllegalArgumentException if months argument is empty (size is 0).
	 */
	public String exportData(Collection<Month> months) throws IllegalArgumentException {
		Utility.validateNotEmpty(months);
		LocalDateTime dt = LocalDateTime.now();
		String filename = String.format("BudgetKeeperExport_%s_%02d-%02d-%02d%s",
				dt.format(Constants.FORMAT_YYYYMMDD).replace('/', '-'),
				dt.getHour(),
				dt.getMinute(),
				dt.getSecond(),
				FILE_EXTENSION);
		File file = new File(filename);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			file.createNewFile();
			for (Month m : months) {
				StringBuilder sbm = new StringBuilder();
				sbm.append(MARKER_MONTH);
				sbm.append(m.getDate().format(Constants.FORMAT_YYYYMM));
				writer.write(sbm.toString());
				writer.newLine();
				for (Transaction t : m.getTransactions()) {
					StringBuilder sbt = new StringBuilder();
					sbt.append(MARKER_TRANSACTION);
					sbt.append(t.isIncome()); 									//0 (indices)
					sbt.append(DELIMITER);
					sbt.append(t.getDate().format(Constants.FORMAT_YYYYMMDD));	//1
					sbt.append(DELIMITER);
					sbt.append(t.getType().name());								//2
					sbt.append(DELIMITER);
					sbt.append(t.getAbsoluteValue());							//3
					sbt.append(DELIMITER);
					sbt.append(t.getName());									//4
					sbt.append(DELIMITER);
					sbt.append(t.isPaid());										//5
					writer.write(sbt.toString());
					writer.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filename;
	}

	/**
	 * Import a List of {@link model.domain.Month Month} objects from a specified {@value #FILE_EXTENSION} file.<br>
	 * If the import fails an empty list is returned
	 * @param bkeFile
	 * @return {@code List<Month>}
	 */
	public List<Month> importData(File bkeFile) throws FileNotFoundException {
		List<Month> monthsFromFile = new LinkedList<Month>();
		try (BufferedReader reader = new BufferedReader(new FileReader(bkeFile))) {
			String line = reader.readLine();
			int lineCounter = 1;
			while (line != null && !line.equals("")) {
				try {
					// construct Month
					LocalDate date = LocalDate.parse(line.substring(MARKER_MONTH.length()), Constants.FORMAT_YYYYMM);
					line = reader.readLine();
					lineCounter++;
					Month m = new Month(date);

					// if next line (a Month) has no Transactions - add month to list and continue
					if (line != null && line.startsWith(MARKER_MONTH)) {
						monthsFromFile.add(m);
						continue;
					}

					// construct transactions
					SortedSet<Transaction> transactions = new TreeSet<Transaction>();
					while (line != null && line.startsWith(MARKER_TRANSACTION)) {
						line = line.substring(MARKER_TRANSACTION.length());
						lineCounter++;
						String[] tTokens = line.split(DELIMITER);
						try {
							transactions.add(parseTransaction(tTokens));
						} catch (IllegalArgumentException e) {
							System.err.println("Transaction from file parse failed on line " + lineCounter);
							System.err.println(line);
						}
						line = reader.readLine();
					}

					// add Transactions to Month
					if (transactions.size() > 0) {
						m.addTransactions(transactions);
					}

					// add Month to List
					monthsFromFile.add(m);

				} catch (DateTimeParseException dateErroneousEx) {
					System.out.println("Date (" + line + ") could not be parsed to a LocalDate object");
				}
				lineCounter++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return monthsFromFile;
	}

	private Transaction parseTransaction(String[] tTokens) throws IllegalArgumentException {
		return new Transaction(tTokens[4],
				Boolean.valueOf(tTokens[5]),
				LocalDate.parse(tTokens[1], Constants.FORMAT_YYYYMMDD),
				Boolean.valueOf(tTokens[0]),
				Type.valueOf(tTokens[2]),
				Double.valueOf(tTokens[3]),
				Transaction.NEW_ID);
	}
}
