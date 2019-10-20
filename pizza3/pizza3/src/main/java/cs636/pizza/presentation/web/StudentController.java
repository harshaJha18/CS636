package cs636.pizza.presentation.web;

import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.service.PizzaOrderData;
import cs636.pizza.service.ServiceException;
import cs636.pizza.service.StudentService;

@Controller
@SessionAttributes("student")
public class StudentController {
	@Autowired
	private StudentService studentService;

	@RequestMapping("welcome.html")
	public String welcome(Model model) {
		return "welcome";
	}

	@RequestMapping("studentWelcome.html")
	public String displayWelcome(Model model, @RequestParam(value = "room", required = false) String chosenRoom,
			HttpServletRequest request) throws ServletException {
		// At studentWelcome, the user gets a StudentBean.
		// If it's not there for subsequent pages, hand the request to
		// studentWelcome. Having the bean is like being "logged in".
		// boolean hasBean = (session.getAttribute("student") != null);
		Integer roomNo = null;
		// take room parameter over session var in StudentBean--

		if (chosenRoom != null) {
			try {
				roomNo = Integer.parseInt(chosenRoom);
				System.out.println("Got roomNo from param = " + roomNo);
			} catch (NumberFormatException e) {
				// if get here, it's a bug: user can't directly enter room no.
				System.out.println("studentWelcome: bad number format in room");
				throw new ServletException("Bad room number");
			}
		}

		StudentBean student = (StudentBean) request.getSession().getAttribute("student");
		if (student == null)
			student = new StudentBean();
		if (roomNo != null)
			student.setRoomNo(roomNo); // set newly obtained roomNo
		if (student.getRoomNo() > 0)
			roomNo = student.getRoomNo(); // just set or older setting
		request.getSession().setAttribute("student", student);

		Set<String> allSizes = null;
		Set<String> allToppings = null;
		try {
			allSizes = studentService.getSizeNames();
			allToppings = studentService.getToppingNames();
		} catch (Exception e) {
			System.out.println("exception in displayWelcome " + e);
			throw new ServletException("failed to access DB", e);
		}

		List<PizzaOrderData> report = null;
		Boolean hasBaked = false;
		try {
			System.out.println("in StudentWelcomeController pt B (isnull = " + (studentService == null));
			if (roomNo != null && roomNo > 0) {
				report = studentService.getOrderStatus(roomNo);
				System.out.println("report has " + report.size());
				if (report != null)
					for (PizzaOrderData order : report)
						if (order.getStatus() == PizzaOrderData.BAKED)
							hasBaked = true;
			}

		} catch (Exception e) {
			System.out.println("in StudentController.displayWelcome");
			System.out.println(PizzaSystemConfig.exceptionReport(e));
			throw new ServletException("failed to access DB for order status", e);
		}
		request.setAttribute("statusReport", report);
		model.addAttribute("allSizes", allSizes);
		model.addAttribute("allToppings", allToppings);
		model.addAttribute("hasBaked", hasBaked);
		model.addAttribute("numRooms", PizzaSystemConfig.NUM_OF_ROOMS);
		model.addAttribute("statusReport", report);

		return "jsp/studentWelcome"; // /jsp/studentWelcome.jsp
	}

	@RequestMapping("orderForm.html")
	public String displayOrderForm(Model model) throws ServletException {

		Set<String> allSizes = null;
		Set<String> allToppings = null;
		try {
			allSizes = studentService.getSizeNames();
			allToppings = studentService.getToppingNames();
		} catch (Exception e) {
			System.out.println("exception in displayWelcome " + e);
			throw new ServletException("failed to access DB for names, toppings", e);
		}
		System.out.println("#sizes = " + allSizes.size());
		model.addAttribute("allSizes", allSizes);
		model.addAttribute("allToppings", allToppings);
		model.addAttribute("numRooms", 10);
		return "jsp/orderForm";
	}

