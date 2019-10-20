package cs636.pizza.service;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.PizzaOrderDAO;

@RunWith(SpringRunner.class)
// configure only the needed beans, service API and their called DAOs
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
		dbDAO.setupConnection(null,null,null);
		adminService.initializeDb(); // bring system to known state
		adminService.addTopping("xxx"); // set up a topping and size
		adminService.addSize("yyy");
	}

	@After
	public void tearDown() throws Exception {
		// This executes even after an exception
		// do full shutdown to make sure that
		// even if a transaction is still going
		// it gets cleaned up
		dbDAO.close();	
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
