package cs636.pizza.presentation;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.dao.DbDAO;
import cs636.pizza.presentation.SystemTest;
import cs636.pizza.service.AdminService;
import cs636.pizza.service.StudentService;
// note that this runs at client-app startup, allowing dynamic initialization
// which we use to set JPA config from runtime args
@Component
public class ClientRun implements CommandLineRunner {

	@Autowired
	private AdminService adminService;
	@Autowired
	private StudentService studentService;
	@Autowired
	Environment environment; // to find active profile, like mysql-SystemTest
//	@Autowired
//	ApplicationContext ctx;
	@Autowired
	DbDAO dbDAO;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Starting ClientRun, #args = " + args.length);
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

		EntityManagerFactory emf = PizzaSystemConfig.configureJPA(dbUrl, dbUser, dbPassword);
		dbDAO.setEntityManagerFactory(emf);
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
			dbDAO.shutdown(); // hangs at end without this
		}
	}
}
