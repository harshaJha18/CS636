package cs636.pizza.presentation.web;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.presentation.SystemTest;
import cs636.pizza.service.PizzaOrderData;
import cs636.pizza.service.AdminService;
import cs636.pizza.service.ServiceException;
import cs636.pizza.service.StudentService;

@Controller
public class AdminController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private StudentService studentService;
	// for listVariables page--
	@Value("${spring.datasource.url}")
	private String dbUrl;
	
	private static final String ADMIN_BASE_URL = "/adminController/";
	private static final String ADMIN_JSP_DIR = "admin/";

	@RequestMapping(ADMIN_BASE_URL+"initializeDB.html")
	public String adminInitDB(Model model) {
		String info;
		try {
			adminService.initializeDb();
			info = "Initialize db: success";
		} catch (ServiceException e) {
			info = "Initialize db: failed " + PizzaSystemConfig.exceptionReport(e);
		}
		model.addAttribute("info", info);
		String url = ADMIN_JSP_DIR + "initializeDB";
		return url;
	}
	
	@RequestMapping(ADMIN_BASE_URL+"sysTest.html")
	public String sysTest(Model model) {
		String info;
		try {
			SystemTest test = new SystemTest(adminService, studentService);
			test.runSystemTest();

			info = "SystemTest success: see console log for output";
		} catch (Exception e) {
			info = "SystemTest failed: " + PizzaSystemConfig.exceptionReport(e);
		}
		model.addAttribute("info", info);
		String url = ADMIN_JSP_DIR + "initializeDB";
		return url;
	}
	
	@RequestMapping(ADMIN_BASE_URL+"adminWelcome.html")
	public String adminWelcome(Model model) {	
		String url = ADMIN_JSP_DIR + "adminWelcome";
		return url;
	}
	
	@RequestMapping(ADMIN_BASE_URL+"listVariables.html")
	public String listVariables(Model model) {	
		model.addAttribute("dbUrl", dbUrl);
		System.out.println("dbUrl from application.properties: " + dbUrl);
		String url = ADMIN_JSP_DIR + "listVariables";
		return url;
	}
	
	@RequestMapping(ADMIN_BASE_URL+"logout.html")
	public String logout(Model model, HttpServletRequest request) {	
		request.getSession().invalidate();  // drop session
		String url = ADMIN_JSP_DIR + "logout";
		return url;
	}

	@RequestMapping(ADMIN_BASE_URL+"days")
	public String manageDays(Model model, @RequestParam(value = "command", required = false) String command) {
		
		if (command != null && command.equals("advance")) { // user clicked button
			try {
				adminService.advanceDay();
			} catch (ServiceException e) {
				String error = "manageDays failed: " + PizzaSystemConfig.exceptionReport(e);
				model.addAttribute("error", error);
				String url = ADMIN_JSP_DIR + "error";
				return url;
			}
		}
		int day = 0;
		List<PizzaOrderData> orders1 = null;
		try {
			day = adminService.getCurrentDay();
			orders1 = adminService.getOrdersByDay(day);
			System.out.println("seeing " + orders1.size() + " orders");
		} catch (ServiceException e) {
			String error = "manageDays failed: " + PizzaSystemConfig.exceptionReport(e);
			model.addAttribute("error", error);
			String url = ADMIN_JSP_DIR + "error";
			return url;
		}
		model.addAttribute("currentDay", day);
		model.addAttribute("orders", orders1);
		String url = ADMIN_JSP_DIR + "dayView";
		return url;
	}

	@RequestMapping(ADMIN_BASE_URL+"orders")
	public String manageOrders(Model model, @RequestParam(value = "command", required = false) String command) {
		System.out.println("in manageOrders, seeing command = " + command);
		if (command != null && command.equals("mark")) { // button
			try {
				adminService.markNextOrderReady();
			} catch (ServiceException e) {
				String error = "manageOrders failed: " + PizzaSystemConfig.exceptionReport(e);
				model.addAttribute("error", error);
				String url = ADMIN_JSP_DIR + "error";
				return url;
			}
		}

		List<PizzaOrderData> orders1 = null;
		List<PizzaOrderData> orders2 = null;
		try {
			orders1 = adminService.getTodaysOrdersByStatus(PizzaOrderData.PREPARING);
			System.out.println("seeing " + orders1.size() + " preparing orders");
			orders2 = adminService.getTodaysOrdersByStatus(PizzaOrderData.BAKED);
			System.out.println("seeing " + orders2.size() + " baked orders");

		} catch (ServiceException e) {
			String error = "manageOrders failed: " + PizzaSystemConfig.exceptionReport(e);
			model.addAttribute("error", error);
			String url = ADMIN_JSP_DIR + "error";
			return url;
		}
		model.addAttribute("orders_preparing", orders1);
		model.addAttribute("orders_baked", orders2);
		String url = ADMIN_JSP_DIR + "orderView";
		return url;
	}
	
	@RequestMapping(ADMIN_BASE_URL+"toppings")
	public String manageToppings(Model model, @RequestParam(value = "command", required = false) String command,
			@RequestParam(value = "item", required = false) String item) {
		Set<String> allToppings = null;
		try {
			if (item != null) {
				// null command means textbox entry with <CR> submitted by
				// browser
				if (command == null || command.equalsIgnoreCase("add")) {
					adminService.addTopping(item); // item is name
				} else if (command.equalsIgnoreCase("remove")) {
					removeTopping(item); // item is id
				} else {
					String error = "manageToppings failed because of bad request parameter: " + command;
					System.out.println(error);
					model.addAttribute("error", error);
					String url = ADMIN_JSP_DIR + "error";
					return url;
				}
			}
			allToppings = studentService.getToppingNames();
		} catch (ServiceException e) {
			String error = PizzaSystemConfig.exceptionReport(e);
			model.addAttribute("error", error);
			String url = ADMIN_JSP_DIR + "error";
			return url;
		}

		model.addAttribute("allToppings", allToppings);
		String url = ADMIN_JSP_DIR + "toppingView";
		return url;
	}

	@RequestMapping(ADMIN_BASE_URL+"sizes")
	public String manageSizes(Model model, @RequestParam(value = "command", required = false) String command,
			@RequestParam(value = "item", required = false) String item) {
		Set<String> allSizes = null;
		try {
			if (item != null) {
				// null command means textbox entry with <CR> submitted
				if (command == null || command.equalsIgnoreCase("add")) {
					adminService.addPizzaSize(item); // item is name
				} else if (command.equalsIgnoreCase("remove")) {
					removeSize(item); // item is id
				} else {
					String error = "manageSizess failed because of bad request parameter: " + command;
					model.addAttribute("error", error);
					String url = ADMIN_JSP_DIR + "error";
					return url;
				}
			}
			allSizes = studentService.getSizeNames();
		} catch (ServiceException e) {
			String error = PizzaSystemConfig.exceptionReport(e);
			model.addAttribute("error", error);
			String url = ADMIN_JSP_DIR + "error";
			return url;
		}

		model.addAttribute("allSizes", allSizes);
		String url = ADMIN_JSP_DIR + "sizeView";
		return url;
	}

	private void removeSize(String size) throws ServiceException {
		adminService.removePizzaSize(size);
	}

	private void removeTopping(String topping) throws ServiceException {
		adminService.removeTopping(topping);
	}

}
