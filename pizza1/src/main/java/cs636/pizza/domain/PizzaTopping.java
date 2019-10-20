package cs636.pizza.domain;

/**
 * The persistent class for the pizza topping objects for an ordered pizza
 * Each pizza "owns" its set of PizzaTopping objects
 * 
 */
// Domain object for a pizza topping used on a certain pizza
// Immutable object (no setters or other mutator methods)
// Has orderId, but not used for object navigation: note there is no reference in 
// the object to the owning PizzaOrder
// OrderId in database table implements set of toppings for a pizza order
// The topping name should correspond to MenuTopping name at time of ordering
// but later, the MenuTopping may be deleted and this order will still have that topping
public class PizzaTopping implements Comparable<PizzaTopping> {
	private int id;
	private int orderId; // the owning pizza order
	private String toppingName;
	
	public PizzaTopping(int orderId, int id, String name)
	{
		this.orderId = orderId;
		this.id = id;
		this.toppingName = name;
	}
	
	public PizzaTopping(String name)
	{
		this.orderId = -1;  // not yet known
		this.id = -1;
		this.toppingName = name;
	}
	
	public int getId() {
		return id;
	}
	
	public int getOrderId() {
		return orderId;
	}

	public String getToppingName() {
		return toppingName;
	}
	
	// so we can use TreeSet<PizzaTopping> or HashSet<PizzaTopping> any time we want--
    // "business key equality/comparison/hashCode" where business key is toppingName
    // see comments in PizzaSize
	public int compareTo(PizzaTopping x)
	{
		return getToppingName().compareTo(x.getToppingName());
	}
	@Override
	public boolean equals(Object x)
	{
		if (x == null || x.getClass()!= getClass())
			return false;
		return getToppingName().equals(((PizzaTopping)x).getToppingName());
	}
	@Override
	public int hashCode()
	{
		return getToppingName().hashCode();
	}
}
