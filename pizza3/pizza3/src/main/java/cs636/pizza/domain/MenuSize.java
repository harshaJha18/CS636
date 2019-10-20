package cs636.pizza.domain;
// size available for new pizza orders
// Immutable object
public class MenuSize implements Comparable<MenuSize> {
	private int id;
	private String sizeName;

	public MenuSize(int id, String name)
	{
		this.id = id;
		this.sizeName = name;
	}
	public int getId() {
		return id;
	}

	public String getSizeName() {
		return sizeName;
	}
	
	// Implement compareTo and equals so we can use TreeSet<PizzaSize>,
	// with ordering based on sizeName ("large", "medium", "small") for ex.
    // and hashCode too so we can use HashSet<PizzaSize>
    @Override
	public int compareTo(MenuSize x)
	{
		return getSizeName().compareTo(x.getSizeName());
	}
	@Override
	public boolean equals(Object x)
	{
		if (x == null || x.getClass()!= getClass())
			return false;
		return getSizeName().equals(((MenuSize)x).getSizeName());
	}
	@Override
	public int hashCode()
	{
		return getSizeName().hashCode();
	}
}
