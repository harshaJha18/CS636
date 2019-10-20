package cs636.pizza.config;

/**
 * @author Betty O'Neil
 *
 * Configure the service objects, shut them down
 * 
 */

import java.io.PrintWriter;
import java.io.StringWriter;

import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.PizzaOrderDAO;
import cs636.pizza.service.AdminService;
import cs636.pizza.service.StudentService;

public class PizzaSystemConfig {
	// for ease of testing, handle only a few rooms--
	public static final int NUM_OF_ROOMS = 10;

	// the service objects in use, representing all lower layers to the app
	private static AdminService adminService;
	private static StudentService studentService;
	// the lower-level service objects--
	private static AdminDAO adminDAO;
	private static MenuDAO menuDAO;
	private static PizzaOrderDAO pizzaOrderDAO;
	private static DbDAO dbDAO; // contains Connection

	// set up service API, data access objects
	public static void configureServices(String dbUrl, String usr, String psswd)
			throws Exception {
		// Configure service layer and DAO objects, all singletons--
		// DAOs first, then service objects, for DI (dependency injection),
		// a form of IoC (inversion of control)
		// The service objects get what they need at creation-time
		// This is known as "constructor injection"
		try {
			dbDAO = new DbDAO(dbUrl, usr, psswd);
			adminDAO = new AdminDAO(dbDAO);
			menuDAO = new MenuDAO(dbDAO);
			pizzaOrderDAO = new PizzaOrderDAO(dbDAO, menuDAO);
			adminService = new AdminService(dbDAO, adminDAO, pizzaOrderDAO, menuDAO);
			studentService = new StudentService(pizzaOrderDAO, menuDAO, adminDAO);
		} catch (Exception e) {
			System.out.println(exceptionReport(e));
			// e.printStackTrace(); // causes lots of output
			System.out.println("Problem with contacting DB: " + e);
			throw (e); // rethrow to notify caller
		}
	}

	// Compose an exception report
	// and return the string for callers to use
	public static String exceptionReport(Exception e) {
		String message = e.toString(); // exception name + message
		if (e.getCause() != null) {
			message += "\n  cause: " + e.getCause();
			if (e.getCause().getCause() != null) {
				message += "\n    cause's cause: " + e.getCause().getCause();
			}
		}
		message += "\n Stack Trace: "
						+ exceptionStackTraceString(e);
		return message;
	}

	private static String exceptionStackTraceString(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	// When the app exits, the shutdown happens automatically
	// For other cases, call this to free up the JDBC Connection
	public static void shutdownServices() throws Exception {
		dbDAO.close(); // close JDBC connection
	}

	// Let the apps get the business logic layer services
	public static AdminService getAdminService() {
		return adminService;
	}

	public static StudentService getStudentService() {
		return studentService;
	}
}
