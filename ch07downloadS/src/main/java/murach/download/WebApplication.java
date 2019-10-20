package murach.download;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// the following works to start SysTestServlet, which can run SystemTest
// by GET to /servlet/SystemTestServlet
import org.springframework.boot.web.servlet.ServletComponentScan;
@ServletComponentScan(basePackages = "murach")
// This specifies where to look for @Components, etc.
@SpringBootApplication(scanBasePackages = { "murach" })
public class WebApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebApplication.class, args);
    }
}