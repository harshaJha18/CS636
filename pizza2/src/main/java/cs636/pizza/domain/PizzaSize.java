package cs636.pizza.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the PIZZA_SIZE database table.
 * The Id generator follows the example in Keith & Schincariol, pg. 74
 * Here all database attributes have fields, unlike Topping 
 * The status field is only changed inside the DAO, to keep
 * this domain object effectively invariant and excuse its
 * return to the presentation layer.
 * 
 */
@Entity(name = "PizzaSize")
@Table(name="PIZZA_SIZES")
public class PizzaSize implements Serializable, Comparable<PizzaSize> {
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name="SizeIdGen",
			table = "PIZZA_ID_GEN",
			pkColumnName = "GEN_NAME",
			valueColumnName = "GEN_VAL",
			pkColumnValue = "SizeId_Gen")				
	@GeneratedValue(generator="SizeIdGen")
	private int id;

	@Column(name="SIZE_NAME")
	private String sizeName;

    public PizzaSize() {
    }
    
	/** create PizzaSize, id is handled by JPA */
    public PizzaSize(String sizeName) {
    	this.sizeName = sizeName;
    }
    
    public int getId() {
		return this.id;
	}

	public String getSizeName() {
		return this.sizeName;
	}

	// Implement compareTo and equals so we can use TreeSet<PizzaSize>,
    // and hashCode too so we can use HashSet<PizzaSize>
    // If we used id comparison here, we would be restricted to sets of 
    // Toppings with actual ids, and ids are assigned sometime between
	// the persist inside createTopping and the commit that follows.
	// To allow sets of new and old Toppings, and since
    // we have a UNIQUE column constraint on toppingname, we
    // can use it for equality checking and comparison.
    // Here toppingName is a "business key".
    // See Bauer and King, pg. 397 for discussion of this issue.
    // Also, use getters here (for object x), in case these methods
	// are executed for a proxy in a lazy to-one relationship 
	// (not in use in the pizza project, but just in case you use 
	// this code as a model for another project that does use lazy
	// to-one relationships.) 
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