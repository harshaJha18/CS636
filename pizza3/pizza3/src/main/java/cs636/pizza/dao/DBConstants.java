package cs636.pizza.dao;

/**
 * @author Saaid Baraty
 * 
 *         Database schema related constants.
 */

public interface DBConstants {
	public static final String ORDER_TABLE = "pizza_orders";
	public static final String SYS_TABLE = "pizza_sys_tab";
	public static final String MENU_TOPPING_TABLE = "menu_toppings";
	public static final String MENU_SIZE_TABLE = "menu_sizes";
	public static final String PIZZA_TOPPING_TABLE = "pizza_toppings";
	public static final String PIZZA_SIZE_TABLE = "pizza_sizes";
	public static final int MAX_TOPPINGS_STR_SIZE = 30;
	public static final int MAX_SIZE_STR_SIZE = 30;
}
