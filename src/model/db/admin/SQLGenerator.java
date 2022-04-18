package model.db.admin;

import static model.domain.Utility.nullCheck;

import model.db.admin.Enums.Tables;

/**
 * Generate SQL code statements.
 * 
 * @author Peter Marley
 * @StudentNumber 13404067
 * @Email pmarley03@qub.ac.uk
 * @GitHub https://github.com/PeterMarley
 *
 */
public class SQLGenerator {

	//--------------------------\
	//	Generate SQL code		|
	//--------------------------/

	/**
	 * Generate DROP TABLE SQL statements for a {@link Tables} enum.
	 * 
	 * @param table
	 * @return
	 */
	public static String dropTablesSQL(Tables table) throws IllegalArgumentException {
		nullCheck(table);
		StringBuilder b = new StringBuilder();
		b.append("DROP TABLE ");
		b.append(table.tableName());
		b.append(";");

		return b.toString();

	}

	/**
	 * Generate CREATE TABLE statements for a {@link Tables} enum.
	 * 
	 * @param table
	 * @return
	 */
	public static String createTableSQL(Tables table) throws IllegalArgumentException {
		nullCheck(table);
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE ");
		b.append(table.tableName());
		b.append(" (%n");
		for (int i = 0; i < table.columns().length; i++) {
			b.append("\t");
			b.append(table.columns()[i]);
			b.append(",%n");
		}
		b.append("\tPRIMARY KEY(");
		b.append(table.primaryKey());
		b.append(")");
		if (table.foreignKeys() != null) {
			b.append(",%n");
			b.append("\tFOREIGN KEY(");
			b.append(table.foreignKeys()[0]);
			b.append(") REFERENCES ");
			b.append(table.foreignKeys()[1]);
			b.append(" (");
			b.append(table.foreignKeys()[0]);
			b.append(")%n");
		} else {
			b.append("%n");
		}
		b.append(");");
		return String.format(b.toString());
	}
}
