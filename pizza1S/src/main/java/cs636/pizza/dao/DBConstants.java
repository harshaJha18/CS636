package cs636.pizza.dao;

/**
 * @author Saaid Baraty
 * 
 *         Database schema related constants.
 */

public interface DBConstants {
	public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
	public static final String H2_DRIVER = "org.h2.Driver";
	public static final String HSQLDB_DRIVER = "org.hsqldb.jdbcDriver";
	public static final String HSQLDB_URL = "jdbc:hsqldb:hsql://localhost/";
	public static final String H2_URL = "jdbc:h2:~/test";

	public static final String ORDER_TABLE = "pizza_orders";
	public static final String SYS_TABLE = "pizza_sys_tab";
	public static final String MENU_TOPPING_TABLE = "menu_toppings";
	public static final String MENU_SIZE_TABLE = "menu_sizes";
	public static final String PIZZA_TOPPING_TABLE = "pizza_toppings";
	public static final String PIZZA_SIZE_TABLE = "pizza_sizes";
	public static final int MAX_TOPPINGS_STR_SIZE = 30;
	public static final int MAX_SIZE_STR_SIZE = 30;
}
