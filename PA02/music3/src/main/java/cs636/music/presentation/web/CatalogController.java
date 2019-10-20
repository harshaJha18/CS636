package cs636.music.presentation.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import cs636.music.domain.Cart;
import cs636.music.domain.Product;
import cs636.music.domain.Track;
import cs636.music.presentation.client.PresentationUtils;
import cs636.music.service.CatalogService;
import cs636.music.service.ServiceException;
import cs636.music.service.data.CartItemData;
import cs636.music.service.data.UserData;

@Controller
public class CatalogController {
	@Autowired
	private CatalogService catalogService;

	// String constants for URL's
	private static final String WELCOME_URL = "/welcome.html";
	private static final String WELCOME_VIEW = "/welcome";
	private static final String USER_WELCOME_URL = "/userWelcome.html";
	private static final String CATALOG_URL = "/catalog.html";
	private static final String CATALOG_VIEW = "/WEB-INF/jsp/catalog";
	private static final String CART_URL = "/cart.html";
	private static final String CART_VIEW = "/WEB-INF/jsp/cart";
	private static final String PRODUCT_URL = "/product.html";
	private static final String PRODUCT_VIEW = "/WEB-INF/jsp/product";
	private static final String LISTEN_URL = "/listen.html";
	private static final String SOUND_VIEW = "/WEB-INF/jsp/sound";
	private static final String DOWNLOAD_URL = "/download.html"; // download.html didn't work
	private static final String REGISTER_FORM_VIEW = "/WEB-INF/jsp/registerForm";

	@RequestMapping(WELCOME_URL)
	public String handleWelcome() {
		return WELCOME_VIEW;
	}

	@RequestMapping(CATALOG_URL)
	public String catalogController(Model model, HttpServletRequest request) throws ServletException {
		if (!SalesController.checkUser(request))
			return "forward:" + USER_WELCOME_URL;
		List<Product> products = new ArrayList<Product>();
		try {
			products = new ArrayList<Product>(catalogService.getProductList());
		} catch (ServiceException e) {
			System.out.println("CatalogController: " + e);
			throw new ServletException(e);
		}
		model.addAttribute("products", products);
		return CATALOG_VIEW;
	}

	// The cart controller could easily be in SalesController instead of here
	@RequestMapping(CART_URL)
	public String cartController(Model model, @RequestParam(required = false) String addItem, HttpServletRequest request) throws ServletException {
		if (!SalesController.checkUser(request))
			return "forward:" + USER_WELCOME_URL;
		HttpSession session = request.getSession();
		UserBean userBean = (UserBean) session.getAttribute("user");
		Cart userCart = userBean.getCart();

		// Make a cart for this user, if they don't already have one
		if (userCart == null) {
			userCart = catalogService.createCart();
			userBean.setCart(userCart);
		}
		// get user's desired productId
		long productId = -1;
		if (addItem != null)
			productId = userBean.getProductId();
		// Add new product to cart
		if (addItem != null && productId >= 0)
			catalogService.addItemtoCart(productId, userCart, 1);
		Set<CartItemData> cartInfo = null;
		try {
			cartInfo = catalogService.getCartInfo(userCart);
			System.out.println("Cart");
			PresentationUtils.displayCart(cartInfo, System.out);
		} catch (ServiceException e) {
			System.out.println("CartController: " + e);
			throw new ServletException(e);
		}
		model.addAttribute("cartInfo", cartInfo);
		return CART_VIEW;
	}

	@RequestMapping(PRODUCT_URL)
	public String productController(Model model, @RequestParam() String productCode, HttpServletRequest request)
			throws ServletException {
		if (!SalesController.checkUser(request))
			return "forward:" + USER_WELCOME_URL;
		HttpSession session = request.getSession();
		UserBean userBean = (UserBean) session.getAttribute("user");
		Product product = null;
		try {
			product = catalogService.getProductByCode(productCode);
			userBean.setProductId(product.getId());
		} catch (ServiceException e) {
			System.out.println("ProductController: " + e);
			throw new ServletException(e);
		}
		// for convenience of product page code (could use user.product)--
		model.addAttribute("product", product);
		return PRODUCT_VIEW;
	}

	@RequestMapping(LISTEN_URL)
	public String listenController(Model model, HttpServletRequest request) throws ServletException {
		if (!SalesController.checkUser(request))
			return "forward:" + USER_WELCOME_URL;
		HttpSession session = request.getSession();
		UserBean userBean = (UserBean) session.getAttribute("user");
		long productId = userBean.getProductId();
		Product product = null;
		try {
			product = catalogService.getProduct(productId);
		} catch (ServiceException e) {
			System.out.println("ListenController: " + e);
			throw new ServletException(e);
		}
		UserData user = userBean.getUser();
		model.addAttribute("product", product); // for sound view
		// if logged-in user, go direct to Sound page (oldUserView), else go to
		// RegisterForm
		System.out.println("Returning from ListenController");
		return user != null ? SOUND_VIEW : REGISTER_FORM_VIEW;
	}
	
	// handle request coming from src=download.html in <audio> element
	@RequestMapping(DOWNLOAD_URL)
	public String downloadController( @RequestParam() int trackNum, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		System.out.println("DownloadController: starting");
		if (!SalesController.checkUser(request))
			return "forward:" + USER_WELCOME_URL;
		HttpSession session = request.getSession();
		UserBean userBean = (UserBean) session.getAttribute("user");
		UserData user = userBean.getUser();
		long productId = userBean.getProductId();
		Product product = null;
		Track track = null;
		try {
			product = catalogService.getProduct(productId);
			track = product.findTrackByNumber(trackNum);
			catalogService.addDownload(user.getEmailAddress(), track);
		} catch (ServiceException e) {
			System.out.println("DownloadController: " + e);
			throw new ServletException(e);
		}
		// Forward directly to the MP3: browser will play it
		String mp3URL = "/sound/" + product.getCode() + "/" + track.getSampleFilename();
		System.out.println("forwarding to " + mp3URL);
		return "forward:" + mp3URL; // return URL of mp3 data
	}

}
