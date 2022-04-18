package model.db.admin;

/**
 * Enumeration for programmatically creating SQL statements on each table
 * 
 * @author Peter Marley
 * @StudentNumber 13404067
 * @Email pmarley03@qub.ac.uk
 * @GitHub https://github.com/PeterMarley
 *
 */
public class Enums {
	public enum Tables {
		TRANSACTION("transactions", "transactionID", new String[] { "monthID", "months" }, new String[] { "transactionID INTEGER NOT NULL", "monthID INTEGER NOT NULL", "income INTEGER", "date TEXT",
				"type TEXT",
				"value REAL" }),
		MONTH("months", "monthID", null, new String[] { "monthID INTEGER NOT NULL", "date TEXT" });

		private String tableName;
		private String primaryKey;
		private String[] foreignKeys;
		private String[] columns;

		private Tables(String tableName, String primaryKey, String[] foreignKey, String[] columns) {
			this.tableName = tableName;
			this.primaryKey = primaryKey;
			this.foreignKeys = foreignKey;
			this.columns = columns;
		}

		public String tableName() {
			return this.tableName;
		}

		public String primaryKey() {
			return this.primaryKey;
		}

		public String[] foreignKeys() {
			return this.foreignKeys;
		}

		public String[] columns() {
			return this.columns;
		}
	}

	public enum Files {
		DATABASE("jdbc:sqlite:database.db");

		private String location;

		private Files(String location) {
			this.location = location;
		}

		public String toString() {
			return this.location;
		}
	}
}
