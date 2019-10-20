package cs636.pizza.domain;
// size for a certain pizza
// Immutable object
public class PizzaSize implements Comparable<PizzaSize> {
	private int id;
	private String sizeName;

	public PizzaSize(String name)
	{
		this.id = -1;
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
	public int compareTo(PizzaSize x)
	{
		return getSizeName().compareTo(x.getSizeName());
	}
	@Override
	public boolean equals(Object x)
	{
		if (x == null || x.getClass()!= getClass())
			return false;
		return getSizeName().equals(((PizzaSize)x).getSizeName());
	}
	@Override
	public int hashCode()
	{
		return getSizeName().hashCode();
	}
}
