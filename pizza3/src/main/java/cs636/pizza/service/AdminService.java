package cs636.pizza.service;

import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.PizzaOrderDAO;
import cs636.pizza.domain.PizzaOrder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class captures the business logic for admin-related interactions, the
 * actions that ordinary users (students) don't need.
 * 
 * Only one instance of this class is instantiated, i.e., its object is a
 * singleton object, and this singleton receives references to the singleton DAO
 * objects at its own creation time.
 */

// Note all the similar code for each service call. This can be eliminated by
// using container-managed transactions (not available in Tomcat, though).
// Note that each call catches DAO/JPA PersistenceExceptions and throws its own
// exception, after rolling back the transaction. The new exception, with
// a useful message, then gets caught in the presentation layer.
@Service
public class AdminService {
	@Autowired
	private DbDAO dbDAO;
	@Autowired
	private AdminDAO adminDAO;
	@Autowired
	private MenuDAO menuDAO;
	@Autowired
	private PizzaOrderDAO pizzaOrderDAO;

	public void initializeDb() throws ServiceException {
		Connection connection = null;
		try {
			connection = dbDAO.startTransaction();
			dbDAO.initializeDb();
			dbDAO.commitTransaction(connection);
		} catch (Exception e) { // any exception
			// the following doesn't itself throw, but it handles the case that
			// rollback throws, discarding that exception object
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Can't initialize DB: (probably need to load DB)", e);
		}
	}

	public void addTopping(String name) throws ServiceException {
		Connection connection = null;
		try {
			connection = dbDAO.startTransaction();
			menuDAO.createMenuTopping(connection, name);
			dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Topping was not added successfully: ", e);
		}
	}

	public void removeTopping(String topping) throws ServiceException {
		Connection connection = null;
		try {
			connection = dbDAO.startTransaction();
			menuDAO.deleteMenuTopping(connection, topping);
			dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Error while removing topping ", e);
		}
	}

	public void addPizzaSize(String name) throws ServiceException {
		Connection connection = null;
		try {
			System.out.println("in addPizzaSize " + name);
			connection = dbDAO.startTransaction();
			System.out.println("in addPizzaSize, w tx " + name);
			menuDAO.createMenuSize(connection, name);
			dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Pizza size was not added successfully", e);
		}
	}

	public void removePizzaSize(String size) throws ServiceException {
		Connection connection = null;
		try {
			connection = dbDAO.startTransaction();
			menuDAO.deleteMenuSize(connection, size);
			dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Error while removing topping", e);
		}
	}

	public void markNextOrderReady() throws ServiceException {
		Connection connection = null;
		PizzaOrder order = null;
		try {
			connection = dbDAO.startTransaction();
			order = pizzaOrderDAO.findFirstOrder(connection, PizzaOrder.PREPARING);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Error in marking the next order ready", e);
		}
		if (order == null) {
			try {
				dbDAO.rollbackTransaction(connection);
			} catch (Exception e) {
				dbDAO.rollbackAfterException(connection);
			}
			throw new ServiceException("No PREPARING orders exist!");
		}
		order.makeReady();
		try {
			pizzaOrderDAO.updateOrderStatus(connection, order.getId(), PizzaOrder.BAKED);
			dbDAO.commitTransaction(connection); // update occurs here
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Error in marking the next order ready", e);
		}
	}

	public int getCurrentDay() throws ServiceException {
		Connection connection = null;
		int day;
		try {
			connection = dbDAO.startTransaction(); // read-only
			day = adminDAO.findCurrentDay(connection);
			dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Can't access date in db: ", e);
		}
		return day;
	}

	public void advanceDay() throws ServiceException {
		Connection connection = null;
		try {
			connection = dbDAO.startTransaction();
			List<PizzaOrder> pizzaOrders = getTodaysOrders(connection);
			// day is done, so mark today's pizzas as "finished"
			for (PizzaOrder order : pizzaOrders) {
				order.finish();
			}
			adminDAO.advanceDay(connection);
			dbDAO.commitTransaction(connection);
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Unsuccessful advance day", e);
		}
	}

	// helper method to advanceDay
	// executes inside the current transaction
	private List<PizzaOrder> getTodaysOrders(Connection connection) throws Exception {
		int today = adminDAO.findCurrentDay(connection);
		List<PizzaOrder> orders = pizzaOrderDAO.findOrdersByDays(connection, today, today);
		return orders;
	}

	public List<PizzaOrderData> getOrdersByDay(int day) throws ServiceException {
		Connection connection = null;
		try {
			connection = dbDAO.startTransaction();
			List<PizzaOrder> orders = pizzaOrderDAO.findOrdersByDays(connection, day, day);
			List<PizzaOrderData> orders1 = new ArrayList<PizzaOrderData>();
			for (PizzaOrder o : orders) {
				orders1.add(new PizzaOrderData(o));
			}
			dbDAO.commitTransaction(connection);
			return orders1;
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Error while getting daily report ", e);
		}
	}

	public List<PizzaOrderData> getTodaysOrdersByStatus(int status) throws ServiceException {
		Connection connection = null;
		try {
			connection = dbDAO.startTransaction();
			List<PizzaOrder> orders = getTodaysOrders(connection);
			dbDAO.commitTransaction(connection);
			List<PizzaOrderData> orders1 = new ArrayList<PizzaOrderData>();
			for (PizzaOrder o : orders) {
				if (o.getStatus() == status)
					orders1.add(new PizzaOrderData(o));
			}
			return orders1;
		} catch (Exception e) {
			dbDAO.rollbackAfterException(connection);
			throw new ServiceException("Error while getting daily report ", e);
		}
	}

}
