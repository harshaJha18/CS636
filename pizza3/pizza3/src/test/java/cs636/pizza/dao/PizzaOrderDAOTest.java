package cs636.pizza.dao;
import org.junit.Before;
import org.junit.Test;

//import cs636.pizza.config.StandAloneDataSourceFactory;
import cs636.pizza.domain.PizzaOrder;
import cs636.pizza.domain.PizzaSize;
import cs636.pizza.domain.PizzaTopping;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
// Needed to handle DataSource config
@JdbcTest
// This sets up full set of beans, not all needed
//@ContextConfiguration(classes = {AppConfig.class})
//to be minimalistic, configure only the needed beans, avoid using AppConfig
@ContextConfiguration(classes= {DbDAO.class, MenuDAO.class, PizzaOrderDAO.class})
// use application-test.properties in src/main/resources instead of application.properties
@ActiveProfiles("test")
public class PizzaOrderDAOTest {
	@Autowired
	private DbDAO db;
	@Autowired
	private PizzaOrderDAO pizzaOrderDAO;
	@Autowired
	private MenuDAO menuDAO;
	private Connection connection;
	
	@Before
	public void setup() {
		try {
		db.initializeDb();  // no orders, etc.
		} catch (SQLException e) {
			System.out.println("initialize DB failed in @Before");
		}
	}
	
	// no orders, toppings, sizes
	// in JUnit4, an exception will cause a failure, unless "expected"
	// expected exceptions are listed in @Test, as in the last case,
	// so we don't have to code try-catch in simple test 

	@Test
	public void testInsertOrder() throws SQLException
	{	
		connection = db.startTransaction();
		PizzaSize size = new PizzaSize("small");
		menuDAO.createMenuSize(connection, "small");
		Set<PizzaTopping> tops = new TreeSet<PizzaTopping>();
		tops.add(new PizzaTopping("pepperoni"));	
		menuDAO.createMenuTopping(connection, "pepperoni");
		PizzaOrder order = new PizzaOrder(1, 5, size, tops, 1, 1);
		pizzaOrderDAO.insertOrder(connection, order);
		db.commitTransaction(connection);
	}

	@Test
	public void testOrderNumber() throws SQLException
	{
		connection = db.startTransaction();
		int ordNo = db.findNextId(connection, "next_order_id");
		db.commitTransaction(connection);
		assertTrue("first ordNo 0 or negative", ordNo > 0);		
	}
	
	// no orders yet, so findFirstOrder should fail
	// Note that even if insertOrder has already executed, the setup
	// for this test will have reinitialized the DB to be empty again
	@Test
	public void noFirstOrderYet() throws SQLException
	{
		connection = db.startTransaction();
		PizzaOrder po = pizzaOrderDAO.findFirstOrder(connection, PizzaOrder.PREPARING);
		db.commitTransaction(connection);
		assertTrue("first order exists but shouldn't", po == null);
	}
	
	// case of expected Exception: 	
	@Test(expected=SQLException.class)
	public void badMakeOrder() throws SQLException
	{	
		connection = db.startTransaction();
		PizzaSize size = new PizzaSize("tiny");  // bad size (not on menu)
		Set<PizzaTopping> tops = new TreeSet<PizzaTopping>();
		tops.add(new PizzaTopping("pepperoni"));
		
		PizzaOrder order = new PizzaOrder(5, size, tops, 1, 1);
		pizzaOrderDAO.insertOrder(connection, order);
		db.commitTransaction(connection);
	}
}
