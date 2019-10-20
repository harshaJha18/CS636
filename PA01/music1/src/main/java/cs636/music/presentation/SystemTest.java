
package cs636.music.presentation;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import cs636.music.config.MusicSystemConfig;
import cs636.music.domain.Cart;
import cs636.music.domain.Product;
import cs636.music.domain.Track;
import cs636.music.service.AdminService;
import cs636.music.service.ServiceException;
import cs636.music.service.UserService;
import cs636.music.service.data.CartItemData;
import cs636.music.service.data.DownloadData;
import cs636.music.service.data.InvoiceData;
import cs636.music.service.data.UserData;


/**
 * 
 *         This class tests the system.
 */
public class SystemTest {

	private AdminService adminService;
	private UserService userService;
	private String inFile;
	private Cart cart;

	public SystemTest(String inFile, String dbUrl, String usr, String passwd)
			throws Exception {
		this.inFile = inFile;
		System.out
		.println("running SystemTest, calling config with: "+dbUrl+" "+usr+" "+passwd);	
		MusicSystemConfig.configureServices(dbUrl, usr, passwd);
		adminService = MusicSystemConfig.getAdminService();
		userService = MusicSystemConfig.getUserService();
	}

	public static void main(String[] args) {
		String inFile = null;
		String dbUrl = null;
		String usr = null;
		String pw = null;
		if (args.length == 0) {  // no args: run on H2 with test.dat
			inFile = "test.dat";
			// leave dbUrl null, for H2
		} else if (args.length == 3) {
			inFile = "test.dat";
			dbUrl = args[0];
			usr = args[1];
			pw = args[2];
		} else if (args.length == 4) {
			inFile = args[0];
			dbUrl = args[1];
			usr = args[2];
			pw = args[3];	
		} else {
			System.out
					.println("usage:java [<inputFile>] <dbURL> <user> <passwd> ");
			return;
		}
		try {
			SystemTest test = new SystemTest(inFile, dbUrl, usr, pw);
			test.run();
			MusicSystemConfig.shutdownServices();
			System.out.println("Run complete, exiting");
		} catch (Exception e) {
			System.out.println("Error in run of SystemTest: " );
			System.out.println(MusicSystemConfig.exceptionReport(e));
		}
	}

	public void run() throws IOException, ServiceException {
		String command = null;
		if (inFile== null)
			inFile = "test.dat";
		Scanner in = new Scanner(new File(inFile));
		while ((command = getNextCommand(in)) != null) {
			System.out.println("\n\n*************" + command
					+ "***************\n");
			if (command.equalsIgnoreCase("i")) { // admin init db
				System.out.println("Initializing system");
				this.adminService.initializeDB();
			} else if (command.equalsIgnoreCase("gp")) // get list of CDs
			{
				Set<Product> cdList = userService.getProductList();
				if (cdList != null)
					PresentationUtils.displayCDCatlog(cdList, System.out);

			} else if (command.startsWith("gui")) { // get info on user
				String usr = getTokens(command)[1];
				UserData user = userService.getUserInfo(usr);
				if (user == null)
					System.out.println("\nNo such user" + usr +"\n");
				else
					PresentationUtils.displayUserInfo(user, System.out);
			} else if (command.startsWith("gpi")) { // get info for product
				String productCode = getTokens(command)[1];
				Product product = userService.getProduct(productCode);
				if (product == null)
					System.out.println("\nNo such product\n");
				else
					PresentationUtils.displayProductInfo(product, System.out);
			} else if (command.startsWith("ureg")) { // ureg fname lname email
				String userInfo[] = getTokens(command); // whitespace delim.
														// tokens
				System.out.println("Registering user: " + 
						userInfo[1] + " " + userInfo[2] + " " + userInfo[3]);
				userService.registerUser(userInfo[1], userInfo[2], userInfo[3]);
			} else if (command.startsWith("gti")) {
				// gti prodcode:  list track info for CD
				String productCode = getTokens(command)[1];
				Product product = userService.getProduct(productCode);
				if (product == null)
					System.out.println("\nNo such product\n");
				else
					PresentationUtils.displayTracks(product, System.out);

			} else if (command.startsWith("dl")) {
				// record download by user x of product y (some track)
				String params[] = getTokens(command);
				String userEmail = params[1];
				String productCode = params[2];
				int tracknum = Integer.parseInt(params[3]);
				Product product = userService.getProduct(productCode);
				if (product == null)
					System.out.println("\nNo such product\n");
				else {
					Set<Track> tracks = product.getTracks();
					//Track track0 = tracks.iterator().next();
					UserData user = userService.getUserInfo(userEmail);
					if (user == null)
						System.out.println("\nNo such user\n");
					else {
						Track track0 = null;
						for (Track track: tracks ) {
							if (track.getTrackNumber() == tracknum){
								track0 = track;
							}
						}
						if ( track0 != null){
							System.out.println("Recording download for user");
							userService.addDownload(user.getId(), track0);
						} else {
							System.out.println("\nNo such track\n");
						}
					}
				}
			} else if (command.startsWith("cc")) { // create cart
				cart = userService.createCart();
				System.out.println("\n cart created ");

			} else if (command.startsWith("sc")) { // show cart
				System.out.println("\n Now displaying Cart...");
				Set<CartItemData> items = userService.getCartInfo(cart);
				PresentationUtils.displayCart(items, System.out);

			} else if (command.startsWith("co")) { // checkout userid
				String params[] = getTokens(command);
				UserData user = userService.getUserInfo(params[1]);
				if (user == null)
					System.out.println("\nNo such user\n");
				else {
					InvoiceData invoice = userService.checkout(cart, user.getId());
					System.out.println("\n CDs Ordered...");
					PresentationUtils.displayInvoice(invoice, System.out);
					System.out.println();
				}

			} else if (command.startsWith("addli")) { // add to cart

				String params[] = getTokens(command);
				Product product = userService.getProduct(params[1]);
				if (product == null)
					System.out.println("\nNo such product\n");
				else {
					userService.addItemtoCart(product, cart, 1);
					System.out.println("\n Added to Cart..");
				}
			} else if (command.startsWith("setproc")) // process invoice
			{
				int params[] = getIntTokens(command);
				this.adminService.processInvoice(params[1]);
			} else if (command.equalsIgnoreCase("gi")) // get list of invoices
			{
				Set<InvoiceData> inv = adminService.getListofInvoices();
				PresentationUtils.displayInvoices(inv, System.out);
			} else if (command.startsWith("gd")) // get list of downloads
			{
				Set<DownloadData> dList = adminService.getListofDownloads();
				PresentationUtils.downloadReport(dList, System.out);
			} else
				System.out.println("Invalid Command: " + command);
			System.out.println("----OK");
		}
		in.close();
	}

	// Return line or null if at end of file
	public String getNextCommand(Scanner in) throws IOException {
		String line = null;
		try {
			line = in.nextLine();
		} catch (NoSuchElementException e) { } // leave line null
		return (line != null) ? line.trim() : line;
	}
		
	// use powerful but somewhat mysterious split method of String
	private String[] getTokens(String command) {
		return command.split("\\s+"); // white space
	}

	private int[] getIntTokens(String command) {
		String tokens[] = getTokens(command);
		int returnValue[] = new int[tokens.length];
		for (int i = 1; i < tokens.length; i++)
			// skipping 0th, not an int
			returnValue[i] = Integer.parseInt(tokens[i]);
		return returnValue;
	}

}
