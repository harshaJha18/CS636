package cs636.music.service;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import cs636.music.dao.AdminDAO;
import cs636.music.dao.DbDAO;
import cs636.music.dao.DownloadDAO;
import cs636.music.dao.InvoiceDAO;
import cs636.music.domain.Download;
import cs636.music.domain.Invoice;
import cs636.music.service.data.DownloadData;
import cs636.music.service.data.InvoiceData;

/**
 * 
 * Provide admin level services to user app through accessing DAOs 
 * @author Chung-Hsien (Jacky) Yu
 */
public class AdminService {
	
	private DbDAO db;
	private DownloadDAO downloadDb;
	private InvoiceDAO invoiceDb;
	private AdminDAO adminDb;
	
	/**
	 * construct a admin service provider 
	 * @param dbDao
	 * @param downloadDao
	 * @param invoiceDao
	 */
	public AdminService(DbDAO dbDao, DownloadDAO downloadDao ,InvoiceDAO invoiceDao, AdminDAO adminDao) {
		db = dbDao;
		downloadDb = downloadDao;
		invoiceDb = invoiceDao;
		adminDb = adminDao;
		
	}
	
	/**
	 * Clean all user table, not product and system table to empty
	 * and then set the index numbers back to 1
	 * @throws ServiceException
	 */
	public void initializeDB()throws ServiceException {
		try {
			db.initializeDb();
		} catch (SQLException e)
		{
			throw new ServiceException("Can't initialize DB: ", e);
		}	
	}
	
	/**
	 * process the invoice
	 * @param invoiceId
	 * @throws ServiceException
	 */
	public void processInvoice(long invoiceId) throws ServiceException {
		try {
			Invoice i = invoiceDb.findInvoice(invoiceId);
			invoiceDb.updateInvoice(i);
		} catch (SQLException e)
		{
			throw new ServiceException("Can't process invoice: ", e);
		}
	}

	/**
	 * Get a list of all invoices
	 * @return list of all invoices in InvoiceData objects
	 * @throws ServiceException
	 */
	public Set<InvoiceData> getListofInvoices() throws ServiceException {
		Set<Invoice> invoices = null;
		try {
			invoices = invoiceDb.findAllInvoices();
		} catch (SQLException e) {
			throw new ServiceException("Can't find invoice list in DB: ", e);
		}
		Set<InvoiceData> invoices1 = new HashSet<InvoiceData>();
		for (Invoice i : invoices) {
			invoices1.add(new InvoiceData(i));
		}
		return invoices1;

	}

	/**
	 * Get a list of all unprocessed invoices
	 * @return list of all unprocessed invoices in InvoiceData objects
	 * @throws ServiceException
	 */
	public Set<InvoiceData> getListofUnprocessedInvoices() throws ServiceException {
		Set<Invoice> invoices = null;
		try {
			invoices = invoiceDb.findAllUnprocessedInvoices();
		} catch (SQLException e) {
			throw new ServiceException("Can't find unprocessed invoice list in DB: ", e);
		}
		Set<InvoiceData> invoices1 = new HashSet<InvoiceData>();
		for (Invoice i : invoices) {
			invoices1.add(new InvoiceData(i));
		}
		return invoices1;
	}
	
	/**
	 * Get a list of all downloads
	 * @return list of all downloads as DownloadData objects
	 * @throws ServiceException
	 */
	public Set<DownloadData> getListofDownloads() throws ServiceException {
		
		Set<Download> downloads = null;
		try {
			downloads =  downloadDb.findAllDownloads();
		} catch (SQLException e)
		{
			throw new ServiceException("Can't find download list: ", e);
		}
		Set<DownloadData> downloads1 = new HashSet<DownloadData>();
		for (Download d: downloads) {
			downloads1.add(new DownloadData(d));
		}
		return downloads1;
	}
	
	
	/**
	 * Check login user
	 * @param username
	 * @param password
	 * @return true if useranme and password exist, otherwise return false
	 * @throws ServiceException
	 */
	public Boolean checkLogin(String username,String password) throws ServiceException {
		try {
			return adminDb.findAdminUser(username, password);
		} catch (SQLException e)
		{
			throw new ServiceException("Check login error: ", e);
		}
	}
	
}
