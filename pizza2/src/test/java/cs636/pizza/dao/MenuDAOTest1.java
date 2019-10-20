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
import javax.persistence.EntityManagerFactory;
import org.springframework.test.context.junit4.SpringRunner;
import cs636.pizza.config.PizzaSystemConfig;

@RunWith(SpringRunner.class)
//Needed to handle DataSource config
@JdbcTest
//This sets up full set of beans, not all needed
//@ContextConfiguration(classes = {AppConfig.class})
//to be minimalistic, configure only the needed beans, avoid using AppConfig
@ContextConfiguration(classes= {DbDAO.class, MenuDAO.class})
//use application-test.properties in src/main/resources instead of application.properties

public class MenuDAOTest1 {
	@Autowired
	private DbDAO dbDAO;
	@Autowired
	private MenuDAO menuDAO;

	@Before
	public void setup() {
		EntityManagerFactory emf = PizzaSystemConfig.configureJPA(null, null, null);
		dbDAO.setEntityManagerFactory(emf);
		dbDAO.startTransaction();
		dbDAO.initializeDb(); // no orders, toppings, sizes
		menuDAO = new MenuDAO(dbDAO);
		dbDAO.commitTransaction();
	}

	@After
	public void tearDown() {
		// This executes even after an exception as well as normal ending
		// so we need to rollback here in case of exception
		// (If the transaction was successful, it's already
		// committed, and this won't hurt.)
		dbDAO.rollbackAfterException();
		dbDAO.shutdown();	
	}
	
	@Test
	public void testCreateTopping() throws SQLException
	{
		dbDAO.startTransaction();
		menuDAO.createMenuTopping("anchovies");	
		int count = menuDAO.findMenuToppings().size();
		dbDAO.commitTransaction();
		assertTrue("first topping not created", count == 1);
	}
	

	@Test(expected=RuntimeException.class)
	public void testDuplicateTopping()
	{
		dbDAO.startTransaction();
		menuDAO.createMenuTopping("anchovies");	
		menuDAO.createMenuTopping("anchovies");	
		dbDAO.commitTransaction();
	}
	
}
