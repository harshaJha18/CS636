package cs636.pizza.config;

/**
 * @author Betty O'Neil
 *
 * With Spring, we don't need to create the API objects in our code.
 * just annotate their classes and let Spring do the new's
 * 
 */

import java.io.PrintWriter;
import java.io.StringWriter;

public class PizzaSystemConfig {
	// for ease of testing, handle only a few rooms--
	public static final int NUM_OF_ROOMS = 10;

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

}
