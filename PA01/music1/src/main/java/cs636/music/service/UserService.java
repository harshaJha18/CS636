package cs636.music.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cs636.music.dao.DownloadDAO;
import cs636.music.dao.InvoiceDAO;
import cs636.music.dao.ProductDAO;
import cs636.music.dao.UserDAO;
import cs636.music.domain.Cart;
import cs636.music.domain.CartItem;
import cs636.music.domain.Download;
import cs636.music.domain.Invoice;
import cs636.music.domain.LineItem;
import cs636.music.domain.Product;
import cs636.music.domain.Track;
import cs636.music.domain.User;
import cs636.music.service.ServiceException;
import cs636.music.service.data.CartItemData;
import cs636.music.service.data.InvoiceData;
import cs636.music.service.data.UserData;

/**
 * 
 * Provide user level services to user app through accessing DAOs
 * 
 * @author Chung-Hsien (Jacky) Yu
 */
public class UserService {

	private DownloadDAO downloadDb;
	private InvoiceDAO invoiceDb;
	private ProductDAO prodDb;
	private UserDAO userDb;

	/**
	 * construct a user service provider
	 * 
	 * @param productDao
	 * @param userDao
	 * @param downloadDao
	 * @param lineItemDao
	 * @param invoiceDao
	 */
	public UserService(ProductDAO productDao, UserDAO userDao,
			DownloadDAO downloadDao, InvoiceDAO invoiceDao) {
		downloadDb = downloadDao;
		invoiceDb = invoiceDao;
		prodDb = productDao;
		userDb = userDao;;
	}

	/**
	 * Getting list of all products
	 * API design: Could return Set<Long> for productIds
	 * 
	 * @return list of all product
	 * @throws ServiceException
	 */
	public Set<Product> getProductList() throws ServiceException {
		Set<Product> prodlist;
		try {
			prodlist = prodDb.findAllProducts();
		} catch (SQLException e) {
			throw new ServiceException("Can't find product list in db: ", e);
		}
		return prodlist;
	}

	/**
	 * Create a new cart
	 * 
	 * @return the cart
	 */
	public Cart createCart() {
		return new Cart();
	}
	
	/**
	 * Get cart info
	 * 
	 * @return the cart's content
	 * API design: OK to use Set<CartItem> here for return value, but then need getProductById in service API
	 *     because CartItem only has the product id, not the whole Product 
	 */
	public Set<CartItemData> getCartInfo(Cart cart) throws ServiceException {
		Set<CartItemData> items = new HashSet<CartItemData>();
		try {
			for (CartItem item : cart.getItems()) {
				Product product = prodDb.findProductByPID(item.getProductId());
				CartItemData itemData = new CartItemData(item.getProductId(), item.getQuantity(), product.getCode(),
						product.getDescription(), product.getPrice());
				items.add(itemData);
			}
		} catch (Exception e) {
			throw new ServiceException("Can't find product in cart: ", e);
		}
		return items;

	}
	/**
	 * Add a product to the cart. If the product is already in the cart, add
	 * quantity. Otherwise, insert a new line item.
	 * API design: first argument can be productId here (good choice actually)
	 * 
	 * @param prod
	 * @param cart
	 * @param quantity
	 */
	public void addItemtoCart(Product prod, Cart cart, int quantity) {
		CartItem item = cart.findItem(prod.getId());
		if (item != null) { // product is already in the cart, add quantity
			int qty = item.getQuantity();
			item.setQuantity(qty + quantity);
		} else { // not in the cart, add new item with quantity
			item = new CartItem(prod.getId(), quantity);
			// cart.addItem(item);
			cart.getItems().add(item);
		}
	}

	/**
	 * Change the quantity of one item. If quantity <= 0 then delete this item.
     * API design: first argument can be productId here (good choice actually)
	 * @param prod
	 * @param cart
	 * @param quantity
	 */
	public void changeCart(Product prod, Cart cart, int quantity) {
		CartItem item = cart.findItem(prod.getId());
		if (item != null) {
			if (quantity > 0) {
				item.setQuantity(quantity);
			} else { // if the quantity was changed to 0 or less, remove the
						// product from cart
				cart.removeItem(prod.getId());
			}
		}
	}

