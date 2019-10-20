package cs636.pizza.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cs636.pizza.dao.DbDAO;
import cs636.pizza.presentation.SystemTest;
import cs636.pizza.service.AdminService;
import cs636.pizza.service.StudentService;

// note that this runs at webapp startup as well as client-app startup, allowing dynamic 
// initialization, which we use to set up the JDBC Connection from runtime args
@Component
public class ClientRun implements CommandLineRunner {

	@Autowired
	private AdminService adminService;
	@Autowired
	private StudentService studentService;
	@Autowired
	DbDAO dbDAO;  // need access to finish initialization, termination of Connection
//	@Autowired
//	ApplicationContext ctx;  // used to print out beans (code commented out)

	@Override
	public void run(String... args) throws Exception {
		String dbUrl = null; // use H2 if all null
		String dbUser = null;
		String dbPassword = null;
		String app = "SystemTest"; // default to SystemTest
		System.out.println("Starting ClientRun, #args = " + args.length);
		if (args.length >= 1)
			app = args[0];
		else if (args.length >= 4) {
			dbUrl = args[0];
			dbUser = args[1];
			dbPassword = args[2];
			app = args[3];
		}
		// at this point, the beans are already set up--look at them--
//		 String[] beanNames = ctx.getBeanDefinitionNames();
//		 System.out.println("Seeing beans: ");
//	        Arrays.sort(beanNames);
//	        for (String beanName : beanNames) {
//	            System.out.println(beanName);
//	        }

		dbDAO.setupConnection(dbUrl, dbUser, dbPassword);  
		try {
			if (app.equals("SystemTest")) { // allow "mysql-SystemTest" for ex.
				System.out.println("Starting SystemTest from ClientRun");
				SystemTest st = new SystemTest(adminService, studentService);
				st.runSystemTest();
			} else if (app.equals("ShopAdmin")) {
				System.out.println("Starting ShopAdmin from ClientRun");
				ShopAdmin sa = new ShopAdmin(adminService);
				sa.runShopAdmin();
			} else if (app.equals("TakeOrder")) {
				System.out.println("Starting TakeOrder from ClientRun");
				TakeOrder ta = new TakeOrder(studentService);
				ta.runTakeOrder();
			} else {
				System.out.println(
						"app = " + app + ", not SystemTest, ShopAdmin or TakeOrder, so not running one of them");
				return;
			}
		} finally {
			dbDAO.close(); // close Connection
		}
	}
}
