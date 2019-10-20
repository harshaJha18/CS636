package cs636.music.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static cs636.music.dao.DBConstants.*;

/**
 * Database connection and initialization.
 * Implemented singleton on this class.
 * 
 */
public class DbDAO {
	   
	private Connection connection;
		
	/**
	 *  Use to connect to databases through JDBC drivers
	 *  @param dbUrl connection string
	 *  @param usr  user name
	 *  @param passwd password
	 *  @throws  SQLException
	 */
	public DbDAO(String dbUrl, String usr, String passwd) throws SQLException {
		if (dbUrl == null) {
			System.out.println("DbDAO constructor: replacing null dbUrl with "+H2_URL);
			dbUrl = H2_URL; // default to H2, an embedded DB
			usr = "test";
			passwd = "";
		} else {
			System.out.println("DbDAO constructor called with "+dbUrl);
		}
		
		// Although simple JDBC apps no longer need Class.forName lookups, we are
		// using a jar with all three drivers in it and this confuses the
		// automatic driver location by JDBC. So we do it the old way.
		String dbDriverName;
		if (dbUrl.contains("mysql"))
			dbDriverName = MYSQL_DRIVER;
		else if (dbUrl.contains("oracle"))
			dbDriverName = ORACLE_DRIVER;
		else if (dbUrl.contains("h2"))
			dbDriverName = H2_DRIVER;
		else throw new SQLException("Unknown DB URL "+dbUrl);		
	
		try {
			Class.forName(dbDriverName);   // as done with JDBC before v4
		} catch (Exception e) {
			System.out.println("can't find driver " + dbDriverName);
		}
		connection = DriverManager.getConnection(dbUrl, usr, passwd);
	}

	// package protection: no need to call this from service layer
	Connection getConnection() {
		return connection;
	}
	
	/**
	 *  Terminate the built connection
	 *  @throws  SQLException
	 */
	public void close() throws SQLException {
		connection.close();  // this object opened it, so it gets to close it
	}
	
	/**
	*  bring DB back to original state
	*  @throws  SQLException
	**/
	public void initializeDb() throws SQLException {
		clearTable(DOWNLOAD_TABLE);
		clearTable(LINEITEM_TABLE);
		clearTable(INVOICE_TABLE);
		clearTable(USER_TABLE);
		clearTable(SYS_TABLE);
		initSysTable();		
	}

	/**
	*  Delete all records from the given table
	*  @param tableName table name from which to delete records
	*  @throws  SQLException
	**/
	private void clearTable(String tableName) throws SQLException {
		Statement stmt = connection.createStatement();
		try {
			stmt.execute("delete from " + tableName);
		} finally {
			stmt.close();
		}
	}
	
	/**
	*  Set all the index number used in other tables back to 1
	*  @throws  SQLException
	**/
	private void initSysTable() throws SQLException {
		Statement stmt = connection.createStatement();
		try {
			stmt.execute("insert into " + SYS_TABLE + " values (1,1,1,1)");
		} finally {
			stmt.close();
		}
	}

}