	@RequestMapping("orderPizza.html")
	public String orderPizza(Model model, @RequestParam(value = "size", required = false) String chosenSize,
			@RequestParam(value = "room", required = false) String chosenRoom,
			@RequestParam(required = false) Set<String> toppings, HttpServletRequest request) throws ServletException {
		// List<String>toppings = null;
		System.out.println("Starting orderPizza");
		// this controller forwards to two places, depending
		// on whether the form data is OK, allowing a pizza order,
		// or the form needs to be filled out again by the user:
		String viewSuccess = "redirect:studentWelcome.html"; // form OK and processed, show welcome page
		String redoFormURL = "forward:/orderForm.html"; // redo form (URL back to form req)
		System.out.println("in OrderPizzaController");
		String forwardURL;
		Set<String> allSizes;
		Set<String> allToppings;
		String errorMessage = "";
		try {
			allSizes = studentService.getSizeNames();
			allToppings = studentService.getToppingNames();

			model.addAttribute("allSizes", allSizes);
			model.addAttribute("allToppings", allToppings);
		} catch (ServiceException e) {
			System.out.println("failed to access DB for names, toppings");
			throw new ServletException("failed to access DB for names, toppings", e);
		}

		if (allSizes.size() == 0)
			errorMessage += "No pizza sizes are now available, please try again later";
		else if (allToppings.size() == 0)
			errorMessage += "No toppings are now available, please try again later";

		if (errorMessage.length() > 0) {
			model.addAttribute("errorMessage", errorMessage);
			forwardURL = redoFormURL; // show form again
			return forwardURL;
		}

		if (chosenSize == null) {
			errorMessage = "Please choose a pizza size. ";
		}
		// Set<String> chosenToppings = null;
		if (toppings == null || toppings.size() == 0)
			errorMessage += "Please choose at least one topping. ";
		else {
			System.out.println("#toppings from form: " + toppings.size());
		}
		if (errorMessage.length() > 0) {
			model.addAttribute("errorMessage", errorMessage);
			forwardURL = redoFormURL; // show form again
		} else {
			StudentBean student = (StudentBean) request.getSession().getAttribute("student");
			if (student == null)
				return "forward:studentWelcome.html"; // get started right
			Integer roomNo = student.getRoomNo();
			if (chosenRoom != null) {
				roomNo = Integer.parseInt(chosenRoom);
				student.setRoomNo(roomNo);
			}
			roomNo = student.getRoomNo(); // just set or from before
			if (roomNo < 0) {
				// bug: UI should have forced user to choose a good roomNo
				System.out.println("OrderPizzaController: unexpected bad roomNo " + roomNo);
				throw new ServletException("unexpected bad roomNo");
			}
			// have toppings and size objects set up, and roomNo, call
			// BL to make order
			try {
				studentService.makeOrder(roomNo, chosenSize, toppings);
				forwardURL = viewSuccess; // success
			} catch (ServiceException e) {
				// almost certainly a bug, but might be a disappeared topping/size, report to
				// log--
				System.out.println("OrderPizzaController: order failed: " + e + e.getCause());
				errorMessage = "Your order failed, please try again. " + e + e.getCause();
				model.addAttribute("errorMessage", errorMessage);
				// let user have another chance at it (maybe useless)
				forwardURL = redoFormURL;
			}
		}
		System.out.println("forwarding to " + forwardURL);
		return forwardURL;
	}

	@RequestMapping("orderReceive.html")
	public String receivePizza(Model model, HttpServletRequest request) throws ServletException {

		System.out.println("in receivePizza");
		HttpSession session = request.getSession();
		StudentBean student = (StudentBean) session.getAttribute("student");
		Integer roomNo = student.getRoomNo();
		System.out.println("in orderReceive, roomNo = " + roomNo);
		if (roomNo == null || roomNo == 0) {
			// this is a bug
			System.out.println("OrderStatus Controller: no room number in receive info ");
		}

		try {
			studentService.receiveOrders(roomNo);
		} catch (ServiceException e) {
			System.out.println("Exception in receivePizza Controller: " + e);
		}
		return "forward:studentWelcome.html";
	}
}
