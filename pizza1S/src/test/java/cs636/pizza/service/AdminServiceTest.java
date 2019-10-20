package cs636.pizza.service;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.PizzaOrderDAO;

@RunWith(SpringRunner.class)
// configure only the needed beans, service API and their called DAOs
@ContextConfiguration(classes= {AdminService.class,StudentService.class, DbDAO.class, MenuDAO.class,
		PizzaOrderDAO.class, AdminDAO.class })
// These are not "unit tests" but rather "integration tests" since they use
// multiple classes from this project at once
public class AdminServiceTest {
	// Since we're setting up multiple objects here, this is not a real "unit test"
	// but rather an "integration test"
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
	}

	@After
	public void tearDown() throws Exception {
		dbDAO.close();
	}

	@Test
	public void testAddTopping() throws ServiceException {
		adminService.addTopping("mushrooms");
		assertTrue("first topping not created", studentService.getToppingNames().size()==1);
	}

	@Test
	public void testAddPizzaSize() throws ServiceException {
		adminService.addSize("small");
		assertTrue("first size not created", studentService.getSizeNames().size()==1);
	}
	@Test
	public void testAddRemovePizzaSize() throws ServiceException {
		adminService.addSize("small");
		assertTrue("first size not created", studentService.getSizeNames().size()==1);
		adminService.removeSize("small");
		assertTrue("first size created and removed", studentService.getSizeNames().size()==0);
	}
	
	// duplicate will cause unique constraint failure
	@Test(expected=ServiceException.class)
	public void testAddDupPizzaSize() throws ServiceException {
		adminService.addSize("small");
		adminService.addSize("small");
	}
	
	// you can't delete a nonexistent size quietly
	@Test(expected=ServiceException.class)
	public void testRemoveNonExistentPizzaSize() throws ServiceException {
		adminService.removeSize("small");
	}

	@Test
	public void testGetPizzaSizes() throws ServiceException {
		assertTrue("sizes exist at init (should be none)", studentService.getSizeNames().size()==0);
		adminService.addSize("small");
		assertTrue("first added size not there", studentService.getSizeNames().size()==1);
		assertTrue("first added size not right", 
				studentService.getSizeNames().iterator().next().equals("small"));
	}

	@Test
	public void testAddTopping1() throws ServiceException  {
		assertTrue("toppings exist at init (should be none)", studentService.getToppingNames().size()==0);
		adminService.addTopping("xxx");
		assertTrue("first added topping not there", studentService.getToppingNames().size()==1);
		assertTrue("first added topping not right", 
				studentService.getToppingNames().iterator().next().equals("xxx"));

	}

}
