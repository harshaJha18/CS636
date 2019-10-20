package cs636.pizza.domain;
// Domain object for a pizza topping offered for new pizzas
// Immutable object, with id and toppingName properties

public class MenuTopping implements Comparable<MenuTopping> {
	private int id;
	private String name;
	
	public MenuTopping(int id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public String getToppingName() {
		return name;
	}
	
	// so we can use TreeSet<Topping> or HashSet<Topping> any time we want--
    // "business key equality/comparison/hashCode" where business key is toppingName
    // see comments in PizzaSize
	public int compareTo(MenuTopping x)
	{
		return getToppingName().compareTo(x.getToppingName());
	}
	@Override
	public boolean equals(Object x)
	{
		if (x == null || x.getClass()!= getClass())
			return false;
		return getToppingName().equals(((MenuTopping)x).getToppingName());
	}
	@Override
	public int hashCode()
	{
		return getToppingName().hashCode();
	}
}
