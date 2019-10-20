package cs636.pizza.domain;

/**
 * @author Saaid Baraty
 *
 * Domain object for Pizza Order. Represents a pizza order in the service
 * and DAO layers. The invariant PizzaOrderData object holds the same attributes
 * For use in the presentation layer.
 */

import java.util.Set;
import java.util.TreeSet;
	
public class PizzaOrder {

	private int roomNumber;
	private PizzaSize pizzaSize;
	private Set<PizzaTopping> pizzaToppings;
	private int id;
	private int day;	
	private int status;	
	
	// pizza order status values--
	public static final int PREPARING = 1;
	public static final int BAKED = 2;
	public static final int FINISHED = 3;
	public static final int NO_SUCH_ORDER = 0;
	private static final String[] STATUS_NAME = { "NO_SUCH_ORDER", 
        "PREPARING", 
        "BAKED",
        "FINISHED"
        };  
	
	// for use in creating new PizzaOrder object, id not yet specified
	public PizzaOrder(int roomNumber, PizzaSize size, Set<PizzaTopping> PizzaToppings, int day, int status) {		
		this(-1, roomNumber, size, PizzaToppings, day, status);
	}
	// for use in creating PizzaOrder object from database data
	public PizzaOrder( int id, int roomNumber, PizzaSize size, Set<PizzaTopping> pizzaToppings, int day, int status) {
		
		this.roomNumber = roomNumber;
		this.pizzaSize = size;
		this.id = id;
		this.day = day;
		this.status = status;
		this.pizzaToppings = pizzaToppings;
	}
	
	// mutators, AKA "commands": the application-specific ways to change a PizzaOrder
	public void makeReady()
	{
		status = BAKED;
	}
	
	// Student acknowledges receipt of pizza, finishing the delivery
	public void receive()
	{
		assert(status == BAKED);
		status = FINISHED;
	}

	public void finish()
	{
		status = FINISHED;
	}
	// convenience method for building a PizzaOrder
	public void addPizzaTopping(PizzaTopping PizzaTopping)
	{
		pizzaToppings.add(PizzaTopping);
	}
	// only used in DAO--
	public void setId(int id)
	{
		this.id = id;
	}
	
	// Getters.  Note there are no corresponding setters--
	// Changes to PizzaOrders are done by the "commands" above
	public Set<PizzaTopping> getPizzaToppings()
	{
		return pizzaToppings;
	}
	
	public Set<String> getPizzaToppingNames()
	{
		Set<String> names = null;
		if (pizzaToppings != null) {
			names = new TreeSet<String>();
			for (PizzaTopping t : pizzaToppings) {
				names.add(t.getToppingName());
			}
		}
		return names;
	}
	
	public boolean containsPizzaTopping(PizzaTopping PizzaTopping)
	{
		return pizzaToppings.contains(PizzaTopping); 
	}
		
	public int getDay() {
		return day;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public PizzaSize getPizzaSize() 
	{
		return pizzaSize;
	}

	// This is only used (outside this source) by the DAO, to persist a given order
	public int getId() {
		return id;
	}
	
	public int getStatus() {
		return status;
	}

	// string equivalent of status code
	public String statusString()
	{
		return STATUS_NAME[status];
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ORDER ID: " + getId() + "\n");
		buffer.append("ORDER DAY: " + getDay() + "\n");
		if (getPizzaSize() != null)   // some orders are fetched without details
			buffer.append("SIZE: " + getPizzaSize().getSizeName() + "\n");
		if (getPizzaToppings() != null) {
			buffer.append("PizzaToppingS: ");
			for (PizzaTopping t: getPizzaToppings()) {
				buffer.append(t.getToppingName());
				buffer.append(" ");
			}
			buffer.append("\n");
		}
		buffer.append("ROOM NUMBER: " + getRoomNumber() + "\n");
		buffer.append("STATUS: " + statusString());
		return buffer.toString();
	}
	
	// Note: no compareTo, so can't use TreeSet<PizzaOrder>, just HashSet<PizzaOrder>
	// or List<PizzaOrder>. 
	// Here equals and hashCode are not overridden, 
	// so simple object equality holds, based on object memory addresses.
	// Thus two PizzaOrder objects o1 and o2 are equal, i.e.,
	// o1.equals(o2) is true, if and only if o1 and o2 
	// are the same object, so o1 == o2.
}
