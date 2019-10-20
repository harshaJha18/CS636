package cs636.pizza.dao;

// Example JUnit4 test 
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManagerFactory;
import org.springframework.test.context.junit4.SpringRunner;
import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.domain.PizzaOrder;
import cs636.pizza.domain.PizzaSize;
import cs636.pizza.domain.PizzaTopping;

@RunWith(SpringRunner.class)
//Needed to handle DataSource config
@JdbcTest
//This sets up full set of beans, not all needed
//@ContextConfiguration(classes = {AppConfig.class})
//to be minimalistic, configure only the needed beans, avoid using AppConfig
@ContextConfiguration(classes= {DbDAO.class, MenuDAO.class, PizzaOrderDAO.class})
//use application-test.properties in src/main/resources instead of application.properties

public class PizzaOrderDAOTest1 {
	@Autowired
	private DbDAO dbDAO;
	@Autowired
	private MenuDAO menuDAO;
	@Autowired
	private PizzaOrderDAO pizzaOrderDAO;

	@Before
	// each test runs in its own transaction, on same db setup
	public void setup() {
		EntityManagerFactory emf = PizzaSystemConfig.configureJPA(null, null, null);
		dbDAO.setEntityManagerFactory(emf);
		dbDAO.startTransaction();	
		dbDAO.initializeDb(); // no orders, toppings, sizes
		dbDAO.commitTransaction();
		menuDAO = new MenuDAO(dbDAO);
		pizzaOrderDAO = new PizzaOrderDAO(dbDAO, menuDAO);
		dbDAO.startTransaction();
		menuDAO.createMenuSize("small");  // make a legit size
		menuDAO.createMenuTopping("pepperoni"); 
		dbDAO.commitTransaction();
	}

	@After
	public void tearDown() {
		// This executes even after an exception
		// so we need to rollback here in case of exception
		// (If the transaction was successful, it's already
		// committed, and this won't hurt.)
		dbDAO.rollbackAfterException();
		dbDAO.shutdown();	
	}

	@Test
	public void testMakeOrder() throws SQLException
	{	
		dbDAO.startTransaction();	
		PizzaSize size = new PizzaSize("small");
		Set<PizzaTopping> tops = new TreeSet<PizzaTopping>();
		tops.add(new PizzaTopping("pepperoni"));
		PizzaOrder order = new PizzaOrder(5, size, tops, 1, 1);
		pizzaOrderDAO.insertOrder(order);
		dbDAO.commitTransaction();
	}

	// no orders yet, so findFirstOrder should return null
	@Test
	public void noFirstOrderYet() {
		dbDAO.startTransaction();
		PizzaOrder p = pizzaOrderDAO.findFirstOrder(1);
		assertTrue(p==null);
		dbDAO.commitTransaction();
	}
	
	// case of expected Exception: 
	
	@Test(expected=RuntimeException.class)
	public void badMakeOrder() throws SQLException
	{	
		dbDAO.startTransaction();
		PizzaSize size = new PizzaSize("tiny");  // bad size (not on menu)
		Set<PizzaTopping> tops = new TreeSet<PizzaTopping>();
		tops.add(new PizzaTopping("pepperoni"));
		
		PizzaOrder order = new PizzaOrder(5, size, tops, 1, 1);
		pizzaOrderDAO.insertOrder(order);
		dbDAO.commitTransaction();
	}

}
