package cs636.pizza.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.PizzaOrderDAO;
import cs636.pizza.domain.MenuSize;
import cs636.pizza.domain.MenuTopping;
import cs636.pizza.domain.PizzaOrder;
import cs636.pizza.domain.PizzaSize;
import cs636.pizza.domain.PizzaTopping;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * This class captures the business logic for student interactions. 
 *
 * Only one instance of this class is instantiated, i.e.,
 * its object is a singleton object, and this singleton receives 
 * references to the singleton DAO objects at its own creation time.
 */
@Service
public class StudentService {
	@Autowired
	private PizzaOrderDAO pizzaOrderDAO;
	@Autowired
	private MenuDAO menuDAO;
	@Autowired
	private AdminDAO adminDAO;
	@Autowired
	private DbDAO dbDAO;
	public StudentService() {}

	public Set<String> getSizeNames()throws ServiceException
	{
		Connection connection = null;
		Set<MenuSize> sizes = null;
		Set<String>sizeStrings = new TreeSet<String>();
		try {
			connection = dbDAO.startTransaction();
			sizes = menuDAO.findMenuSizes(connection);
			for (MenuSize s: sizes) {
				sizeStrings.add(s.getSizeName());
			}	
            dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection); 
			throw new ServiceException("Can't access pizza sizes in db: ", e);
		}
	   return sizeStrings;	
	}

	public Set<String> getToppingNames()throws ServiceException
	{
		Connection connection = null;
		Set<MenuTopping> toppings = null;
		Set<String>toppingStrings = new TreeSet<String>();
		try {
			connection = dbDAO.startTransaction();
			toppings = menuDAO.findMenuToppings(connection);
			for (MenuTopping t: toppings) {
				toppingStrings.add(t.getToppingName());
			}	
            dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection); 
			throw new ServiceException("Can't access toppings in db: ", e);
		}
		return toppingStrings;
	}

	// Transaction to make an order
	// Check the arguments to make sure the size and toppings are still on the menu, throw if not
	public void makeOrder(int roomNum, String size, Set<String> toppings) 
				throws ServiceException {
		Connection connection = null;
		try {	
			connection = dbDAO.startTransaction();
			// check out the size and toppings vs. the current menu offerings
			// create dependent objects here, not in PizzaOrder constructor
			// to avoid calling a constructor in a constructor
			if (menuDAO.findMenuSize(connection, size) == null) 
				throw new ServiceException(
						"Order cannot be placed because specified size " + size + " is unavailable");
			PizzaSize pizzaSize = new PizzaSize(size);
			Set<PizzaTopping> orderToppings = new HashSet<PizzaTopping>();
			for (String t:toppings) {
				if (menuDAO.findMenuTopping(connection, t) == null)
					throw new ServiceException(
							"Order cannot be placed because specified topping " + t + " is unavailable");
				else
					orderToppings.add(new PizzaTopping(t));  // it's OK, collect it up
			}
			PizzaOrder order = new PizzaOrder(roomNum, pizzaSize, orderToppings,
					adminDAO.findCurrentDay(connection), PizzaOrder.PREPARING);
			pizzaOrderDAO.insertOrder(connection, order);
			dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Order can not be placed ", e);
		}
	}

	// return all PREPARING or BAKED orders for this room
	public List<PizzaOrderData> getOrderStatus(int roomNumber)
			throws ServiceException {
		Connection connection = null;
		List<PizzaOrder> pizzaOrders = null;
		List<PizzaOrderData> pizzaOrders1 = new ArrayList<PizzaOrderData>();
		try {
			connection = dbDAO.startTransaction();																				
			pizzaOrders = pizzaOrderDAO.findOrdersByRoom(connection, roomNumber, adminDAO
					.findCurrentDay(connection));

			for (PizzaOrder order : pizzaOrders) {
				if (order.getStatus() == PizzaOrder.BAKED || order.getStatus() == PizzaOrder.PREPARING)
					pizzaOrders1.add(new PizzaOrderData(order));
			}
			dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Error in getting status ", e);
		}
		return pizzaOrders1;
	}
	
	// receive pizza orders that are ready, for a certain room
	// i.e., student acknowledges getting the baked pizza(s) for this room
	// each such order is marked "finished"
	public void receiveOrders(int roomNumber)throws ServiceException {
		Connection connection = null;
		List<PizzaOrder> pizzaOrders = null;
		try {
			connection = dbDAO.startTransaction();	
			pizzaOrders = pizzaOrderDAO.findOrdersByRoom(connection, roomNumber, adminDAO.findCurrentDay(connection));
			for (PizzaOrder order: pizzaOrders) {
				if (order.getStatus()== PizzaOrder.BAKED) {
					order.receive();	// mark this pizza "finished"
					pizzaOrderDAO.updateOrderStatus(connection, order.getId(), PizzaOrder.FINISHED);
				}
			}
			dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Error in getting status" + e, e);
		}
	}
					
}
