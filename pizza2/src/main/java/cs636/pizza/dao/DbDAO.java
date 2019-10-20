package cs636.pizza.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import static cs636.pizza.dao.DBConstants.*;

@Repository
public class DbDAO {
	// filled in by setter called from ClientRun to finish bean initialization
	private EntityManagerFactory emf;
	
	// the current transaction's em: it can be a field here because this
	// app is single-threaded. When we turn this into a web app, we
	// won't be able to have such a field because multiple threads will
	// each need their own em.
	private EntityManager em;

	public EntityManager getEM() {
		return em;
	}
	
	// called during initialization (in ClientRun or JUnit setup()) to supply emf
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public DbDAO(String dbUrl, String user, String password) {
	}

	// for DAO unit tests
	public DbDAO() {
	}

	public void initializeDb() {
		// drop tables with FK cols before the tables they refer to
		clearTable(PIZZA_TOPPING_TABLE);
		clearTable(ORDER_TABLE);
		clearTable(PIZZA_SIZE_TABLE);
		clearTable(MENU_TOPPING_TABLE);
		clearTable(MENU_SIZE_TABLE);
		clearTable(SYS_TABLE);
		clearTable(PIZZA_ID_GEN_TABLE);

		initSysTable();
		initIdGenTable();
	}

	// We can use direct SQL for DB setup easily as follows.
	// Any SQLException is handled by JPA, marking the
	// transaction as rollback-only, and then JPA throws a
	// javax.persistence.PersistenceException
	private int clearTable(String tableName) {
		Query q = em.createNativeQuery("delete from " + tableName);
		int n = q.executeUpdate(); // SQL of update shows in FINE logging
		// System.out.println("deleted " + n + " rows from " + tableName);
		return n;
	}

	// In pizza2, we're only using the last column (current_day) of this table
	// but we keep the others so we can run pizza1 on the same database

	private int initSysTable() {
		// System.out.println("inserting row (1,1,1,1,1,1) into " + SYS_TABLE);
		Query q = em.createNativeQuery("insert into " + SYS_TABLE + " values (1,1,1,1,1,1)");
		int n = q.executeUpdate();
		// System.out.println("inserted " + n + " rows into " + SYS_TABLE);
		return n;
	}

	// reinitialize id gen table so
	// as to get same sequence of ids on each re-init'd run
	private void initIdGenTable() {
		for (int i = 0; i < PIZZA_GEN_NAMES.length; i++)
			initIdGenTableOneRow(PIZZA_GEN_NAMES[i]);
	}

	private int initIdGenTableOneRow(String genName) {
		// System.out.println("inserting row (" + genName + ", 0) into " +
		// PIZZA_ID_GEN_TABLE);
		Query q = em.createNativeQuery("insert into " + PIZZA_ID_GEN_TABLE + " values ('" + genName + "', 0)");
		int n = q.executeUpdate();
		// System.out.println("inserted " + n + " rows into " + PIZZA_ID_GEN_TABLE);
		return n;
	}

	public void shutdown() {
		if (emf != null && emf.isOpen())
			emf.close();
	}

	public void startTransaction() {
		em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	}

	public void commitTransaction() {
		// the commit call can throw, and then the caller needs to rollback
		em.getTransaction().commit();
		// We are using an application-managed entity manager, so we need
		// to explicitly close it to release its resources.
		// See Keith & Schincariol, pg. 138, first paragraph.
		// By closing the em at the end of the transaction, we are
		// following the pattern of transaction-scoped entity managers
		// used in EJBs by default.
		em.close(); // this causes the entities to become detached
	}

	public void rollbackTransaction() {
		try {
			em.getTransaction().rollback();
		} finally {
			em.close();
		}
	}

	// Exceptions occurring in JPA code are almost always fatal to the
	// EntityManager context, meaning that we need to rollback the transaction
	// (and also close the EntityManager in our setup) and start over
	// or fail the action. An exception to this rule is the NoResultException
	// from the method singleResult()--it's OK to handle the exception and
	// continue the EntityManager/transaction after that particular exception.
	// If the caller has already seen an exception, it probably
	// doesn't want to handle a failing rollback, so it can use this.
	// Then the caller should issue its own exception based on the
	// original exception.
	public void rollbackAfterException() {
		try {
			rollbackTransaction();
		} catch (Exception e) {
			// discard secondary exception--probably server can't be reached
		}
	}


}
