package cs636.pizza.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static cs636.pizza.dao.DBConstants.*;

public class DbDAO {
	   
	private Connection connection;
	
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
	public void close() throws SQLException {
		connection.close();  // this object opened it, so it gets to close it
	}

	// bring DB back to original state
	public void initializeDb() throws SQLException {
		// drop tables with FK cols before the tables they refer to
		clearTable(PIZZA_TOPPING_TABLE);
		clearTable(ORDER_TABLE);
		clearTable(PIZZA_SIZE_TABLE);
		clearTable(MENU_TOPPING_TABLE);
		clearTable(MENU_SIZE_TABLE);
		clearTable(SYS_TABLE);
		initSysTable();		
	}

	private void clearTable(String tableName) throws SQLException {
		Statement stmt = connection.createStatement();
		try {
			stmt.execute("delete from " + tableName);
		} finally {
			stmt.close();
		}
	}
	
	private void initSysTable() throws SQLException {
		Statement stmt = connection.createStatement();
		try {
			stmt.execute("insert into " + SYS_TABLE + " values (1,1,1,1,1,1)");
		} finally {
			stmt.close();
		}
	}
	
	// Note package scope: no need to call this from service layer
	void advanceId(String columnName) throws SQLException
	{
		Statement stmt = connection.createStatement();
		try {
			stmt.executeUpdate(" update " + SYS_TABLE
					+ " set " + columnName + " = " + columnName + " + 1");
		} finally {
			stmt.close();
		}
	}

	// This shows one good way to produce new primary key value--use another
	// table's data to specify the next value
	// Here we use SYS_TABLE columns next_order_id, etc.
	int findNextId(String columnName) throws SQLException
	{
		int nextId;
		Statement stmt = connection.createStatement();
		try {
			ResultSet set = stmt.executeQuery(" select " + columnName + " from " + SYS_TABLE);
			set.next();
			nextId = set.getInt(columnName);
		} finally {
			stmt.close();
		}
		advanceId(columnName);
		return nextId;
	}
	

}
