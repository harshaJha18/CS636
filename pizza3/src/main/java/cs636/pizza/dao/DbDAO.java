package cs636.pizza.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

import static cs636.pizza.dao.DBConstants.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DbDAO {
	@Autowired
	private DataSource dataSource;  
		
	// bring DB back to original state
	public void initializeDb() throws SQLException {
		Connection connection = dataSource.getConnection();
		System.out.println("Datasource type = " +dataSource.getClass());

		// drop tables with FK cols before the tables they refer to
		clearTable(connection, PIZZA_TOPPING_TABLE);
		clearTable(connection, ORDER_TABLE);
		clearTable(connection, PIZZA_SIZE_TABLE);
		clearTable(connection, MENU_TOPPING_TABLE);
		clearTable(connection, MENU_SIZE_TABLE);
		clearTable(connection, SYS_TABLE);
		initSysTable(connection);
		connection.close();
	}

	private void clearTable(Connection connection, String tableName) throws SQLException {
		Statement stmt = connection.createStatement();
		try {
			stmt.execute("delete from " + tableName);
		} finally {
			stmt.close();
		}
	}
	
	private void initSysTable(Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		try {
			stmt.execute("insert into " + SYS_TABLE + " values (1,1,1,1,1,1)");
		} finally {
			stmt.close();
		}
	}
	
	// Note package scope: no need to call this from service layer
	void advanceId(Connection connection, String columnName) throws SQLException
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
	int findNextId(Connection connection, String columnName) throws SQLException
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
		advanceId(connection, columnName);
		return nextId;
	}
	
	public Connection startTransaction() throws SQLException {
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		return connection;
	}

	public void commitTransaction(Connection connection) throws SQLException {
		// the commit call can throw, and then the caller needs to rollback
		// System.out.println("commitTxn");
		connection.commit();
		connection.close();
	}

	public void rollbackTransaction(Connection connection) throws SQLException {
		// System.out.println("rollbackTxn");
		connection.rollback();
		connection.close();
	}
	
	// If the caller has already seen an exception, it probably
	// doesn't want to handle a failing rollback, so it can use this.
	// Then the caller should issue its own exception based on the
	// original exception.
	public void rollbackAfterException(Connection connection) {
		try {
			// System.out.println("rollbackAfterException");
			rollbackTransaction(connection);
		} catch (Exception e) {	
			// discard secondary exception--probably server can't be reached
		}
		try {
			connection.close();
		} catch (Exception e) {
			// discard secondary exception--probably server can't be reached
		}
	}

}
