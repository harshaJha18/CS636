package cs636.pizza.dao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "cs636.pizza" })

public class AppConfig {
	  static{
	        System.out.println("AppConfig loaded");
	    }
}

