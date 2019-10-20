package cs636.pizza.dao;

// Example JUnit4 test 
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import cs636.pizza.domain.PizzaOrder;
import cs636.pizza.domain.PizzaSize;
import cs636.pizza.domain.PizzaTopping;

import static org.junit.Assert.assertTrue;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

@RunWith(SpringRunner.class)
//to be minimalistic, configure only the needed beans
@ContextConfiguration(classes= {DbDAO.class, MenuDAO.class, PizzaOrderDAO.class})

public class PizzaOrderDAOTest {
	@Autowired
	private DbDAO dbDAO;
	@Autowired
	private MenuDAO menuDAO;
	@Autowired
	private PizzaOrderDAO pizzaOrderDAO;

	// This executes before *each* test, so every test starts out the same way
	@Before
	public void setUp() throws Exception {
		// we use H2, the DbDAO default db, as the db for testing
		// Note: need to load it first
		dbDAO.setupConnection(null,null,null);
		dbDAO.initializeDb(); // no orders, toppings, sizes
		menuDAO.createMenuSize("small"); // make a legit size
		menuDAO.createMenuTopping("pepperoni");
	}

	@After
	public void tearDown() throws Exception {
		dbDAO.close();
	}

	@Test
	public void testMakeOrder() throws SQLException {
		PizzaSize size = new PizzaSize("small");
		Set<PizzaTopping> tops = new TreeSet<PizzaTopping>();
		tops.add(new PizzaTopping("pepperoni"));

		PizzaOrder order = new PizzaOrder(1, 5, size, tops, 1, 1);
		pizzaOrderDAO.insertOrder(order);
	}

	@Test
	public void testOrderNumber() throws SQLException {
		// in JUnit4, an exception will cause a failure, unless "expected"
		// expected exceptions are listed in @Test, as in next case
		// so we don't have to code try-catch in simple test
		int ordNo = dbDAO.findNextId("next_order_id");
		assertTrue("first ordNo 0 or negative", ordNo > 0);
	}

	// no orders yet, so findFirstOrder should return null
	// Note that even if insertOrder has already executed, the setup
	// for this test will have reinitialized the DB to be empty again
	@Test
	public void noFirstOrderYet() throws SQLException {
		PizzaOrder po = pizzaOrderDAO.findFirstOrder(1);
		assertTrue("first order exists but shouldn't", po == null);
	}

	// case of expected Exception:

	@Test(expected = SQLException.class)
	public void badMakeOrder() throws SQLException {
		PizzaSize size = new PizzaSize("tiny"); // bad size (not on menu)
		Set<PizzaTopping> tops = new TreeSet<PizzaTopping>();
		tops.add(new PizzaTopping("pepperoni"));
		PizzaOrder order = new PizzaOrder(5, size, tops, 1, 1);
		pizzaOrderDAO.insertOrder(order);
	}
}
