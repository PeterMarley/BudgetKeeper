package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import model.db.Constants;
import model.domain.Month;
import model.domain.Transaction;
import model.domain.Utility;

public class ImportExport {
	
	private static final String FILE_EXTENSION = ".bke";
	private static final String DELIMITER = ",";
	private static final String MARKER_MONTH = ":M:-";
	private static final String MARKER_TRANSACTION = ":T:-";

	public String export(List<Month> months) {
		Utility.validateNotEmpty(months);
		LocalDateTime dt = LocalDateTime.now();
		String filename = String.format("BudgetKeeperExport_%s_%02d:%02d:%02d%s",
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
					sbt.append(t.isIncome());
					sbt.append(DELIMITER);
					sbt.append(t.getDate().format(Constants.FORMAT_YYYYMMDD));
					sbt.append(DELIMITER);
					sbt.append(t.getType().name());
					sbt.append(DELIMITER);
					sbt.append(t.getAbsoluteValue());
					sbt.append(DELIMITER);
					sbt.append(t.getName());
					sbt.append(DELIMITER);
					sbt.append(t.isPaid());
					sbt.append(DELIMITER);
					sbt.append(t.getTransactionID());
					writer.write(sbt.toString());
					writer.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filename;
	}
	
	public static List<Month> importData(String filename) {
		return null;
	}
}
