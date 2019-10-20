package cs636.pizza.service;

import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.PizzaOrderDAO;
import cs636.pizza.domain.PizzaOrder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class captures the business logic for admin-related interactions, the
 * actions that ordinary users (students) don't need.
 * 
 * Only one instance of this class is instantiated, i.e., its object is a
 * singleton object, and this singleton receives references to the singleton DAO
 * objects at its own creation time.
 */

public class AdminService {

	private DbDAO dbDAO;
	private AdminDAO adminDAO;
	private MenuDAO menuDAO;
	private PizzaOrderDAO pizzaOrderDAO;

	public AdminService(DbDAO db, AdminDAO admDAO,PizzaOrderDAO poDAO,  MenuDAO mDAO) {
		dbDAO = db;
		adminDAO = admDAO;
		menuDAO = mDAO;
		pizzaOrderDAO = poDAO;
	}

	public void initializeDb() throws ServiceException {
		try {
			dbDAO.initializeDb();
		} catch (SQLException e) {
			throw new ServiceException(
					"Can't initialize DB (probably need to load DB)", e);
		}
	}

	public void addTopping(String name) throws ServiceException {
		try {
			menuDAO.createMenuTopping(name);
		} catch (SQLException e) {
			throw new ServiceException("Topping was not added successfully: ",
					e);
		}
	}

	public void removeTopping(String topping) throws ServiceException {
		try {
			menuDAO.deleteMenuTopping(topping);
		} catch (SQLException e) {
			throw new ServiceException("Error while removing topping" + e, e);
		}
	}

	public void addSize(String name) throws ServiceException {
		try {
			menuDAO.createMenuSize(name);

		} catch (SQLException e) {
			throw new ServiceException("Pizza size was not added successfully",
					e);
		}
	}

	public void removeSize(String size) throws ServiceException {
		try {
			menuDAO.deleteMenuSize(size);
		} catch (SQLException e) {
			throw new ServiceException("Error while removing topping", e);
		}
	}

	public void markNextOrderReady() throws ServiceException {
		try {
			PizzaOrder order = pizzaOrderDAO.findFirstOrder(PizzaOrder.PREPARING);
			if (order != null)
				pizzaOrderDAO.updateOrderStatus(order.getId(), PizzaOrder.BAKED);
		} catch (SQLException e) {
			throw new ServiceException("Error in marking the next order ready",
					e);
		}
	}

	public int getCurrentDay() throws ServiceException {
		int date;
		try {
			date = adminDAO.findCurrentDay();
		} catch (SQLException e) {
			throw new ServiceException("Can't access date in db: ", e);
		}
		return date;
	}

	public void advanceDay() throws ServiceException {
		try {
			List<PizzaOrder> pizzaOrders = getTodaysOrders();
			// day is done, so mark today's pizzas as "finished"
			for (PizzaOrder order : pizzaOrders) {
				order.finish();
				pizzaOrderDAO.updateOrderStatus(order.getId(),
						PizzaOrder.FINISHED);
			}
			adminDAO.advanceDay();
		} catch (SQLException e) {
			throw new ServiceException("Unsuccessful advance day", e);
		}
	}

	// helper method to advanceDay
	private List<PizzaOrder> getTodaysOrders() throws SQLException {
		int today = adminDAO.findCurrentDay();
		List<PizzaOrder> orders = pizzaOrderDAO.findOrdersByDays(today, today);
		return orders;
	}

	public List<PizzaOrderData> getOrdersByDay(int day) throws ServiceException {
		try {
			List<PizzaOrder> orders = pizzaOrderDAO.findOrdersByDays(day, day);
			List<PizzaOrderData> orders1 = new ArrayList<PizzaOrderData>();
			for (PizzaOrder o : orders) {
				orders1.add(new PizzaOrderData(o));
			}
			return orders1;
		} catch (SQLException e) {
			throw new ServiceException("Error while getting orders ", e);
		}
	}

	public List<PizzaOrderData> getTodaysOrdersByStatus(int status)
			throws ServiceException {
		try {
			List<PizzaOrder> orders = getTodaysOrders();
			List<PizzaOrderData> orders1 = new ArrayList<PizzaOrderData>();
			for (PizzaOrder o : orders) {
				if (o.getStatus() == status)
					orders1.add(new PizzaOrderData(o));
			}
			return orders1;
		} catch (SQLException e) {
			throw new ServiceException("Error while getting orders by status ", e);
		}
	}
}
