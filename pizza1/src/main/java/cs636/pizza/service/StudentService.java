package cs636.pizza.service;

import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.PizzaOrderDAO;
import cs636.pizza.domain.PizzaOrder;
import cs636.pizza.domain.PizzaSize;
import cs636.pizza.domain.PizzaTopping;
import cs636.pizza.domain.MenuSize;
import cs636.pizza.domain.MenuTopping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.sql.SQLException;

/** 
 * 
 * This class captures the business logic for student interactions. 
 *
 * Only one instance of this class is instantiated, i.e.,
 * its object is a singleton object, and this singleton receives 
 * references to the singleton DAO objects at its own creation time.

 */

public class StudentService {
	
	private PizzaOrderDAO pizzaOrderDAO;
	private MenuDAO menuDAO;
	private AdminDAO adminDAO;

	public StudentService(PizzaOrderDAO pizzaDAO, MenuDAO mDAO, AdminDAO admDAO) {
		pizzaOrderDAO = pizzaDAO;
		menuDAO = mDAO;
		adminDAO = admDAO;
	}

	public Set<String> getSizeNames()throws ServiceException
	{
		Set<MenuSize> sizes = null;
		try {
			sizes = menuDAO.findMenuSizes();
		} catch(SQLException e) {
			throw new ServiceException("Can't access pizza sizes in db: ", e);
		}
		Set<String> sizeNames = new TreeSet<String>();
		for (MenuSize s: sizes)
			sizeNames.add(s.getSizeName());
	   return sizeNames;	
	}

	public Set<String> getToppingNames()throws ServiceException
	{
		Set<MenuTopping> toppings = null;
		try {
			toppings = menuDAO.findMenuToppings();
		} catch(SQLException e) {
			throw new ServiceException("Can't access toppings in db: ", e);
		}
		Set<String> toppingNames = new TreeSet<String>();
		for (MenuTopping t: toppings)
			toppingNames.add(t.getToppingName());

		return toppingNames;
	}
		
	public void makeOrder(int roomNum, String sizeName, Set<String> toppingNames) 
													throws ServiceException {
		try {
			// TODO: ck args (sizeName should be in menu_sizes, etc.)
			// Create dependent objects first
			PizzaSize size = new PizzaSize(sizeName);
			Set<PizzaTopping> toppings = new TreeSet<PizzaTopping>();
			for (String toppingName: toppingNames) {
				toppings.add(new PizzaTopping(toppingName));
			}
			PizzaOrder order = new PizzaOrder(-1, roomNum, size, toppings,
					 adminDAO.findCurrentDay(), PizzaOrder.PREPARING);
			pizzaOrderDAO.insertOrder(order);
		} catch (SQLException e) {
			throw new ServiceException("Order can not be inserted" + e, e);
		}
	}
	
	// return all orders for this room, for today, in order by id
	public List<PizzaOrderData> getOrderStatus(int roomNumber) throws ServiceException {
		List<PizzaOrder> pizzaOrders = null;
		List<PizzaOrderData> pizzaOrders1 = new ArrayList<PizzaOrderData>();
		try {
			pizzaOrders = pizzaOrderDAO.findOrdersByRoom(roomNumber, adminDAO.findCurrentDay());	
			for (PizzaOrder order: pizzaOrders) {
				pizzaOrders1.add(new PizzaOrderData(order));
			}	
		} catch (SQLException e) {
			throw new ServiceException("Error in getting status" + e, e);
		}
		return pizzaOrders1;
	}
	
	// receive pizza orders that are ready, for a certain room
	// i.e., student acknowledges getting the baked pizza(s) for this room
	// each such order is marked "finished"
	public void receiveOrders(int roomNumber)throws ServiceException {
		List<PizzaOrder> pizzaOrders = null;
		try {
			pizzaOrders = pizzaOrderDAO.findOrdersByRoom(roomNumber, adminDAO.findCurrentDay());
			for (PizzaOrder order: pizzaOrders) {
				if (order.getStatus()== PizzaOrder.BAKED) {
					order.receive();	// mark this pizza "finished"
					// in DB too--
					pizzaOrderDAO.updateOrderStatus(order.getId(), PizzaOrder.FINISHED);
				}
			}
		
		} catch (SQLException e) {
			throw new ServiceException("Error in getting status" + e, e);
		}
	}
}
