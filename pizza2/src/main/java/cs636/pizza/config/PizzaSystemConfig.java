
package cs636.pizza.config;

// System configuration for JPA using a single DB. See persistence.xml
// for its configuration. The JDBC properties are added at runtime.
// The API objects are set up by Spring in this project

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import static cs636.pizza.dao.DBConstants.H2_DRIVER;
import static cs636.pizza.dao.DBConstants.H2_URL;
import static cs636.pizza.dao.DBConstants.MYSQL_DRIVER;
import static cs636.pizza.dao.DBConstants.ORACLE_DRIVER;


public class PizzaSystemConfig {
	// for ease of testing, handle only a few rooms--
	public static final int NUM_OF_ROOMS = 10;

	// The basic configuration information is read from the persistence.xml file
	// on the classpath. Here we set the further properties like user and password
	// based on which DB we are connecting to. These could easily be put in the
	// persistence.xml for one DB, but we are using 3 DBs, so we would have to
	// replace the persistence.xml file each time we switched databases.
	// This may throw a RuntimeException.
	public static EntityManagerFactory configureJPA(String dbUrl, String user, String password) {
		// null values mean use H2--
		System.out.println("NOTE: configureJPA called with dbUrl" + dbUrl);
		if (dbUrl == null) {
			dbUrl = H2_URL; // default to H2, an embedded DB
			user = "test";
			password = "";
		}
		String dbDriverName;
		// Hibernate can mitigate SQL variations between products if it knows which
		// "dialect" of SQL is being used
		// Also, setting the dialect avoids a misleading error message about missing
		// dialect when the real problem is that the connect failed (e.g. to an 
		// already-connected H2 DB)
		String dbHibernateDialect;
		if (dbUrl.contains("mysql")) {
			dbDriverName = MYSQL_DRIVER;
			dbHibernateDialect = "org.hibernate.dialect.MySQLDialect";
		} else if (dbUrl.contains("oracle")) {
			dbDriverName = ORACLE_DRIVER;
			dbHibernateDialect = "org.hibernate.dialect.Oracle10gDialect";
		} else if (dbUrl.contains("h2")) {
			dbDriverName = H2_DRIVER;
			dbHibernateDialect = "org.hibernate.dialect.H2Dialect";
		} else
			throw new RuntimeException("Unknown DB URL " + dbUrl);

		Map<String, String> addedOrOverridenProperties = new HashMap<String, String>();
		addedOrOverridenProperties.put("javax.persistence.jdbc.driver", dbDriverName);
		addedOrOverridenProperties.put("hibernate.dialect", dbHibernateDialect);
		addedOrOverridenProperties.put("javax.persistence.jdbc.url", dbUrl);
		addedOrOverridenProperties.put("javax.persistence.jdbc.user", user);
		addedOrOverridenProperties.put("javax.persistence.jdbc.password", password);
		addedOrOverridenProperties.put("javax.persistence.jdbc.password", password);
		return Persistence.createEntityManagerFactory("pizza2hib", addedOrOverridenProperties);
	}	
	// Try a test EM session to make sure needed functionality is there
	public static void testJPA(EntityManagerFactory emf) {
		try {
			System.out.println("starting testJPA");
			EntityManager em = emf.createEntityManager();
			System.out.println("testEMF: Got an EM");
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			// dig down in software to get the actual JDBC Connection--

			tx.commit();
			em.close();
		} catch (Exception e) {
			System.out.println(" Exception trying to set up a transaction: " + e + "\n Continuing...");
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
}
