package cs636.pizza.dao;
// Example JUnit4 test 
import org.junit.After;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.*;

import static org.junit.Assert.assertTrue;
import java.sql.Connection;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
// Needed to handle DataSource config
@JdbcTest
// This sets up full set of beans, not all needed
//@ContextConfiguration(classes = {AppConfig.class})
//to be minimalistic, configure only the needed beans, avoid using AppConfig
@ContextConfiguration(classes= {DbDAO.class, MenuDAO.class})
// use application-test.properties in src/main/resources instead of application.properties
@ActiveProfiles("test")

//import cs636.pizza.config.StandAloneDataSourceFactory;
public class MenuDAOTest {
	@Autowired
	private DbDAO db;
	@Autowired
	private MenuDAO menuDAO;
	private Connection connection;
	

	@After
	public void tearDown() throws Exception {
		// This executes even after an exception
		// so we need to rollback here in case of exception
		// (If the transaction was successful, it's already
		// committed, and this won't hurt.)
		db.rollbackAfterException(connection);
	}
	
	@Test
	public void testCreateTopping() throws SQLException
	{
		connection = db.startTransaction();
		menuDAO.createMenuTopping(connection, "anchovies");	
		int count = menuDAO.findMenuToppings(connection).size();
		assertTrue("first topping not created", count == 1);
		db.commitTransaction(connection);
	}
	
	@Test
	public void testCreateSize() throws SQLException
	{
		connection = db.startTransaction();
		menuDAO.createMenuSize(connection, "huge");	
		int count = menuDAO.findMenuSizes(connection).size();
		assertTrue("first topping not created", count == 1);
		db.commitTransaction(connection);
	}

}
