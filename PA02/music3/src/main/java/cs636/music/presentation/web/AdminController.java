package cs636.music.presentation.web;

import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cs636.music.config.MusicSystemConfig;
import cs636.music.service.CatalogService;
import cs636.music.service.SalesService;
import cs636.music.service.ServiceException;
import cs636.music.service.data.DownloadData;
import cs636.music.service.data.InvoiceData;


@Controller
public class AdminController {

	@Autowired
	private SalesService salesService;
	@Autowired
	private CatalogService catalogService; // for Downloads

	private static final String ADMIN_BASE_URL = "/adminController/";
	private static final String ADMIN_JSP_DIR = "/WEB-INF/admin/";

	@RequestMapping(ADMIN_BASE_URL + "adminWelcome.html")
	public String handleAdminLogin(Model model, @RequestParam(required = false) String username,
			@RequestParam(required = false) String password, HttpServletRequest request) throws ServletException {
		String uname = (String) request.getSession().getAttribute("adminUser");
		if (uname != null) {
			return ADMIN_JSP_DIR + "adminMenu"; // already logged in
		}

		if (username != null && password != null) {
			try {
				Boolean adminExistence = salesService.checkAdminLogin(username, password);
				// Success: Need to transfer the admin to the Admin Menu page
				// Bad credentials: Need to make the admin stay at the same login page.
				// Inform user 'Invalid Credentials'.
				if (adminExistence) {
					// save uname as session variable
					request.getSession().setAttribute("adminUser", username);
					return ADMIN_JSP_DIR + "adminMenu";
				} else {
					request.setAttribute("error", "Invalid Credentials");
					return ADMIN_JSP_DIR + "adminLogin";
				}
			} catch (ServiceException e) {
				String error = "Error in checking credentials: " + e;
				request.setAttribute("error", error);
				return ADMIN_JSP_DIR + "error";
			}
		}
		// missing or incomplete username/password: show login page
		else {
			return ADMIN_JSP_DIR + "adminLogin";
		}
	}
	
	@RequestMapping(ADMIN_BASE_URL+"listVariables.html")
	public String listVariables(Model model) {	
		String url = ADMIN_JSP_DIR + "listVariables";
		return url;
	}
	
	@RequestMapping(ADMIN_BASE_URL + "logout.html")
	public String logout(Model model, HttpServletRequest request) {
		request.getSession().invalidate(); // drop session
		String url = ADMIN_JSP_DIR + "logout";
		return url;
	}

	@RequestMapping(ADMIN_BASE_URL + "processInvoices.html")
	public String processInvoices(Model model, HttpServletRequest request) throws ServletException {
		if (!checkAdmin(request))
			return "forward:" + ADMIN_BASE_URL + "adminWelcome.html";
		String url = null;
		try {
			Set<InvoiceData> pendingInvoices = salesService.getListofUnprocessedInvoices();
			model.addAttribute("unProcessedInvoices", pendingInvoices);
			System.out.println("#pendingInvoices: " + pendingInvoices.size());
			return ADMIN_JSP_DIR + "pendingInvoices";
		} catch (ServiceException e) {
			String error = "Error: " + e;
			model.addAttribute("error", error);
			url = ADMIN_JSP_DIR + "error";
		}
		return url;
	}

	@RequestMapping(ADMIN_BASE_URL + "initDB.html")
	public String initializeDB(Model model, HttpServletRequest request) throws ServletException {
		if (!checkAdmin(request))
			return "forward:" + ADMIN_BASE_URL + "adminWelcome.html";

		String info = null;
		try {
			salesService.initializeDB();
			catalogService.initializeDB();
			info = "success";
		} catch (ServiceException e) {
			info = "failed: " + MusicSystemConfig.exceptionReport(e);
		}
		model.addAttribute("info", info);
		String url = ADMIN_JSP_DIR + "initializeDB";
		return url;
	}

	@RequestMapping(ADMIN_BASE_URL + "displayReports.html")
	public String displayReports(Model model, HttpServletRequest request) throws ServletException {
		if (!checkAdmin(request))
			return "forward:" + ADMIN_BASE_URL + "adminWelcome.html";

		String url = null;
		String error = null;
		try {
			Set<DownloadData> downloadReport = catalogService.getListofDownloads();
			model.addAttribute("downloads", downloadReport);
			Set<InvoiceData> invoiceReport = salesService.getListofInvoices();
			model.addAttribute("invoices", invoiceReport);
			url = ADMIN_JSP_DIR + "reports";
		} catch (ServiceException e) {
			error = "Error: " + e;
			model.addAttribute("error", error);
			url = ADMIN_JSP_DIR + "error";
		}
		return url;
	}

	@RequestMapping(ADMIN_BASE_URL + "processInvoice.html")
	public String processInvoice(Model model, @RequestParam() long invoiceId, HttpServletRequest request)
			throws ServletException {
		if (!checkAdmin(request))
			return "forward:" + ADMIN_BASE_URL + "adminWelcome.html";
		String error = null;
		System.out.println(" Processing invoice " + invoiceId + " .....");
		try {
			salesService.processInvoice(invoiceId);
		} catch (ServiceException e) {
			error = "Error: " + e;
			model.addAttribute("error", error);
			return ADMIN_JSP_DIR + "error";
		}
		return "forward:/adminController/processInvoices.html";
	}

	boolean checkAdmin(HttpServletRequest request) {
		boolean isAdmin = (request.getSession().getAttribute("adminUser") != null);
		return isAdmin;
	}
}
