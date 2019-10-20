package cs636.pizza.dao;

/**
 *
 * Data access class for pizza order objects, including their sizes and toppings
 */

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.TreeSet;
import java.util.Set;

import cs636.pizza.domain.MenuSize;
import cs636.pizza.domain.MenuTopping;
//use static import to simplify use of constants--
import static cs636.pizza.dao.DBConstants.*;

public class MenuDAO {
	
    private Connection connection;
    DbDAO dbDAO;  // for common DB methods
    
	public MenuDAO(DbDAO db) throws SQLException {
		dbDAO = db;
		connection = db.getConnection();
	}

	// find size by name
	public MenuSize findMenuSize(String name) throws SQLException 
	{
		MenuSize size = null;

		Statement stmt = connection.createStatement();
		try {
			ResultSet set = stmt.executeQuery("select * from " + MENU_SIZE_TABLE + 
					" where size_name = '" + name + "'");
			if (set.next())
			   size = new MenuSize(set.getInt("id"), set.getString("size_name"));
		} finally {
			stmt.close();
		}
		return size;		
	}
	// find all active sizes
	public Set<MenuSize> findMenuSizes() throws SQLException 
	{
		Set<MenuSize> sizes = new TreeSet<MenuSize>();

		Statement stmt = connection.createStatement();
		try {
			ResultSet set = stmt.executeQuery("select * from " + MENU_SIZE_TABLE);
			while (set.next())
				sizes.add(new MenuSize(set.getInt("id"), set.getString("size_name")));
		} finally {
			stmt.close();
		}
		return sizes;		
	}

	// find topping by name
	public MenuTopping findMenuTopping(String name) throws SQLException 
	{
		MenuTopping topping = null;

		Statement stmt = connection.createStatement();
		try {
			ResultSet set = stmt.executeQuery("select * from " + MENU_TOPPING_TABLE +
					" where topping_name = '" + name + "'");
			if (set.next())
				topping = new MenuTopping(set.getInt("id"), set.getString("topping_name"));
		} finally {
			stmt.close();
		}
		return topping;		
	}
	// find all active toppings
	public Set<MenuTopping> findMenuToppings() throws SQLException {
		Set<MenuTopping> toppings = new TreeSet<MenuTopping>();

		Statement stmt = connection.createStatement();
		try {
			ResultSet set = stmt.executeQuery("select * from " + MENU_TOPPING_TABLE);
			while (set.next())
				toppings.add(new MenuTopping(set.getInt("id"), set.getString("topping_name")));
		} finally {
			stmt.close();
		}
		return toppings;
	}

	// add a topping
	public void createMenuTopping(String toppingName) throws SQLException {

		Statement stmt = connection.createStatement();
		try {
			// this topping_name not there, create a new row--
			int newID = dbDAO.findNextId("next_menu_topping_id");
			stmt.executeUpdate(
					"insert into " + MENU_TOPPING_TABLE + " values (" + newID + ", '" + toppingName + "')");

		} finally {
			stmt.close();
		}
	}

	// add a size
	public void createMenuSize(String sizeName) throws SQLException {
		Statement stmt = connection.createStatement();
		try {
			// this size_name not there, create a new row--
			int newID = dbDAO.findNextId("next_pizza_size_id");
			stmt.executeUpdate("insert into " + MENU_SIZE_TABLE + " values (" + newID + ", '" + sizeName + "')");
		} finally {
			stmt.close();
		}
	}
	
	// delete a topping from active duty, but keep it around so that
	// old orders can still show their past toppings
	public void deleteMenuTopping(String topping) throws SQLException
	{	Statement stmt = connection.createStatement();
		try {
			int rowsUpdated = stmt.executeUpdate("delete from " + MENU_TOPPING_TABLE + 
					 " where topping_name = '" + topping + "'");
			if (rowsUpdated != 1)
				throw new SQLException("deletion of non-existent menu topping " + topping);
		} finally {
			stmt.close();
		}
	}
	
	// delete a size from active duty, but keep it around so that
	// old orders can still show their past size
	public void deleteMenuSize(String size) throws SQLException
	{
		Statement stmt = connection.createStatement();
		try {
			int rowsUpdated = stmt.executeUpdate("delete from " + MENU_SIZE_TABLE + 
				" where size_name = '" + size + "'" );
			if (rowsUpdated != 1)
				throw new SQLException("deletion of non-existent Menu size " + size);
		} finally {
			stmt.close();
		}
	}
	
}
