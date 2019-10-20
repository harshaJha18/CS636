package cs636.pizza.service;

import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.*;

import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.PizzaOrderDAO;

@RunWith(SpringRunner.class)
//Needed to handle DataSource config
@JdbcTest
//to be minimalistic, configure only the needed beans
@ContextConfiguration(classes= {AdminService.class,StudentService.class, DbDAO.class, MenuDAO.class,
		PizzaOrderDAO.class, AdminDAO.class })

public class DeleteToppingTest {
	@Autowired
	private DbDAO dbDAO;
	@Autowired
	private AdminService adminService;
	@Autowired
	private StudentService studentService;
	
	@Before
	public void setUp() throws Exception {
		EntityManagerFactory emf = PizzaSystemConfig.configureJPA(null, null, null);
		dbDAO.setEntityManagerFactory(emf);
		adminService.initializeDb(); // bring system to known statete
		adminService.addTopping("xxx"); // set up a topping and size
		adminService.addPizzaSize("yyy");
	}

	@After
	public void tearDown() throws Exception {
		// This executes even after an exception
		// do full shutdown to make sure that
		// even if a transaction is still going
		// it gets cleaned up
		dbDAO.shutdown();	
	}
	
	// one user selects a topping, another deletes it
	// then first user orders with it
	// Note: with fancier code, we could check that the
	// exception message has "Topping" in it, as expected
	@Test(expected=ServiceException.class)
	public void testDropToppingMakeOrder() throws ServiceException  {
		// user 1 action
		Set<String> tops = studentService.getToppingNames();
		// user 2 action
		adminService.removeTopping("xxx");  // xxx was added in setUp
		// user1 actions
		String size = studentService.getSizeNames().iterator().next();
		studentService.makeOrder(1, size, tops);
	}

}
