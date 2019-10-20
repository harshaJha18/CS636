package cs636.pizza.presentation;

import java.util.List;
import java.util.Set;

import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.service.AdminService;
import cs636.pizza.service.PizzaOrderData;
import cs636.pizza.service.ServiceException;
import cs636.pizza.service.StudentService;

public class MinTest {

	private AdminService adminService;
	private StudentService studentService;

	public MinTest(String dbUrl, String usr, String psswd) throws Exception {
		PizzaSystemConfig.configureServices(dbUrl, usr, psswd);
		adminService = PizzaSystemConfig.getAdminService();
		studentService = PizzaSystemConfig.getStudentService();
	}

	public static void main(String[] args) {
		try {
			MinTest minTest = new MinTest(null, null, null); // for default of
																// HSQLDB
			minTest.test1();
		} catch (Exception e) {
			System.out.println("Error in run of MinTest: ");
			System.out.println(PizzaSystemConfig.exceptionReport(e));
		}
	}

	public void test1() throws ServiceException {
		adminService.initializeDb(); // create new tables, etc.
		adminService.addSize("small"); // call serviceAPI
		adminService.addTopping("Pepperoni");
		Set<String> allToppings = studentService.getToppingNames();
		Set<String> allSizes = studentService.getSizeNames();
		// get a particular PizzaSize
		String chosenPizzaSize = allSizes.iterator().next();
		// call makeOrder with domain objects gotten from service API calls
		studentService.makeOrder(5 /* roomno */, chosenPizzaSize, allToppings);

		List<PizzaOrderData> report = studentService.getOrderStatus(5);
		PresentationUtils.printOrderStatus(report, System.out);
		System.out.println("getOrderStatus returned " + report.size()
				+ " pizza orders");
	}
}
