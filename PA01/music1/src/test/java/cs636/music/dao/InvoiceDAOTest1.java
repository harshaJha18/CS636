package cs636.music.dao;
//Example JUnit4 test 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs636.music.domain.Invoice;
import cs636.music.domain.LineItem;
import cs636.music.domain.Product;
import cs636.music.domain.User;

public class InvoiceDAOTest1 {
	private DbDAO dbDAO;
	private LineItemDAO lineitemdao;
	private InvoiceDAO invoicedao;
	private ProductDAO productdao;
	private UserDAO userdao;
    private User user;  // set up in setup()

	@Before
	// each test runs in its own transaction, on same db setup
	public void setup() throws SQLException {
		// we use H2, the DbDAO default db, as the db for testing
		// Note: need to load it first
		dbDAO = new DbDAO(null, null, null);

		dbDAO.initializeDb(); //  no users, etc.
		userdao = new UserDAO(dbDAO);
		// Need a user to test Invoices
		user = new User();
		user.setEmailAddress("doe@joe.com");
		user.setFirstname("doe");
		user.setLastname("schmo");
		userdao.insertUser(user);
		productdao = new ProductDAO(dbDAO);
		lineitemdao = new LineItemDAO(dbDAO);
		invoicedao = new InvoiceDAO(dbDAO, lineitemdao, userdao, productdao);
	}

	@After
	public void tearDown() throws SQLException {
		dbDAO.close();
	}

	@Test
	public void testInsertInvoice() throws SQLException
	{
		Invoice i = makeInvoice(1);
		invoicedao.insertInvoice(i);
	}
	
	@Test
	public void testFindAllInvoices() throws SQLException {
		Invoice i = makeInvoice(1);
		invoicedao.insertInvoice(i);
		
		Set<Invoice> invoices = invoicedao.findAllInvoices();
		assertTrue(invoices.size()==1);
		assertEquals("doe", invoices.iterator().next().getUser().getFirstname());
	}
	
	// helper method to make a test invoice
	private Invoice makeInvoice(long id) throws SQLException {
		Product p = productdao.findProductByCode("8601");
		Invoice i = new Invoice();
		i.setInvoiceDate(new Date());
		i.setUser(user);
		i.setProcessed(false);
		i.setTotalAmount(new BigDecimal(10));
		LineItem li = new LineItem(id, p, i, 1);
		HashSet<LineItem> items = new HashSet<LineItem>();
		items.add(li);
		i.setLineItems(items);
		return i;
	}
}
