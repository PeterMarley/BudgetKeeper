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

public class ImportExport {

	private static final String FILE_EXTENSION = ".bke";
	private static final String DELIMITER = ",";
	private static final String MARKER_MONTH = ":M:-";
	private static final String MARKER_TRANSACTION = ":T:-";

	public String exportData(List<Month> months) {
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
					sbt.append(t.isIncome()); 									//0
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
					sbt.append(DELIMITER);
					sbt.append(t.getTransactionID());							//6
					writer.write(sbt.toString());
					writer.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filename;
	}

	public List<Month> importData(File bkeFile) throws FileNotFoundException, IllegalArgumentException {
		FileChooser fx = new FileChooser();
		fx.setInitialDirectory(new File("./"));
		fx.setSelectedExtensionFilter(new ExtensionFilter("BudgetKeeper Export", ".bke"));
		List<Month> monthsFromFile = new LinkedList<Month>();
		try (BufferedReader reader = new BufferedReader(new FileReader(bkeFile))) {
			String line = reader.readLine();
			int lineCounter = 1;
			while (line != null && !line.equals("")) {
				try {
					// construct Month
					int markerMonthEndIndex = MARKER_MONTH.length();
					LocalDate date = LocalDate.parse(line.substring(markerMonthEndIndex),Constants.FORMAT_YYYYMM);
					line = reader.readLine();
					lineCounter++;
					Month m = new Month(date);

					// if Month has no Transaction - continue
					if (line.startsWith(MARKER_MONTH)) {
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
							Transaction t = new Transaction(tTokens[4],
									(tTokens[5] == "1") ? true : false,
									LocalDate.parse(tTokens[1], Constants.FORMAT_YYYYMMDD),
									(tTokens[0] == "1") ? true : false,
									Type.valueOf(tTokens[2]),
									Double.valueOf(tTokens[3]),
									Transaction.NEW_ID);
							transactions.add(t);
							line = reader.readLine();
						} catch (IllegalArgumentException e) {
							System.err.println("Transaction from file parse failed on line " +lineCounter);
							System.err.println(line);
						}
					}
					if (transactions.size() > 0) {
						m.addTransactions(transactions);
					}
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
}
