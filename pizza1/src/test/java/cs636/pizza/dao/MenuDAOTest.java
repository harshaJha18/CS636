package cs636.pizza.dao;
// Example JUnit4 test 
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;

import static org.junit.Assert.assertTrue;
import java.sql.SQLException;

public class MenuDAOTest {
	private DbDAO db;
	private MenuDAO menuDAO;
	
	// This executes before *each* test, so every test starts out the same way
	@Before
	public void setUp() throws Exception {
		// we use H2, the DbDAO default db, as the db for testing
		// Note: need to load it first
		db = new DbDAO(null, null, null);
		db.initializeDb();  // no orders, toppings, sizes
		menuDAO = new MenuDAO(db);
	}

	@After
	public void tearDown() throws Exception {
		db.close();
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
