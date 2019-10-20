package cs636.pizza.service;
import static org.junit.Assert.*;
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

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@RunWith(SpringRunner.class)
//Needed to handle DataSource config
@JdbcTest
//to be minimalistic, configure only the needed beans
@ContextConfiguration(classes= {AdminService.class,StudentService.class, DbDAO.class, MenuDAO.class,
		PizzaOrderDAO.class, AdminDAO.class })

// These are not "unit tests" but rather "integration tests" since they use
// multiple classes from this project at once
public class AdminServiceTest1 {
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
		adminService.initializeDb(); // bring system to known state
	}

	@After
	public void tearDown() throws Exception {
		// This executes even after an exception
		// do full shutdown to make sure that
		// even if a transaction is still going
		// it gets cleaned up
		dbDAO.shutdown();	
	}
	
	@Test
	public void testAddTopping() throws ServiceException {
		adminService.addTopping("mushrooms");
		assertTrue("first topping not created", studentService.getToppingNames().size()==1);
	}

	@Test
	public void testAddPizzaSize() throws ServiceException {
		adminService.addPizzaSize("small");
		assertTrue("first size not created", studentService.getSizeNames().size()==1);
	}
	@Test
	public void testAddRemovePizzaSize() throws ServiceException {
		adminService.addPizzaSize("small");
		assertTrue("first size not created", studentService.getSizeNames().size()==1);
		adminService.removePizzaSize("small");
		assertTrue("first size created and removed", studentService.getSizeNames().size()==0);
	}
	
	@Test(expected=ServiceException.class)
	public void testAddDupPizzaSize() throws ServiceException {
		adminService.addPizzaSize("small");
		adminService.addPizzaSize("small");
	}
	
	// but you can't delete a nonexistent size quietly
	@Test(expected=ServiceException.class)
	public void testRemoveNonExistentPizzaSize() throws ServiceException {
		adminService.removePizzaSize("small");
	}
	
	@Test
	public void testGetPizzaSizes0() throws ServiceException {
		assertTrue("sizes exist at init (but shouldn't)", studentService.getSizeNames().size()==0);
	}

	@Test
	public void testGetPizzaSizes() throws ServiceException {
		assertTrue("sizes exist at init", studentService.getSizeNames().size()==0);
		adminService.addPizzaSize("small");
		assertTrue("first added size not there", studentService.getSizeNames().size()==1);
		assertTrue("first added size not right", 
				studentService.getSizeNames().iterator().next().equals("small"));
	}

	@Test
	public void testAddTopping1() throws ServiceException  {
		assertTrue("toppings exist at init", studentService.getToppingNames().size()==0);
		adminService.addTopping("xxx");
		assertTrue("first added topping not there", studentService.getToppingNames().size()==1);
		assertTrue("first added topping not right", 
				studentService.getToppingNames().iterator().next().equals("xxx"));
	}

}
