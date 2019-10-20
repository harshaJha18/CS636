package cs636.pizza.presentation;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.web.servlet.ServletComponentScan;
// This specifies where to look for @WebServlet servlets: SysTestServlet
@ServletComponentScan(basePackages = "cs636.pizza")
// This specifies where to look for @Components, ets.
@SpringBootApplication(scanBasePackages = { "cs636.pizza" })
public class SBApplication {

	public static void main(String[] args) {
		System.out.println("Starting SBApplication, #args = " + args.length);
		String appCase = null;  // web or SystemTest or ...
		SpringApplication app = new SpringApplication(SBApplication.class);
		if (args.length == 0) {
			appCase = "web";
		} else appCase = args[0];
		if (!appCase.equals("web")) {
			System.out.println("have arg " + appCase + " , assuming client execution");
			app.setBannerMode(Banner.Mode.OFF);
			app.setWebApplicationType(WebApplicationType.NONE); // don't start tomcat
		}
		app.run(args);  // execute CommandRun.run (the CommandLineRunner)
	}
}
