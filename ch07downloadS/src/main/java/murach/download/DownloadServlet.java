package murach.download;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import murach.business.User;
import murach.data.UserIO;

// handle /error to capture HTTP 404s, etc. forwarded from Spring's servlet
// as well as errors generated in this code
@WebServlet(name = "DownloadServlet", description = "Servlet to handle downloads", urlPatterns = {"/download", "/error"})
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
		String uri = request.getRequestURI();
		System.out.println("doGet from URI " + uri);
			
		System.out.println("doGet: action = " + request.getParameter("action"));
		System.out.println("doGet: productCode = " + request.getParameter("productCode"));
        // get current action
        String action = request.getParameter("action");
        if (action == null) {
            action = "viewAlbums";  // default action
        }

        // perform action and set URL to appropriate page
        String url = null;
        if (uri.equals("/error")) {
        	// forwarded from Spring, so response has the HTTP status code 
        	int code = response.getStatus();
        	System.out.println("in /error handler, code = "+code);
        	request.setAttribute("error", "error forwarded from Spring, HTTP status code = "+code);
			url = "/error.jsp";
		} else if (action.equals("viewAlbums")) {
            url = "/index.html";
        } else if (action.equals("showProduct")) {
            url = showProduct(request, response);
        } else if (action.equals("viewCookies")) {  // remaining 4 actions added to code on pg.235
            url = "/view_cookies.jsp";
        } else if (action.equals("deleteCookies")) {
            url = deleteCookies(request, response);
        } else if (action.equals("logout")) {
        	url = logout(request, response);
        } else if (action.equals("serveMp3")) {
        	url = serveMp3(request, response);
        }
        // if url is null, error response is already set in the response
		if (url != null) {
			// forward to the view
			getServletContext().getRequestDispatcher(url).forward(request,
					response);
		}
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
    	System.out.println("doPost from URI " + request.getRequestURI());
        String action = request.getParameter("action");
        
        // perform action and set URL to appropriate page
        String url = "/index.jsp";
        if (action.equals("registerUser")) {
            url = registerUser(request, response);
        }
        System.out.println("doPost forwarding to " + url);
        // forward to the view
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }
    
    // new top-level method for showing the product page,
    // now separated from checking that the user is logged in
    // (checkUser below). checkUser is also used from the new 
    // serveMp3 top-level action
	private String showProduct(HttpServletRequest request,
			HttpServletResponse response) {
		String productCode = request.getParameter("productCode");
		HttpSession session = request.getSession();
		String url = null;
		try {
			if (checkUser(request)) {
				// user is logged in, can see product page
				url = determineProductView(request, productCode);
			} else { // need to log in
				// remember chosen productCode for later
				session.setAttribute("productCode", productCode);
				url = "/register.jsp";
			}
		} catch (IOException e) {
			request.setAttribute("error", "error accessing user: " + e);
			url = "/error.jsp";
		}
		return url;
	}
        
    // check if user is logged in 
	// for this implementation, just check for session variable
	private boolean checkUser(HttpServletRequest request) throws IOException {
	 HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		return (user != null);
	}
	
	// save new user info
    private String registerUser(HttpServletRequest request,
            HttpServletResponse response) {

        // get the user data
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String url = null;
        // store the data in a User object
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        // write the User object to a file
        ServletContext sc = getServletContext();
        String path = sc.getRealPath("/WEB-INF/EmailList.txt");
        try {
        	UserIO.add(user, path);
        } catch (IOException e) {
          	request.setAttribute("error", "error adding user: " + e);
        	url = "/" + "error.jsp";
        	return url;
        }
        // store the User object as a session attribute
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
    
        // add a cookie that stores the user's email to browser
        Cookie c = new Cookie("emailCookie", email);
        c.setMaxAge(60 * 60 * 24 * 365 * 2); // set age to 2 years
        c.setPath("/");                      // allow entire app to access it
        response.addCookie(c);

        // create and return a URL for the appropriate Download/Product page
        String productCode = (String) session.getAttribute("productCode");
        url = determineProductView(request, productCode);
        return url;
    }

    // eoneil/cs636: Show how to use HTML5 audio element, serve out mp3,
    // by recoding two cases here (product codes 8601 and pf01)
	String determineProductView(HttpServletRequest request, String productCode) {
		String url = null;
		if (productCode.equals("pf01")) {
			request.setAttribute("title", "Whiskey Before Breakfast");
			// make browser use servlet url for mp3 file contents
			// using this approach, we can keep its contents under access
			// control. Request execution ends up in serveMp3 below.
			request.setAttribute("src",
					"download?productCode=pf01&amp;mp3Name=whiskey.mp3&amp;action=serveMp3");
			url = "/universal.jsp"; // HTML5 page with url back to servlet for
									// mp3 data
		} else {
			// Murach's approach: have a JSP for each CD, used here for pf02 and
			// jr01. Also support ${productCode} for JSPs
			// 8601: use HTML5 <audio> here
			url = "/" + productCode + "_download.jsp";
			request.setAttribute("productCode", productCode);
		}
		return url;
	}
	
    // added by eoneil
    // Serve out an mp3 file rather than have browser access it as a file.
    // To use this, make the <audio> src= point to a servlet URL, for example, src =
    // "/download?productCode=pf01&amp;mp3Name=whiskey.mp3&amp;action=serveMp3"
    // If the actual file is put under WEB-INF, browsers can't access it directly.
    // Returns url of mp3 file to forward to, or null if (error) response already set 
	private String serveMp3(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String url = null;
		String productCode = request.getParameter("productCode");
		String mp3Name = request.getParameter("mp3Name");
		if (productCode == null || mp3Name == null) {
			// send HTTP 400 for bad request
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			System.out.println("serveMp3 failing with 400");
			return null; // no forward to do
		}

		if (checkUser(request)) {
			// user is logged in, can access content
			url = "/sound/" + productCode + "/" + mp3Name;
		} else { // not logged in, can't access content
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			System.out.println("serveMp3 failing with 401");
			return null; // no forward to do
		}
		RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
		System.out.println("rd = " + rd );
		RequestDispatcher rd1 = getServletContext().getRequestDispatcher(url);
		System.out.println("rd1 = " + rd1);
		if (rd == rd1)
			System.out.println("RD for mp3 is default servlet RD");
		System.out.println("serveMp3 forwarding to " + url);
		return url;
	}

    private String deleteCookies(HttpServletRequest request,
            HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0); //delete the cookie
            cookie.setPath("/"); //allow the download application to access it
            response.addCookie(cookie);
        }
        String url = "/delete_cookies.jsp";
        return url;
    }
    
    // added by eoneil: logout the user
    private String logout(HttpServletRequest request,
            HttpServletResponse response) {
    	request.getSession().invalidate();  // drop session
		return "/logout.jsp"; 
    }
}