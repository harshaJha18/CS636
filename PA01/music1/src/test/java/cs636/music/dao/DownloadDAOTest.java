package cs636.music.dao;
//Example JUnit4 test 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs636.music.domain.Download;
import cs636.music.domain.Product;
import cs636.music.domain.User;

public class DownloadDAOTest {
	private DbDAO dbDAO;
	private DownloadDAO downloaddao;
	private ProductDAO productdao;
	private UserDAO userdao;
    private User user;  // set up in setup()

	// This executes before *each* test, so every test starts out the same way
	@Before
	public void setUp() throws SQLException {
		// we use H2, the DbDAO default db, as the db for testing
		// Note: need to load it first
		dbDAO = new DbDAO(null, null, null);
		dbDAO.initializeDb();  // no orders, toppings, sizes
		userdao = new UserDAO(dbDAO);
		// Need a user to test Downloads
		user = new User();
		user.setEmailAddress("doe@joe.com");
		user.setFirstname("doe");
		user.setLastname("schmo");
		userdao.insertUser(user);
		productdao = new ProductDAO(dbDAO);
		downloaddao = new DownloadDAO(dbDAO, userdao, productdao);
	}

	@After
	public void tearDown() throws Exception {
		dbDAO.close();
	}
	
	@Test
	public void testInsertDownload() throws SQLException
	{
		Product p = productdao.findProductByCode("8601");
		
		Download d = new Download();
		d.setDownloadDate(new Date());
		d.setUser(user);
		d.setTrack(p.getTracks().iterator().next());
		downloaddao.insertDownload(d);
	}
	
	@Test
	public void testFindAllDownloads() throws SQLException {
		Product p = productdao.findProductByCode("8601");
		
		Download d = new Download();
		d.setDownloadDate(new Date());
		d.setUser(user);
		d.setTrack(p.getTracks().iterator().next());
		downloaddao.insertDownload(d);
		
		Set<Download> downloads = downloaddao.findAllDownloads();
		assertTrue(downloads.size()==1);
		assertEquals("doe", downloads.iterator().next().getUser().getFirstname());
	}
}
