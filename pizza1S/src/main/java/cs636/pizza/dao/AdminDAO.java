package cs636.pizza.dao;
/**
 *
 * Database access class for admin related tasks
 */
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection; 

// Use static import to simplify use of constants--
import static cs636.pizza.dao.DBConstants.*;

@Repository
public class AdminDAO {
	
	@Autowired
	private DbDAO dbDAO;
    
	public AdminDAO(DbDAO db) {
	}

	public void advanceDay() throws SQLException
	{
		Connection connection = dbDAO.getConnection();
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
		Connection connection = dbDAO.getConnection();
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
