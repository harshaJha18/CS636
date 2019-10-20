package murach.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
@ServletComponentScan(basePackages = "murach.email")
// This annotation is needed for getting embedded tomcat to run
// This app has no @Components (Murach uses static API methods
// in classes that don't need singletons)
@SpringBootApplication()
public class WebApplication {
	// run the embedded tomcat
    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebApplication.class, args);
    }
}
