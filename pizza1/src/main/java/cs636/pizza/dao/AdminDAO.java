package cs636.pizza.dao;

/**
 *
 * Database access class for admin related tasks
 */
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection; 

// Use static import to simplify use of constants--
import static cs636.pizza.dao.DBConstants.*;

public class AdminDAO {
	
	private DbDAO dbDAO;
    private Connection connection;
    
	public AdminDAO(DbDAO db) throws SQLException {
		dbDAO = db;
		connection = dbDAO.getConnection();
	}

	public void advanceDay() throws SQLException
	{
		Statement stmt = connection.createStatement();
		try {
			stmt.executeUpdate("update " + SYS_TABLE
					+ " set current_day = current_day + 1 ");
		} finally {
			stmt.close();
		}
	}
	
	public int findCurrentDay() throws SQLException
	{
		Statement stmt = connection.createStatement();
		int date;
		try {
			ResultSet set = stmt.executeQuery("select current_day from " + SYS_TABLE);
			set.next();
			date = set.getInt(1);
		} finally {
			stmt.close();
		}
		return date;
	}
	
}
