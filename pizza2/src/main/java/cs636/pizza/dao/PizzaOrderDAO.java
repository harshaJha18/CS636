package cs636.pizza.dao;
/**
 *
 * Data access class for pizza order objects, including their sizes and toppings
 */

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cs636.pizza.domain.PizzaOrder;
import cs636.pizza.domain.PizzaTopping;

// Note: these can throw various subclasses of RuntimeException, 
// as defined by JPA (compare to SQLException of JDBC, a checked exception)
@Repository
public class PizzaOrderDAO {
	@Autowired
    private DbDAO dbDAO;
	@Autowired
    private MenuDAO menuDAO;
    
	public PizzaOrderDAO(DbDAO db, MenuDAO menuDAO) {
		this.dbDAO = db;
		this.menuDAO = menuDAO;
	}
	
  	public void insertOrder(PizzaOrder order) 
	{
  		EntityManager em = dbDAO.getEM();
  		
  		// We don't want a FK from a pizza_sizes row to menu_sizes row
  		// because it would prevent pizza size deletion
  		// and similarly with pizza toppings
  		// but we can check for size name and topping name existence here explicitly--
  		String sizeName = order.getPizzaSize().getSizeName();
  		if (menuDAO.findMenuSize(sizeName) == null)
  			throw new IllegalArgumentException("no such pizza size available");
  		for (PizzaTopping t: order.getPizzaToppings()) {
  			if (menuDAO.findMenuTopping(t.getToppingName())== null)
  				throw new IllegalArgumentException("no such pizza topping available");
   		}
  		em.persist(order.getPizzaSize());	
   		em.persist(order);
  		for (PizzaTopping t: order.getPizzaToppings()) {
  			t.setPizzaOrder(order);
  			em.persist(t);
  		}
  			
	}
	
	// Get orders for a certain day and room number
  	// Callers can get toppings as needed by using o.getToppings() (by lazy loading)
  	// while the persistence context is still available (i.e., until commit)
	public List<PizzaOrder> findOrdersByRoom(int roomNumber, int day) 
	{
    	TypedQuery<PizzaOrder> query = dbDAO.getEM().createQuery("select o from PizzaOrder o where o.roomNumber = "
				+ roomNumber + " and o.day = " + day + " order by o.id", PizzaOrder.class);
		List<PizzaOrder> orders = query.getResultList();	
		return orders;
	}
	
	// find first order with specified status, or null if no orders there
	public PizzaOrder findFirstOrder(int status) 
	{
    	TypedQuery<PizzaOrder> query = dbDAO.getEM().createQuery("select o from PizzaOrder o where o.status = "
				+ status + " order by o.id", PizzaOrder.class);	
		List<PizzaOrder> orders = query.getResultList();
		if (orders.isEmpty())
			return null;
		else
			return orders.get(0);
	}
	
	// get all orders between day1 and day2 (inclusive)
	public List<PizzaOrder> findOrdersByDays(int day1, int day2) {
    	TypedQuery<PizzaOrder> query = dbDAO.getEM().createQuery("select o from PizzaOrder o where o.day >= " 
				+ day1 + " and o.day <= " + day2 + " order by o.id", PizzaOrder.class);	
    	List<PizzaOrder> orders = query.getResultList();
		return orders;
	}
}
