package cs636.music.dao;
//Example JUnit4 test 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import cs636.music.domain.Download;
import cs636.music.domain.Product;

@RunWith(SpringRunner.class)
//Needed to handle DataSource config
@JdbcTest
// To be minimalistic, configure only the needed beans
// -- here CatalogDAO needs DbUtils
@ContextConfiguration(classes= {CatalogDbDAO.class,  DbUtils.class, DownloadDAO.class,ProductDAO.class})
// Use application-test.properties in src/main/resources instead of application.properties
@ActiveProfiles("test")
public class DownloadDAOTest {
	@Autowired
	private CatalogDbDAO catalogDbDAO;
	@Autowired
	private DownloadDAO downloaddao;
	@Autowired
	private ProductDAO productdao;
	private static String FAKE_USER_EMAIL_ADDRESS = "foo@bar.com";

	@Before
	// each test runs in its own transaction, on same db setup
	public void setup() throws SQLException {
		catalogDbDAO.initializeDb(); 
	}

	@After
	public void tearDown() {
	}
	
	@Test
	public void testInsertDownload() throws SQLException
	{
		Connection connection = catalogDbDAO.startTransaction();
		Product p = productdao.findProductByCode(connection, "8601");
		
		Download d = new Download();
		d.setDownloadDate(new Date());
		d.setEmailAddress(FAKE_USER_EMAIL_ADDRESS);
		d.setTrack(p.getTracks().iterator().next());
		
		downloaddao.insertDownload(connection, d);
		catalogDbDAO.commitTransaction(connection);
	}
	
	@Test
	public void testFindAllDownloads() throws SQLException {
		Connection connection = catalogDbDAO.startTransaction();
		Product p = productdao.findProductByCode(connection, "8601");
		
		Download d = new Download();
		d.setDownloadDate(new Date());
		d.setEmailAddress(FAKE_USER_EMAIL_ADDRESS);
		d.setTrack(p.getTracks().iterator().next());
		downloaddao.insertDownload(connection, d);
		
		Set<Download> downloads = downloaddao.findAllDownloads(connection);
		assertTrue(downloads.size()==1);
		assertEquals(FAKE_USER_EMAIL_ADDRESS, downloads.iterator().next().getEmailAddress());
		catalogDbDAO.commitTransaction(connection);

	}
}
