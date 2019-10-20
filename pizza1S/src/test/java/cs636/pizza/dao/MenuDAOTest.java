package cs636.pizza.dao;
// Example JUnit4 test 
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;

import static org.junit.Assert.assertTrue;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
//to be minimalistic, configure only the needed beans
@ContextConfiguration(classes= {DbDAO.class, MenuDAO.class})

public class MenuDAOTest {
	@Autowired
	private DbDAO dbDAO;
	@Autowired
	private MenuDAO menuDAO;	
	// This executes before *each* test, so every test starts out the same way
	@Before
	public void setUp() throws Exception {	
		// we use H2, the DbDAO default db, as the db for testing
		// Note: need to load it first
		dbDAO.setupConnection(null,null,null);
		dbDAO.initializeDb();  // no orders, toppings, sizes
	}

	@After
	public void tearDown() throws Exception {
		dbDAO.close();
	}
	
	@Test
	public void testCreateTopping() throws SQLException
	{
		menuDAO.createMenuTopping("anchovies");	
		int count = menuDAO.findMenuToppings().size();
		assertTrue("first topping not created", count == 1);
	}
	
	@Test
	public void testCreateSize() throws SQLException
	{
		menuDAO.createMenuSize("huge");	
		int count = menuDAO.findMenuSizes().size();
		assertTrue("first topping not created", count == 1);
	}

}