	/**
	 * Remove a product item from the cart
	 * API design: first argument can be productId here (good choice actually)
	 * 
	 * @param prod
	 * @param cart
	 */
	public void removeCartItem(Product prod, Cart cart) {
		CartItem item = cart.findItem(prod.getId());
		if (item != null) {
			cart.removeItem(prod.getId());
		}
	}

	/**
	 * Register user if the email does not exist in the db
	 * 
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @throws ServiceException
	 */
	public void registerUser(String firstname, String lastname, String email)
			throws ServiceException {
		User user = null;

		try {
			user = userDb.findUserByEmail(email);
			if (user == null) { // this user has not registered yet
				user = new User();
				user.setFirstname(firstname);
				user.setLastname(lastname);
				user.setEmailAddress(email);
				userDb.insertUser(user);
			}
		} catch (SQLException e) {
			throw new ServiceException("Error while registering user: ", e);
		}
	}

	/**
	 * Get user info by given email address
	 * Note: here we assume the presentation layer received the user email
	 * string (the argument here) from the user.
	 * 
	 * @param email
	 * @return the user info found, return null if not found
	 * @throws ServiceException
	 */
	public UserData getUserInfo(String email) throws ServiceException {
		User user = null;
		UserData user1 = null;
		try {
			user = userDb.findUserByEmail(email);
			user1 = new UserData(user);
		} catch (SQLException e) {
			throw new ServiceException("Error while getting user info: ", e);
		}
		return user1;
	}

	/**
	 * Return a product info by given product code
	 * 
	 * @param prodCode
	 *            product code
	 * @return the product info
	 * @throws ServiceException
	 */
	public Product getProduct(String prodCode) throws ServiceException {
		Product prd = null;
		try {
			prd = prodDb.findProductByCode(prodCode);
		} catch (SQLException e) {
			throw new ServiceException("Error while registering user: ", e);
		}
		return prd;
	}

	/**
	 * Check out the cart from the user order and then generate an invoice for
	 * this order. Empty the cart after
	 * API design: --OK but unusual to use UserData as second argument here: userId is better
	 *    --not expected to use a User object as second argument, since it is a
	 *    persistent object. However, it is immutable in this project, so can be allowed.
	 * 
	 * @param cart
	 * @param user id
	 * @return new invoice
	 * @throws ServiceException
	 */
	public InvoiceData checkout(Cart cart, long userId) throws ServiceException {
		Invoice invoice = null;
		try {
			User user = userDb.findUserByID(userId);
			invoice = new Invoice(-1, user, new Date(), false, null, null);
			Set<LineItem> lineItems = new HashSet<LineItem>();
			for (CartItem item : cart.getItems()) {  
				Product prod = prodDb.findProductByPID(item.getProductId());
				LineItem li = new LineItem(prod, invoice, item.getQuantity());
				lineItems.add(li);
			}
			invoice.setLineItems(lineItems);
			invoice.setTotalAmount(invoice.calculateTotal());
			invoiceDb.insertInvoice(invoice);
		} catch (SQLException e) {
			throw new ServiceException("Can't check out: ", e);
		}
		cart.clear();
		return new InvoiceData(invoice);
	}

	/**
	 * Add one download history, record the user and track
	 * API design: See checkout for discussion of alternatives to userId as first argument here.
	 * 
	 * @param userId
	 *            id of user who downloaded the track
	 * @param track
	 *            the track which was downloaded
	 * @throws ServiceException
	 */
	public void addDownload(long userId, Track track) throws ServiceException {
		try {
			Download download = new Download();
			User user = userDb.findUserByID(userId);
			download.setUser(user);
			download.setTrack(track);
			downloadDb.insertDownload(download);  // let database determine timestamp
		} catch (SQLException e) {
			throw new ServiceException("Can't add download: ", e);
		}
	}
}
