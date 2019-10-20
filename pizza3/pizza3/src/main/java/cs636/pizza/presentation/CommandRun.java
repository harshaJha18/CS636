package cs636.pizza.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cs636.pizza.presentation.clientserver.ShopAdmin;
import cs636.pizza.presentation.clientserver.TakeOrder;
import cs636.pizza.service.AdminService;
import cs636.pizza.service.StudentService;

// As a CommandLineRunner, this is called from SpringApplication.run (web or client case)
@Component
public class CommandRun implements CommandLineRunner {

	@Autowired
	private AdminService adminService;
	@Autowired
	private StudentService studentService;
	// for commented-out code to list all beans
//	@Autowired
//	ApplicationContext ctx;

	// note that this runs at webapp startup as well as client-app startup, allowing
	// dynamic initialization of the webapp if needed.
	@Override
	public void run(String... args) throws Exception {
		System.out.println("Starting CommandRun");
		if (args.length == 0 || args[0].contentEquals("web")) {
			return;  // could do webapp initialization here if needed
		}	
		String clientApp = args[0];

		// at this point, the beans are already set up--look at them--
//		 String[] beanNames = ctx.getBeanDefinitionNames();
//		 System.out.println("Seeing beans: ");
//	        Arrays.sort(beanNames);
//	        for (String beanName : beanNames) {
//	            System.out.println(beanName);
//	        }

		switch (clientApp) {
		case "SystemTest":
			System.out.println("Starting SystemTest from CommandRun");
			SystemTest st = new SystemTest(adminService, studentService);
			st.runSystemTest();
			break;
		case "TakeOrder":
			System.out.println("Starting TakeOrder from CommandRun");
			TakeOrder ta = new TakeOrder(studentService);
			ta.runTakeOrder();
			break;
		case "ShopAdmin":
			System.out.println("Starting ShopAdmin from CommandRun");
			ShopAdmin sa = new ShopAdmin(adminService);
			sa.runShopAdmin();
			break;
		default:
			System.out.println("Unknown client app: " + clientApp);
		}
	}
}
