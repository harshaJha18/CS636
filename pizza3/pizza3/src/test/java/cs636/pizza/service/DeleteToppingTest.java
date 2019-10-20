package cs636.pizza.service;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.*;

import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.PizzaOrderDAO;

@RunWith(SpringRunner.class)
//Needed to handle DataSource config
@JdbcTest
//This sets up full set of beans, not all needed
//@ContextConfiguration(classes = {AppConfig.class})
//to be minimalistic, configure only the needed beans, avoid using AppConfig
@ContextConfiguration(classes= {AdminService.class,StudentService.class, DbDAO.class, MenuDAO.class,
		PizzaOrderDAO.class, AdminDAO.class })
//use application-test.properties in src/main/resources instead of application.properties
@ActiveProfiles("test")
public class DeleteToppingTest {
	@Autowired
	private AdminService adminService;
	@Autowired
	private StudentService studentService;

	@Before
	public void setup() {
		try {
			adminService.initializeDb();  // no orders, etc.
		} catch (ServiceException e) {
			System.out.println("initialize DB failed in @Before");
		}
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
