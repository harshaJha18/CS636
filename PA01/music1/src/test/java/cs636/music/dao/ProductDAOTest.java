package cs636.music.dao;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs636.music.domain.Product;

public class ProductDAOTest {
	private DbDAO dbDAO;
	private ProductDAO productdao;

	@Before
	public void setup() throws SQLException {
		// we use H2, the DbDAO default db, as the db for testing
		// Note: need to load it first
		dbDAO = new DbDAO(null, null, null);
		dbDAO.initializeDb(); //  no users, etc.
		productdao = new ProductDAO(dbDAO);
	}
	
	@After
	public void tearDown() throws SQLException {
		dbDAO.close();
	}
	
	@Test
	public void testFindProductByCode() throws SQLException
	{
		Product p2 = productdao.findProductByCode("8601");
		assertTrue(1 == p2.getId());
	}
}