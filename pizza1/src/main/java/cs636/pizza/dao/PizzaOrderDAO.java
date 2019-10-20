package cs636.pizza.dao;

/**
 *
 * Data access class for pizza order objects, including their sizes and toppings
 */

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Set;

import cs636.pizza.domain.PizzaOrder;
import cs636.pizza.domain.PizzaSize;
import cs636.pizza.domain.PizzaTopping;
//use static import to simplify use of constants--
import static cs636.pizza.dao.DBConstants.*;

public class PizzaOrderDAO {
	
    private Connection connection;
    DbDAO dbDAO;  // for common DB methods
	private MenuDAO menuDAO;
    
	public PizzaOrderDAO(DbDAO db, MenuDAO menuDb) throws SQLException {
		dbDAO = db;
		menuDAO = menuDb;
		connection = db.getConnection();
	}

	public void insertOrder(PizzaOrder order) throws SQLException {
  		// We don't want a FK from a pizza_sizes row to menu_sizes row
  		// because it would prevent pizza size deletion
  		// and similarly with pizza toppings
  		// but we can check for size name and topping name existence here explicitly--
  		String sizeName = order.getPizzaSize().getSizeName();
  		if (menuDAO.findMenuSize(sizeName) == null)
  			throw new SQLException("no such pizza size available");  // we are using SQLExceptions from DAO
  		for (PizzaTopping t: order.getPizzaToppings()) {
  			if (menuDAO.findMenuTopping(t.getToppingName())== null)
  				throw new SQLException("no such pizza topping available");
   		}
		int ordNo = dbDAO.findNextId("next_order_id");   // using SYS_TABLE col. next_order_id
		int sid = dbDAO.findNextId("next_pizza_size_id");
		order.setId(ordNo);
		Statement stmt = connection.createStatement();
		try {
			// first insert the size row, target of FK in orders table
			String sqlString = "insert into " + PIZZA_SIZE_TABLE + " values ("
					+ sid + " , '" + order.getPizzaSize().getSizeName() + "')";
			stmt.execute(sqlString);
			// second insert the order row, target of the FKs in pizza toppings table
			sqlString = "insert into " + ORDER_TABLE + " values ("
			+ ordNo + ", " + order.getRoomNumber() + ", "
			+ sid + ", " + order.getDay() + ", "
			+ order.getStatus() + ") ";
			stmt.execute(sqlString);
			// finally insert the pizza toppings
			Set<PizzaTopping> toppings = order.getPizzaToppings();
			for (PizzaTopping t: toppings) {
				int tid = dbDAO.findNextId("next_pizza_topping_id");
				sqlString = "insert into " + PIZZA_TOPPING_TABLE
				+ " values (" + order.getId() + ", " + tid + " , '" + t.getToppingName() 
				+ "') ";
				stmt.execute(sqlString);
			}
		} finally {
			stmt.close();
		}
	}
	
	// Get orders, including toppings for a certain day and room number
	// ordered by order id
	public List<PizzaOrder> findOrdersByRoom(int roomNumber, int day) throws SQLException
	{
		// Don't use "to" as a table alias, it's a reserved word
		// Use left outer join to preserve orders with no toppings in result
		String sqlString =
		"SELECT o.*, s.*, ot.* "
		+ "FROM " + ORDER_TABLE + " o LEFT OUTER JOIN " + PIZZA_TOPPING_TABLE + " ot " +
		" ON o.id = ot.order_id " +
		"JOIN " + PIZZA_SIZE_TABLE + " s ON o.size_id = s.id " + 
		"WHERE o.room_number = " + roomNumber + " and o.day = " + day +
		" ORDER BY o.id";

		Map<Integer, PizzaOrder> statusMap = new TreeMap<Integer, PizzaOrder>();
		Statement stmt = connection.createStatement();
		try {
			ResultSet table = stmt.executeQuery(sqlString);

			while (table.next()) {
				int ordNo = table.getInt("order_id");
				PizzaOrder order = null;
				String toppingName = table.getString("topping_name");
				PizzaTopping topping = new PizzaTopping(ordNo, ordNo, toppingName);
				if ((order = (PizzaOrder) statusMap.get(ordNo)) != null) {
					order.addPizzaTopping(topping);
				} else {
					Set<PizzaTopping> toppings = new TreeSet<PizzaTopping>();
					toppings.add(topping);
					PizzaSize size = new PizzaSize(table.getString("size_name"));
					int status = table.getInt("status");
					order = new PizzaOrder(ordNo, roomNumber, size,	toppings, day, status);
					statusMap.put(ordNo, order);
				}
			}
		} finally {
			stmt.close();
		}
		return new LinkedList<PizzaOrder>(statusMap.values()); // in id order											// anyway
	}
	

	// find first order with specified status, or null if no orders there
	public PizzaOrder findFirstOrder(int status) throws SQLException {
		PizzaOrder po = null;
		Statement stmt = connection.createStatement();
		try {
			ResultSet set = null;
			set = stmt.executeQuery("select * from " + ORDER_TABLE + " where status = " + status + " order by id");
			if (set.next()) {
				po = new PizzaOrder(set.getInt("id"), set.getInt("room_number"), null, null, set.getInt("day"),
						set.getInt("status"));
			}

		} finally {
			stmt.close();
		}
		return po;
	}

	public void updateOrderStatus(int ordNo, int newStatus) throws SQLException
	{
		Statement stmt = connection.createStatement();
		try {
			stmt.executeUpdate(" update " + ORDER_TABLE 
	    	  	        + " set status = " + newStatus +  
	                    " where id = " + ordNo );
		} finally {
			stmt.close();
		}
	}
	
	// get all orders, without toppings or sizes, between day1 and day2 (inclusive)
	// ordered by order id
	public List<PizzaOrder> findOrdersByDays(int day1, int day2) throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet report = stmt.executeQuery("select id, room_number, day, status from " + 
				ORDER_TABLE + " where day >= " + day1 + " and day <= " + day2 + 
				" order by id");
		List<PizzaOrder> reportSet = new LinkedList<PizzaOrder>();
		while(report.next()) {
			reportSet.add(new PizzaOrder( report.getInt("id"), report.getInt("room_number"),
					null, null, report.getInt("day"), report.getInt("status")  ) );
		}
		stmt.close();
		return reportSet;
	}
	
}
